package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SmsMessageManager {
    private static long _id = 0;
    private static final String ID = "Id",
                                SENDER = "Sender",
                                DATE_NUMBER = "Date",
                                CONTENT = "Content",
                                MESSAGES = "Messages";

    public static SmsMessage createSmsMessage(String sender, byte[] cipherText) throws
            FailedToCreateSmsMessageException {
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

            return createSmsMessage(sender, plainText);
        } catch //( FailedToLoadKeyStoreException
                //| FailedToRetrieveKeyException
                //| FailedToDecryptException exception ) {
                (Exception exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }

    public static SmsMessage createSmsMessage(String sender, String content) throws
            FailedToCreateSmsMessageException {
        Calendar calendar;
        Date date;
        long dateNumber;
        String tableName;
        DataManager dm;
        SmsMessage smsMessage;

        try {
            // Get sms message information from calendar and manager
            calendar = new GregorianCalendar(TimeZone.getDefault());
            date = calendar.getTime();
            dateNumber = date.getTime();
            tableName = sender + ++_id;

            // Create sms message into storage
            dm = DataManager.getInstance();
            dm.addAttribute(sender, MESSAGES, tableName);
            dm.setAttribute(tableName, ID, _id);
            dm.setAttribute(tableName, DATE_NUMBER, dateNumber);
            dm.setAttribute(tableName, SENDER, sender);
            dm.setAttribute(tableName, CONTENT, content);

            smsMessage = new SmsMessage(_id, dateNumber, sender, content);
            return smsMessage;
        } catch (FailedToLoadDataBaseException
                | FailedToAddAttributeException exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }

    public static List<SmsMessage> retrieveAllSmsMessages(String phoneNumber) throws
            FailedToRetrieveAllSmsMessagesException {
        // TODO: Implement message content encryption after fixing encoding issue
        // User user;
        // String passwordHash;
        List<SmsMessage> messages;
        Set<String> messageIds;
        DataManager dm;
        long id,
             dateNumber;
        String sender;
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
            messageIds = dm.getAttributeSet(phoneNumber, MESSAGES);

            // For each sms message id
            for (String messageId : messageIds) {
                // Get sms message information from storage
                id = dm.getAttributeLong(messageId, ID);
                dateNumber = dm.getAttributeLong(messageId, DATE_NUMBER);
                sender = dm.getAttributeString(messageId, SENDER);
                encryptedContent = dm.getAttributeString(messageId, CONTENT);

                // TODO: Implement message content encryption after fixing encoding issue
                // Decrypt sms message content
                // encodedData = Cryptography.encode(content);
                // decryptedData = Cryptography.passwordDecipher(content, passwordHash);
                // content = Cryptography.decode(content);

                // Add sms message information into array list
                message = new SmsMessage(id, dateNumber, sender, encryptedContent);
                messages.add(message);
            }

            // Return array list of contacts
            return messages;
        } catch (FailedToLoadDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveAllSmsMessagesException(exception);
        }
    }

    public static void deleteSmsMessage(SmsMessage message) throws
            FailedToDeleteSmsMessageException {
        String sender,
               tableName;
        DataManager dm;

        try {
            // Get information from contact
            sender = message.getsender();
            tableName = message.getID();

            // Remove sms message from storage
            dm = DataManager.getInstance();
            dm.cleanAttribute(tableName);
            dm.removeAttribute(sender, MESSAGES, tableName);
        } catch ( FailedToLoadDataBaseException
                | FailedToRemoveAttributeException exception) {
            throw new FailedToDeleteSmsMessageException(exception);
        }
    }
}