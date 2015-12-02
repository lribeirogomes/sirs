package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import org.spongycastle.util.Arrays;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
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

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SmsMessage {
    private String _id;
    private long _dateNumber;
    private Contact _contact;
    private String _content;

    public SmsMessage(String id, Contact contact, long dateNumber, String content) {
        _id = id;
        _contact = contact;
        _dateNumber = dateNumber;
        _content = content;
    }

    public String getId() { return _id; }

    public Contact getContact() {return _contact; }

    public Date getDate() { return new Date(_dateNumber * 1000); }

    public String getContent() { return _content; }

    public byte[] getEncryptedContent() throws FailedToEncryptSmsMessageException {
        /*Reminder: we are doing sign and then cipher so unfortunately surreptitious forwarding can happen*/
        //TODO: Check the size of the message to see if it fits in an sms
        try {
            Session session = SessionManager.retrieve(_contact);
            session.incrementMySequenceNumber();

            byte[] encodedData = Cryptography.encode(_content);

            byte[] seqNum = new byte[1];
            seqNum[0] = session.getMySequenceNumber();
            byte[] messageToSign = Arrays.concatenate(seqNum, encodedData);

            KeyManager km = KeyManager.getInstance();
            PrivateKey myPrivateKey = km.getMySigningPrivateKey();
            byte[] signature = Cryptography.sign(messageToSign, myPrivateKey);

            if(messageToSign.length > Byte.MAX_VALUE)
                throw new FailedToEncryptSmsMessageException();

            byte[] messageLength = new byte[1];
            messageLength[0] = (byte)messageToSign.length;

            byte[] plaintext = Arrays.concatenate(messageLength, messageToSign, signature);

            SecretKey sessionKey = session.getSessionKey();
            byte[] cipheredData = Cryptography.symmetricCipher(plaintext, sessionKey);

            SessionManager.update(_contact, session);

            return cipheredData;

        }catch (FailedToRetrieveSessionException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToSignException
                | FailedToEncryptException
                |FailedToUpdateSessionException e){
            throw new FailedToEncryptSmsMessageException();

        }
    }

    public static String decrytpContent(Contact contact, byte[] cipheredMessage)throws FailedToDecryptSmsMessageException{
        try{
            Session session = SessionManager.retrieve(contact);
            SecretKey sessionKey = session.getSessionKey();

            byte[] plaintext = Cryptography.symmetricDecipher(cipheredMessage, sessionKey);

            byte messageLength = plaintext[0];
            byte[] message = Arrays.copyOfRange(plaintext, 1, messageLength+1);
            byte[] signature = Arrays.copyOfRange(plaintext, messageLength+1, plaintext.length);

            KeyManager km = KeyManager.getInstance();
            PublicKey contactPublicKey = km.getContactSigningPublicKey(contact.getPhoneNumber());

            Cryptography.verifySignature(message, signature, contactPublicKey);

            session.incrementContactSequenceNumber();
            if(message[0] != session.getContactSequenceNumber())
                throw new FailedToDecryptSmsMessageException();

            byte[] content = Arrays.copyOfRange(message, 1, message.length);

            String textContent = Cryptography.decode(content);

            SessionManager.update(contact, session);

            return textContent;
        }catch (FailedToRetrieveSessionException
                | FailedToDecryptException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToVerifySignatureException
                | InvalidSignatureException
                | FailedToUpdateSessionException e){
            throw new FailedToDecryptSmsMessageException();
        }
    }
}
