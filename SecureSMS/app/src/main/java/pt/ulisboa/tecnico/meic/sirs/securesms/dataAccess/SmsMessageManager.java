package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.telephony.SmsManager;

import org.spongycastle.util.Arrays;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAcknowledgeSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateSessionRequestException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToSendSessionAcknowledgeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToSendSessionRequestException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageType;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SmsMessageManager {

    public static SmsMessage createSmsMessage(Contact contact, byte[] cipherText) throws FailedToCreateSmsMessageException {
        try {
            String content = SmsMessage.decrytpContent(contact, cipherText);
            return createSmsMessage(contact, content);
        } catch(FailedToDecryptSmsMessageException exception) {
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
            messageId = contactId + dm.MESSAGE_CLASS + String.format("%04d", messageCount);
            messageCount++;
            //set attributes
            dm.setAttribute(contactId, dm.MESSAGE_COUNT, messageCount);
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

    public static ArrayList<SmsMessage> retrieveAllSmsMessages(Contact contact) throws
            FailedToRetrieveAllSmsMessagesException {
        // TODO: Implement message content encryption after fixing encoding issue
        // User user;
        // String passwordHash;
        ArrayList<SmsMessage> messages;
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
    public static void sendSessionRequest(Contact contact)throws FailedToSendSessionRequestException{
        final int MAX_REQUEST_LENGTH = 256;
        final int REQUEST_SMS_LENGTH = 128;
        final short SMS_PORT= 8998;

        try {
            byte[] request = SessionManager.generateSessionRequest(contact);

            if(request.length != MAX_REQUEST_LENGTH)
                throw new FailedToSendSessionRequestException("Request lenght is not standard");

            byte[] firstMessage = Arrays.copyOfRange(request, 0, REQUEST_SMS_LENGTH);
            byte[] secondMessage = Arrays.copyOfRange(request, REQUEST_SMS_LENGTH, request.length);

            byte[] firstType = new byte[1];
            firstType[0] = (byte) SmsMessageType.RequestFirstSMS.ordinal();
            byte[] secondType = new byte[1];
            secondType[0] = (byte) SmsMessageType.RequestSecondSMS.ordinal();

            firstMessage = Arrays.concatenate(firstType, firstMessage);
            secondMessage = Arrays.concatenate(secondType, secondMessage);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendDataMessage(contact.getPhoneNumber(),
                    null, // TODO: define scAddress if needed
                    SMS_PORT,
                    firstMessage,
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed

            smsManager.sendDataMessage(contact.getPhoneNumber(),
                    null, // TODO: define scAddress if needed
                    SMS_PORT,
                    secondMessage,
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed

        }catch(FailedToGenerateSessionRequestException e){
            throw new FailedToSendSessionRequestException("Failed to send the session request sms");
        }
    }

    public static void sendSessionAcknowledge(Contact contact)throws FailedToSendSessionAcknowledgeException{
        final short SMS_PORT= 8998;

        try {
            byte[] ack = SessionManager.generateSessionAcknowledge(contact);
            byte[] type = new byte[1];
            type[0] = (byte)SmsMessageType.Acknowledge.ordinal();
            byte[] message = Arrays.concatenate(type, ack);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendDataMessage(contact.getPhoneNumber(), null, SMS_PORT, message, null, null);

        }catch(FailedToAcknowledgeSessionException e){
            throw new FailedToSendSessionAcknowledgeException("Failed to respond to the session request");
        }

    }
}
