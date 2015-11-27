package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Contact {
    private String _contactName,
                   _phoneNumber;
    private List<SmsMessage> _messages;

    Contact(String contactName, String phoneNumber) {
        _contactName = contactName;
        _phoneNumber = phoneNumber;
        _messages = null;
    }

    public String getPhoneNumber() {
        return _phoneNumber;
    }
    public String getContactName() {
        return _contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        _phoneNumber = phoneNumber;
    }
    public void setContactName(String contactName) {
        _contactName = contactName;
    }

    public SmsMessage getLastMessage() throws
            FailedToRetrieveAllSmsMessagesException {
        List<SmsMessage> messages;

        messages = getSmsMessages();
        if(messages.isEmpty()) {
            return null;
        }

        // Return last message
        return messages.get(0);
    }

    public List<SmsMessage> getSmsMessages() throws
            FailedToRetrieveAllSmsMessagesException {
        // If list of messages not loaded
        if (_messages == null) {
            // Get all messages from storage
            _messages = SmsMessageManager.retrieveAllSmsMessages(_phoneNumber);
        }

        // Return list of messages
        return _messages;
    }
}
