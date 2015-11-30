package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Contact {
    private String _id;
    private String _name;
    private String _phoneNumber;
    private List<SmsMessage> _messages;
    private Session session;


    public Contact(String id, String name, String phoneNumber) {
        _id = id;
        _name = name;
        _phoneNumber = phoneNumber;
        _messages = null;
    }

    public String getId() { return _id; }
    public String getName() { return _name; }
    public String getPhoneNumber() {
        return _phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        _phoneNumber = phoneNumber;
    }
    public void setName(String name) {
        _name = name;
    }

    public SmsMessage getLastMessage() throws FailedToRetrieveAllSmsMessagesException {
        List<SmsMessage> messages;

        messages = getSmsMessages();
        if(messages.isEmpty()) {
            return null;
        }
        // Return last message
        return messages.get(0);
    }

    public List<SmsMessage> getSmsMessages() throws FailedToRetrieveAllSmsMessagesException {
        // If list of messages not loaded
        if (_messages == null) {
            // Get all messages from storage
            _messages = SmsMessageManager.retrieveAllSmsMessages(this);
        }
        // Return list of messages
        return _messages;
    }
}
