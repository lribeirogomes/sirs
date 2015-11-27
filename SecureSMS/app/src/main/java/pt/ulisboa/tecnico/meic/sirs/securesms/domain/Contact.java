package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Contact {
    private String _phoneNumber,
                   _contactName;
    private List<SmsMessage> _messages;

    Contact(String phoneNumber, String contactName) {
        _phoneNumber = phoneNumber;
        _contactName = contactName;
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

    public List<SmsMessage> getSmsMessages() throws
            FailedToRetrieveAllSmsMessagesException {
        // If list of messages not loaded
        if (_messages == null) {
            // Get all messages from storage
            _messages = SmsMessageManager.retrieveAllSmsMessages(this);
        }

        // Return list of messages
        return _messages;
    }
}
