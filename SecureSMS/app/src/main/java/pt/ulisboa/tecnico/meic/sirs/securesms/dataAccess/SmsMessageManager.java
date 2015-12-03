package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.telephony.SmsManager;

import org.spongycastle.util.Arrays;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAcknowledgeSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateSessionRequestException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToSendSessionAcknowledgeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToSendSessionRequestException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class SmsMessageManager {

    public static SmsMessage createSmsMessage(Contact contact, byte[] cipherText) throws FailedToCreateSmsMessageException {
        try {
            SmsMessage sms = new SmsMessage(contact, cipherText);
            String content = sms.decryptFromReceive();
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

            //get key to encrypt messages
            byte[] salt = Cryptography.decodeFromStorage(dm.getAttributeString(dm.USER, dm.SALT));
            byte[] iv = Cryptography.decodeFromStorage(dm.getAttributeString(dm.USER, dm.IV));
            SecretKey key = KeyManager.getInstance().generateStorageKey(salt);

            //setup message id
            contactId = contact.getId();
            messageCount = dm.getAttributeInt(contactId, dm.MESSAGE_COUNT);
            messageId = contactId + dm.MESSAGE_CLASS + String.format("%04d", messageCount);
            messageCount++;

            smsMessage = new SmsMessage(messageId, contact, dateNumber, content);
            String encryptedContent = smsMessage.encryptToStore(key, iv);

            //set attributes
            dm.setAttribute(contactId, dm.MESSAGE_COUNT, messageCount);
            dm.addAttribute(contactId, dm.MESSAGE_TABLE, messageId);
            dm.setAttribute(messageId, dm.MESSAGE_DATE_NUMBER, dateNumber);
            dm.setAttribute(messageId, dm.ENCRYPTED_CONTENT, encryptedContent);


            return smsMessage;
        } catch (FailedToLoadDataBaseException
                | KeyStoreIsLockedException
                | FailedToGenerateKeyException
                | FailedToStoreException
                | FailedToEncryptSmsMessageException
                | FailedToGetAttributeException
                | FailedToAddAttributeException exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }

    public static ArrayList<SmsMessage> retrieveAllSmsMessages(Contact contact) throws
            FailedToRetrieveAllSmsMessagesException {

        ArrayList<SmsMessage> messages;
        Set<String> messageIds;
        DataManager dm;
        long dateNumber;
        String contactId;
        String encryptedContent, decryptedContent;
        SmsMessage sms;

        try {
            messages = new ArrayList<>();

            dm = DataManager.getInstance();

            //get key to decrypt messages
            byte[] salt = Cryptography.decodeFromStorage(dm.getAttributeString(dm.USER, dm.SALT));
            byte[] iv = Cryptography.decodeFromStorage(dm.getAttributeString(dm.USER, dm.IV));
            SecretKey key = KeyManager.getInstance().generateStorageKey(salt);

            contactId = contact.getId();
            messageIds = dm.getAttributeSet(contactId, dm.MESSAGE_TABLE);

            // For each sms message id
            for (String messageId : messageIds) {
                // Get sms message information from storage
                dateNumber = dm.getAttributeLong(messageId, dm.MESSAGE_DATE_NUMBER);
                encryptedContent = dm.getAttributeString(messageId, dm.ENCRYPTED_CONTENT);
                //decrypt
                sms = new SmsMessage(contact, encryptedContent);
                decryptedContent = sms.decryptFromStorage(key, iv);
                sms = new SmsMessage(messageId, contact, dateNumber, decryptedContent);
                messages.add(sms);
            }

            // Return array list of contacts
            return messages;
        } catch (FailedToDecryptSmsMessageException
                | FailedToGenerateKeyException
                | FailedToStoreException
                | KeyStoreIsLockedException
                | FailedToLoadDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveAllSmsMessagesException(exception);
        }
    }


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

  /*  public static void setPendingSmsMessage(Contact contact, String content) throws FailedToCreateSmsMessageException {
        try {
            //TODO:store date
            DataManager dm = DataManager.getInstance();
            SmsMessage sms = createSmsMessage(contact, content);
            String encryptedContent = Cryptography.encodeForStorage(smsMessage.encryptToSend());
            sms.encryptToStore();
            dm.setAttribute(contactId, dm.PENDING_MESSAGE, messageCount);
        } catch (FailedToLoadDataBaseException
                |FailedToCreateSmsMessageException exception) {
            throw new FailedToCreateSmsMessageException(exception);
        }
    }*/

    public static ArrayList<byte[]> createReqSmsMessage(Contact contact)throws FailedToSendSessionRequestException{
        final int MAX_REQUEST_LENGTH = 256;
        final int REQUEST_SMS_LENGTH = 128;

        try {
            byte[] request = SessionManager.generateSessionRequest(contact);

            if(request.length != MAX_REQUEST_LENGTH)
                throw new FailedToSendSessionRequestException("Request length is not standard");

            byte[] firstMessage = Arrays.copyOfRange(request, 0, REQUEST_SMS_LENGTH);
            byte[] secondMessage = Arrays.copyOfRange(request, REQUEST_SMS_LENGTH, request.length);

            byte[] firstType = new byte[1];
            firstType[0] = (byte) SmsMessage.Type.RequestFirstSMS.ordinal();
            byte[] secondType = new byte[1];
            secondType[0] = (byte) SmsMessage.Type.RequestSecondSMS.ordinal();

            ArrayList<byte[]> partialRequests = new ArrayList<byte[]>(2);
            partialRequests.add(Arrays.concatenate(firstType, firstMessage));
            partialRequests.add(Arrays.concatenate(secondType, secondMessage));

            return partialRequests;

        }catch(FailedToGenerateSessionRequestException e){
            throw new FailedToSendSessionRequestException("Failed to send the session request sms");
        }
    }

    public static byte[] createAckSmsMessage(Contact contact)throws FailedToSendSessionAcknowledgeException{
        try {
            byte[] ack = SessionManager.generateSessionAcknowledge(contact);
            byte[] type = new byte[1];
            type[0] = (byte)SmsMessage.Type.Acknowledge.ordinal();
            return Arrays.concatenate(type, ack);

        }catch(FailedToAcknowledgeSessionException e){
            throw new FailedToSendSessionAcknowledgeException("Failed to respond to the session request");
        }

    }

    public static void sendSms(String address, byte[] data) {
        short SMS_PORT = 8998;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(address,
                null, // TODO: define scAddress if needed
                SMS_PORT,
                data,
                null,  // TODO: define sentIntent if needed
                null); // TODO: define deliveryIntent if needed
    }
}
