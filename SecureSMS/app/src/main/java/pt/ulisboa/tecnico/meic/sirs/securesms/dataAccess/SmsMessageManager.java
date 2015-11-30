package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SmsMessageManager {

    public static SmsMessage createSmsMessage(Contact contact, byte[] cipherText) throws FailedToCreateSmsMessageException {
        KeyManager keyManager;
        SecretKey key;
        byte[] decipheredData;
        String plainText;

        try {
            // TODO: Reimplement getInstance after implementing getInstance without arguments
            // Decrypt message content
            // keyManager = KeyManager.getInstance("dummy");
            // key = keyManager.getSessionKey(sender);
            // decipheredData = Cryptography.symmetricDecipher(cipherText, key);
            // plainText = Cryptography.decode(decipheredData);
            plainText = Cryptography.decode(cipherText);

            return createSmsMessage(contact, plainText);
        } catch //( FailedToLoadKeyStoreException
                //| FailedToRetrieveKeyException
                //| FailedToDecryptException exception ) {
                (Exception exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }

    public static SmsMessage createSmsMessage(Contact contact, String content) throws FailedToCreateSmsMessageException {
        //TODO: Set message direction
        Calendar calendar;
        long dateNumber;
        DataManager dm;
        String messageId;
        String contactId;
        int messageCount;
        SmsMessage smsMessage;

        try {
            // Get sms message information from calendar and manager
            calendar = new GregorianCalendar(TimeZone.getDefault());
            //dateNumber = calendar.getTime().getTime();
            dateNumber = calendar.getTimeInMillis();

            // Create sms message into storage
            dm = DataManager.getInstance();

            //setup message id
            contactId = contact.getId();
            messageCount = dm.getAttributeInt(contactId, dm.MESSAGE_COUNT);
            messageId = contactId + dm.MESSAGE_CLASS + messageCount;
            //increment
            dm.setAttribute(contactId, dm.MESSAGE_COUNT, ++messageCount);

            //set attributes
            dm.addAttribute(contactId, dm.MESSAGE_TABLE, messageId);
            dm.setAttribute(messageId, dm.MESSAGE_DATE_NUMBER, dateNumber);
            dm.setAttribute(messageId, dm.MESSAGE_CONTENT, content);

            smsMessage = new SmsMessage(messageId, contact, dateNumber, content);
            return smsMessage;
        } catch (FailedToLoadDataBaseException
                | FailedToGetAttributeException
                | FailedToAddAttributeException exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }

    public static List<SmsMessage> retrieveAllSmsMessages(Contact contact) throws
            FailedToRetrieveAllSmsMessagesException {
        // TODO: Implement message content encryption after fixing encoding issue
        // User user;
        // String passwordHash;
        List<SmsMessage> messages;
        Set<String> messageIds;
        DataManager dm;
        long dateNumber;
        String contactId;
        String encryptedContent;
        // TODO: Implement message content encryption after fixing encoding issue
        // byte[] encodedData,
        //        decryptedData;
        // String content;
        SmsMessage message;

        try {
            // TODO: Implement message content encryption after fixing encoding issue
            // Get password hash from user
            // user = User.getInstance();
            // passwordHash = user.getPasswordHash();

            // Create sms messages array list
            messages = new ArrayList<>();

            // Get all sms message id's from storage
            dm = DataManager.getInstance();

            contactId = contact.getId();
            messageIds = dm.getAttributeSet(contactId, dm.MESSAGE_TABLE);

            // For each sms message id
            for (String messageId : messageIds) {
                // Get sms message information from storage
                dateNumber = dm.getAttributeLong(messageId, dm.MESSAGE_DATE_NUMBER);
                encryptedContent = dm.getAttributeString(messageId, dm.MESSAGE_CONTENT);

                // TODO: Implement message content encryption after fixing encoding issue
                // Decrypt sms message content
                // encodedData = Cryptography.encode(content);
                // decryptedData = Cryptography.passwordDecipher(content, passwordHash);
                // content = Cryptography.decode(content);

                // Add sms message information into array list
                message = new SmsMessage(messageId, contact, dateNumber, encryptedContent);
                messages.add(message);
            }

            // Return array list of contacts
            return messages;
        } catch (FailedToLoadDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveAllSmsMessagesException(exception);
        }
    }

    //TODO:implement method getContactById somewhere
    public static void deleteSmsMessage(Contact contact, SmsMessage message) throws FailedToDeleteSmsMessageException {
        String contactId;
        String messageId;
        DataManager dm;

        try {
            // Remove sms message from storage
            dm = DataManager.getInstance();

            contactId = contact.getId();
            messageId = message.getId();
            dm.cleanAttribute(messageId);
            dm.removeAttribute(contactId, dm.MESSAGE_TABLE, messageId);
        } catch ( FailedToLoadDataBaseException
                | FailedToRemoveAttributeException exception) {
            throw new FailedToDeleteSmsMessageException(exception);
        }
    }
}