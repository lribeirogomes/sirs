package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.ArrayList;
import java.util.Map;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetAllLastMessagesService extends SecureSmsService {
    private ArrayList<SmsMessage> _result;

    public GetAllLastMessagesService() {
        _result = null;
    }

    public ArrayList<SmsMessage> GetResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        GetContactsService service;
        SmsMessage smsMessage;

        try {
            service = new GetContactsService();
            service.Execute();
            ArrayList<Contact> contacts = service.GetResult();

            _result = new ArrayList<>();
            for (Contact contact : contacts) {
                smsMessage = contact.getLastMessage();

                if (smsMessage == null) {
                    continue;
                }

                _result.add(smsMessage);
            }
        } catch ( FailedToGetResultException
                | FailedToRetrieveAllSmsMessagesException exception ) {
            throw new FailedServiceException("get all last messages", exception);
        }
    }
}