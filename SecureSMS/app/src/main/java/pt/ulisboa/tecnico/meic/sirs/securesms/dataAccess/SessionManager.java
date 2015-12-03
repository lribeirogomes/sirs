package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.util.Base64;

import org.spongycastle.util.Arrays;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAcknowledgeSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToDeleteSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateSessionRequestException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToUpdateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSignException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToVerifySignatureException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.InvalidSignatureException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SessionManager {
    private static final String SESSION = "SESSION";
    private static final String MY_SEQUENCE_NUMBER = "mySeqNum";
    private static final String CONTACT_SEQUENCE_NUMBER = "contactSeqNum";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";
    private static final String PARTIAL_REQUEST = "partialRequest";

    public static Session create(Contact contact) throws FailedToCreateSessionException {
        try {
            //delete any previous sessions
            delete(contact);

            KeyManager km = KeyManager.getInstance();
            SecretKey sessionKey = km.generateNewSessionKey(contact.getPhoneNumber());

            Session newSession = new Session(sessionKey);

            //store the session
            storeSession(newSession, contact);

            return newSession;
        } catch (KeyStoreIsLockedException
                | FailedToGenerateKeyException
                | FailedToLoadDataBaseException
                | FailedToAddAttributeException
                | FailedToDeleteSessionException
                | FailedToStoreException e) {
            throw new FailedToCreateSessionException("Failed to create a new session");
        }
    }

    private static void storeSession(Session session, Contact contact) throws FailedToLoadDataBaseException, FailedToAddAttributeException {
        DataManager dm = DataManager.getInstance();
        String contactId = contact.getId();
        dm.setAttribute(contactId + SESSION, MY_SEQUENCE_NUMBER, Byte.toString(session.getMySequenceNumber()));
        dm.setAttribute(contactId + SESSION, CONTACT_SEQUENCE_NUMBER, Byte.toString(session.getContactSequenceNumber()));
        dm.setAttribute(contactId + SESSION, STATUS, session.getStatus().ordinal());
        dm.setAttribute(contactId + SESSION, TIMESTAMP, session.getTimestamp());
    }

    public static Session retrieve(Contact contact) throws FailedToRetrieveSessionException {
        try {
            KeyManager km = KeyManager.getInstance();
            SecretKey sessionKey = km.getSessionKey(contact.getPhoneNumber());

            //retrieve the rest of the session
            DataManager dm = DataManager.getInstance();
            String contactId = contact.getId();
            byte mySequenceNumber;
            byte contactSequenceNumber;
            int storedStatus;
            int timestamp;
            try {
                mySequenceNumber = Byte.parseByte(dm.getAttributeString(contactId + SESSION, MY_SEQUENCE_NUMBER));
                contactSequenceNumber = Byte.parseByte(dm.getAttributeString(contactId + SESSION, CONTACT_SEQUENCE_NUMBER));
                storedStatus = dm.getAttributeInt(contactId + SESSION, STATUS);
                timestamp = dm.getAttributeInt(contactId + SESSION, TIMESTAMP);
            } catch (NumberFormatException e) {
                throw new FailedToRetrieveSessionException("Not in storage");
            }
            Session.Status status = Session.Status.values()[storedStatus];
            Session newSession = new Session(sessionKey, mySequenceNumber, contactSequenceNumber, timestamp, status);

            return newSession;
        } catch (KeyStoreIsLockedException
                | FailedToLoadDataBaseException
                | FailedToGetAttributeException
                | FailedToRetrieveKeyException e) {
            throw new FailedToRetrieveSessionException("Failed to retrieve the session");
        }
    }

    public static void update(Contact contact, Session session) throws FailedToUpdateSessionException {
        try {
            storeSession(session, contact);
        } catch (FailedToAddAttributeException
                | FailedToLoadDataBaseException e) {
            throw new FailedToUpdateSessionException("Failed to update session");
        }
    }

    public static void delete(Contact contact) throws FailedToDeleteSessionException {
        try {
            KeyManager km = KeyManager.getInstance();
            km.removeSessionKey(contact.getPhoneNumber());

            DataManager dm = DataManager.getInstance();
            dm.cleanAttribute(contact.getId() + SESSION);
        } catch (KeyStoreIsLockedException
                | FailedToRemoveKeyException
                | FailedToLoadDataBaseException e) {
            throw new FailedToDeleteSessionException("Failed to delete a session");
        }
    }

    public static byte[] generateSessionRequest(Contact contact) throws FailedToGenerateSessionRequestException {
        try {
            Session session = retrieve(contact);

            //Generate the message that goes in the KEK
            byte[] sessionKey = session.getSessionKey().getEncoded();
            byte[] mySeqNumber = new byte[1];
            mySeqNumber[0] = session.getMySequenceNumber();
            byte[] timestamp = ByteBuffer.allocate(4).putInt(session.getTimestamp()).array();
            byte[] message = Arrays.concatenate(sessionKey, timestamp, mySeqNumber);

            //Sign that data
            KeyManager km = KeyManager.getInstance();
            PrivateKey myPrivateKey = km.getMySigningPrivateKey();
            byte[] signature = Cryptography.sign(message, myPrivateKey);

            //Join the data and the signature
            if (message.length > Byte.MAX_VALUE)//Check if the length fits in one byte
                throw new FailedToGenerateSessionRequestException("Message is too long");
            byte[] messageSize = new byte[1];
            messageSize[0] = (byte) message.length;
            byte[] plaintext = Arrays.concatenate(messageSize, message, signature);

            //Cipher it with the contacts public key
            PublicKey contactsPublicKey = km.getContactEncryptionPublicKey(contact.getPhoneNumber());
            byte[] kek = Cryptography.asymmetricCipher(plaintext, contactsPublicKey);

            return kek;

        } catch (FailedToRetrieveSessionException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToEncryptException
                | FailedToSignException e) {
            throw new FailedToGenerateSessionRequestException("Failed to generate the session request message");
        }

    }

    private static Session processSessionRequest(Contact contact, byte[] kek) throws FailedToCreateSessionException {
        try {
            final int SEQ_NUM_LENGTH = 1;
            final int TIMESTAMPT_LENGTH = 4;

            //Decipher the kek
            KeyManager km = KeyManager.getInstance();
            PrivateKey myPrivateKey = km.getMyEncryptionPrivateKey();
            byte[] plaintext = Cryptography.asymmetricDecipher(kek, myPrivateKey);

            //Split it up
            byte messageLength = plaintext[0];
            byte[] message = Arrays.copyOfRange(plaintext, 1, messageLength + 1);
            byte[] signature = Arrays.copyOfRange(plaintext, messageLength + 1, plaintext.length);

            //Verify the signature
            PublicKey contactsPublicKey = km.getContactSigningPublicKey(contact.getPhoneNumber());
            Cryptography.verifySignature(message, signature, contactsPublicKey);

            //Extract the session key
            byte[] encodedSessionKey = Arrays.copyOfRange(message, 0, messageLength - SEQ_NUM_LENGTH - TIMESTAMPT_LENGTH);
            SecretKey sessionKey = km.importSessionKey(encodedSessionKey, contact.getPhoneNumber());

            //Create the session
            byte sequenceNumber = message[messageLength - SEQ_NUM_LENGTH];
            byte[] timestamp = Arrays.copyOfRange(message, messageLength - SEQ_NUM_LENGTH - TIMESTAMPT_LENGTH, messageLength - SEQ_NUM_LENGTH);
            Session session = new Session(sessionKey, sequenceNumber, ByteBuffer.wrap(timestamp).getInt());

            //Check if it is valid
            if (session.hasExpired())
                throw new FailedToCreateSessionException("This session is not valid (timestamps)");

            //Store it
            storeSession(session, contact);

            return session;

        } catch (KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToDecryptException
                | FailedToVerifySignatureException
                | InvalidSignatureException
                | FailedToLoadDataBaseException
                | FailedToAddAttributeException
                | FailedToStoreException e) {
            throw new FailedToCreateSessionException("Failed to create a session from the request");
        }

    }

    public static byte[] generateSessionAcknowledge(Contact contact) throws FailedToAcknowledgeSessionException {
        try {
            Session session = retrieve(contact);

            byte[] sequenceNumbers = new byte[2];
            sequenceNumbers[0] = session.getMySequenceNumber();
            sequenceNumbers[1] = session.getContactSequenceNumber();

            KeyManager km = KeyManager.getInstance();
            PrivateKey myPrivateKey = km.getMySigningPrivateKey();

            byte[] signature = Cryptography.sign(sequenceNumbers, myPrivateKey);

            byte[] message = Arrays.concatenate(sequenceNumbers, signature);

            byte[] cipheredMessage = Cryptography.symmetricCipher(message, session.getSessionKey());

            return cipheredMessage;
        } catch (FailedToRetrieveSessionException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToSignException
                | FailedToEncryptException e) {
            throw new FailedToAcknowledgeSessionException("Failed to generate a session acknowledgement");
        }
    }

    private static Session processSessionAcknowledge(Contact contact, byte[] ack) throws FailedToAcknowledgeSessionException {
        try {
            Session session = retrieve(contact);

            //Decipher it
            byte[] message = Cryptography.symmetricDecipher(ack, session.getSessionKey());

            //Split it
            byte[] sequenceNumbers = Arrays.copyOfRange(message, 0, 2);
            byte[] signature = Arrays.copyOfRange(message, 2, message.length);

            //Validate its signature
            KeyManager km = KeyManager.getInstance();
            PublicKey contactPublicKey = km.getContactSigningPublicKey(contact.getPhoneNumber());
            Cryptography.verifySignature(sequenceNumbers, signature, contactPublicKey);

            if (sequenceNumbers[1] != session.getMySequenceNumber())
                throw new FailedToAcknowledgeSessionException("Sequence numbers dont match");

            //Update the session
            session.setContactSequenceNumber(sequenceNumbers[0]);
            session.setEstablished();
            update(contact, session);

            return session;
        } catch (FailedToRetrieveSessionException
                | FailedToDecryptException
                | KeyStoreIsLockedException
                | FailedToRetrieveKeyException
                | FailedToVerifySignatureException
                | InvalidSignatureException
                | FailedToUpdateSessionException e) {
            throw new FailedToAcknowledgeSessionException("Failed to process the received session acknowledgement");
        }
    }

    public static void receiveRequestSMS(Contact contact, byte[] sms) throws FailedToCreateSessionException {
        try {
            DataManager dm = DataManager.getInstance();
            String contactId = contact.getId();

            try {
                byte[] completeMessage = {};

                byte[] previousPart = Base64.decode(dm.getAttributeString(contactId + SESSION, PARTIAL_REQUEST), Base64.DEFAULT);
                if ((int) previousPart[0] > Session.Status.values().length)
                    throw new FailedToCreateSessionException("Bad previous message");
                SmsMessage.Type type = SmsMessage.Type.values()[previousPart[0]];

                previousPart = Arrays.copyOfRange(previousPart, 1, previousPart.length);

                byte[] newPart = Arrays.copyOfRange(sms, 1, sms.length);
                if (SmsMessage.Type.RequestFirstSMS == type)
                    completeMessage = Arrays.concatenate(previousPart, newPart);
                else if (SmsMessage.Type.RequestSecondSMS == type)
                    completeMessage = Arrays.concatenate(newPart, previousPart);

                processSessionRequest(contact, completeMessage);

            } catch (FailedToCreateSessionException e) { //If it fails then one of the previous attempts failed to get through so store the new one
                createSession(contact, sms);
            }

        } catch (FailedToLoadDataBaseException
                | FailedToGetAttributeException e) {
            throw new FailedToCreateSessionException("Failed to process the received session request");
        }

    }

    //creating session with partial request
    //TODO: probably delete session before??
    public static void createSession(Contact contact, byte[] sms) {
        try {
            DataManager dm = DataManager.getInstance();
            String contactId = contact.getId();
            Session session = new Session();
            storeSession(session, contact);
            dm.setAttribute(contactId + SESSION, PARTIAL_REQUEST, Base64.encodeToString(sms, Base64.DEFAULT));
        } catch (FailedToLoadDataBaseException
                | FailedToAddAttributeException e) {
            //throw exception
        }
    }

    public static void receiveAcknowledgeSMS(Contact contact, byte[] sms) throws FailedToAcknowledgeSessionException {
        byte[] message = Arrays.copyOfRange(sms, 1, sms.length);
        processSessionAcknowledge(contact, message);
    }

    public static Session.Status checkSessionStatus(Contact contact) {
        Session.Status status;
        try {
            Session session = retrieve(contact);
            if (session.hasExpired()) {
                delete(contact);
                return Session.Status.NonExistent;
            }
            return  session.getStatus();
        } catch (FailedToRetrieveSessionException | FailedToDeleteSessionException e) {
            status = Session.Status.NonExistent;
            return status;
        }
    }
}