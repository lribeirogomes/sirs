package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToDeleteSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToUpdateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SessionManager {
    private final String SESSION = "SESSION";
    private final String MY_SEQUENCE_NUMBER = "mySeqNum";
    private final String CONTACT_SEQUENCE_NUMBER = "contactSeqNum";

    public Session create(Contact contact)throws FailedToCreateSessionException{
        try {
            KeyManager km = KeyManager.getInstance();
            SecretKey sessionKey = km.generateNewSessionKey(contact.getPhoneNumber());


            Session newSession = new Session(sessionKey);

            //store the session
            storeSession(newSession, contact);

            return newSession;
        }catch(KeyStoreIsLockedException
                | FailedToGenerateKeyException
                | FailedToLoadDataBaseException
                | FailedToAddAttributeException
                | FailedToStoreException e){
            throw new FailedToCreateSessionException("Failed to create a new session");
        }
    }

    private void storeSession(Session session, Contact contact)throws FailedToLoadDataBaseException, FailedToAddAttributeException{
        DataManager dm = DataManager.getInstance();
        String contactId = contact.getId();
        dm.setAttribute(contactId + SESSION, MY_SEQUENCE_NUMBER, Byte.toString(session.getMySequenceNumber()));
        dm.setAttribute(contactId + SESSION, CONTACT_SEQUENCE_NUMBER, Byte.toString(session.getContactSequenceNumber()));
        //TODO: Add the timestamp
    }
    public Session retrieve(Contact contact)throws FailedToRetrieveSessionException{
        try {
            KeyManager km = KeyManager.getInstance();
            SecretKey sessionKey = km.getSessionKey(contact.getPhoneNumber());

            //retrieve the rest of the session
            DataManager dm = DataManager.getInstance();
            String contactId = contact.getId();
            byte mySequenceNumber = Byte.parseByte(dm.getAttributeString(contactId + SESSION, MY_SEQUENCE_NUMBER));
            byte contactSequenceNumber = Byte.parseByte(dm.getAttributeString(contactId + SESSION, CONTACT_SEQUENCE_NUMBER));
            //TODO: Add the timestamp

            Session newSession = new Session(sessionKey, mySequenceNumber, contactSequenceNumber);

            return newSession;
        }catch(KeyStoreIsLockedException
                | FailedToLoadDataBaseException
                | FailedToGetAttributeException
                | FailedToRetrieveKeyException e){
            throw new FailedToRetrieveSessionException("Failed to retrieve the session");
        }
    }
    public void update(Contact contact, Session session)throws FailedToUpdateSessionException{
        try{
            storeSession(session, contact);
        }catch(FailedToAddAttributeException
                | FailedToLoadDataBaseException e){
            throw new FailedToUpdateSessionException("Failed to update session");
        }
    }
    public void delete(Contact contact)throws FailedToDeleteSessionException {
        try {
            KeyManager km = KeyManager.getInstance();
            km.removeSessionKey(contact.getPhoneNumber());

            DataManager dm = DataManager.getInstance();
            dm.cleanAttribute(contact.getId() + SESSION);
        }catch(KeyStoreIsLockedException
                |FailedToRemoveKeyException
                | FailedToLoadDataBaseException e){
            throw new FailedToDeleteSessionException("Failed to delete a session");
        }
    }
}
