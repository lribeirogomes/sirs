package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import org.spongycastle.util.Arrays;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToUpdateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSignException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToVerifySignatureException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.InvalidSignatureException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.SMSSizeExceededException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SmsMessage {
    public enum Type {
        RequestFirstSMS,
        RequestSecondSMS,
        Acknowledge,
        Text
    }

    public enum Direction {
        SENT,
        RECEIVED
    }

    private String _id;
    private long _dateNumber;
    private Contact _contact;
    private String _content;
    private byte[] _cipheredMessage;
    private String _cipheredEncodedMessage;

    //normal sms
    public SmsMessage(String id, Contact contact, long dateNumber, String content) {
        _id = id;
        _contact = contact;
        _dateNumber = dateNumber;
        _content = content;
    }

    //to decrypt when its received
    public SmsMessage(Contact contact, byte[] cipheredMessage) {
        _contact = contact;
        _cipheredMessage = cipheredMessage;
    }

    //to decrypt when it comes from storage
    public SmsMessage(Contact contact, String cipheredMessage) {
        _contact = contact;
        _cipheredEncodedMessage = cipheredMessage;
    }

    public String getId() {
        return _id;
    }

    public Contact getContact() {
        return _contact;
    }

    public Date getDate() {
        return new Date(_dateNumber * 1000);
    }

    public String getContent() {
        return _content;
    }

    public String encryptToStore(SecretKey key, byte[] iv) throws FailedToEncryptSmsMessageException {
        try {
            return Cryptography.encodeForStorage(Cryptography.symmetricCipherWithPKCS5(
                    Cryptography.encode(_content),
                    key,
                    iv));
        } catch (Exception exception) {
            throw new FailedToEncryptSmsMessageException();
        }
    }

    public String decryptFromStorage(SecretKey key, byte[] iv) throws FailedToDecryptSmsMessageException {
        try {
            return Cryptography.decode(Cryptography.symmetricDecipherWithPKCS5(
                    Cryptography.decodeFromStorage(_cipheredEncodedMessage),
                    key,
                    iv));
        } catch (Exception exception) {
            throw new FailedToDecryptSmsMessageException();
        }
    }

    public byte[] encryptToSend() throws FailedToEncryptSmsMessageException, SMSSizeExceededException{
        /*Reminder: we are doing sign and then cipher so unfortunately surreptitious forwarding can happen*/
        final int DATA_SMS_MAX_LENGTH = 133;
        try {
            Session session = SessionManager.retrieve(_contact);
            session.incrementMySequenceNumber();

            //Encode the text message
            byte[] encodedData = Cryptography.encode(_content);

            //Prepare the message
            byte[] seqNum = new byte[1];
            seqNum[0] = session.getMySequenceNumber();
            byte[] messageToSign = Arrays.concatenate(seqNum, encodedData);

            //Sign it
            KeyManager km = KeyManager.getInstance();
            PrivateKey myPrivateKey = km.getMySigningPrivateKey();
            byte[] signature = Cryptography.sign(messageToSign, myPrivateKey);

            //Add the byte with structure info
            if (messageToSign.length > Byte.MAX_VALUE)
                throw new FailedToEncryptSmsMessageException();
            byte[] messageLength = new byte[1];
            messageLength[0] = (byte) messageToSign.length;

            //Concatenate the parts
            byte[] plaintext = Arrays.concatenate(messageLength, messageToSign, signature);

            //Cipher it
            SecretKey sessionKey = session.getSessionKey();
            byte[] cipheredData = Cryptography.symmetricCipherWithCTS(plaintext, sessionKey);

            //Add the byte with message type
            byte[] type = new byte[1];
            type[0] = (byte) Type.Text.ordinal();
            byte[] finalMessage = Arrays.concatenate(type, cipheredData);

            if(finalMessage.length > DATA_SMS_MAX_LENGTH)
                throw new SMSSizeExceededException("Message is too long");

            //Update the session
            SessionManager.update(_contact, session);

            return finalMessage;

        } catch (FailedToRetrieveSessionException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToSignException
                | FailedToEncryptException
                | FailedToUpdateSessionException e) {
            throw new FailedToEncryptSmsMessageException();

        }
    }

    public String decryptFromReceive() throws FailedToDecryptSmsMessageException {
        try {
            Session session = SessionManager.retrieve(_contact);
            SecretKey sessionKey = session.getSessionKey();

            //Get rid of the type byte
            _cipheredMessage = Arrays.copyOfRange(_cipheredMessage, 1, _cipheredMessage.length);

            //Decipher with the session key
            byte[] plaintext = Cryptography.symmetricDecipherWithCTS(_cipheredMessage, sessionKey);

            //Split it up
            byte messageLength = plaintext[0];
            byte[] message = Arrays.copyOfRange(plaintext, 1, messageLength + 1);
            byte[] signature = Arrays.copyOfRange(plaintext, messageLength + 1, plaintext.length);

            //Verify the signature
            KeyManager km = KeyManager.getInstance();
            PublicKey contactPublicKey = km.getContactSigningPublicKey(_contact.getPhoneNumber());
            Cryptography.verifySignature(message, signature, contactPublicKey);

            //Check the sequence number
            session.incrementContactSequenceNumber();
            if (message[0] != session.getContactSequenceNumber())
                throw new FailedToDecryptSmsMessageException();

            //Get the actual message and decode it
            byte[] content = Arrays.copyOfRange(message, 1, message.length);
            String textContent = Cryptography.decode(content);

            //Update the session
            SessionManager.update(_contact, session);

            return textContent;
        } catch (FailedToRetrieveSessionException
                | FailedToDecryptException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToVerifySignatureException
                | InvalidSignatureException
                | FailedToUpdateSessionException e) {
            throw new FailedToDecryptSmsMessageException();
        }
    }
}