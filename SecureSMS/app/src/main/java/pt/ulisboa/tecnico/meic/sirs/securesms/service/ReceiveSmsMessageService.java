package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAcknowledgeSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */

//merged checkSessionService with this one because it invoked the same instructions again

public class ReceiveSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _sms;
    private Session.Status _sessionStatus;

    public ReceiveSmsMessageService(String phoneNumber, byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _sms = null;
    }

    public void execute() throws FailedServiceException {

        try {
            //TODO: check in contact exists and prompt user to add it + throw exceptions below + delete session when exceptions are caught
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            SmsMessage.Type messageType = SmsMessage.Type.values()[_encryptedSms[0]];
            _sessionStatus = SessionManager.checkSessionStatus(contact);

            switch (_sessionStatus) {
                case Established: {
                    if (messageType == SmsMessage.Type.Text) {
                        DecryptSmsMessageService service = new DecryptSmsMessageService(_phoneNumber, _encryptedSms);
                        service.execute();
                        _sms = service.getResult();
                        return;
                    }
                    break;
                }
                case AwaitingAck: {
                    if (messageType == SmsMessage.Type.Acknowledge) {
                        SessionManager.receiveAcknowledgeSMS(contact, _encryptedSms);
                        return;
                    }
                    break;
                }
                case NonExistent: {
                    if (messageType == SmsMessage.Type.RequestFirstSMS || messageType == SmsMessage.Type.RequestSecondSMS) {
                        SessionManager.createSession(contact, _encryptedSms);
                        return;
                    }
                    break;
                }
                case PartialReqReceived: {
                    if (messageType == SmsMessage.Type.RequestFirstSMS || messageType == SmsMessage.Type.RequestSecondSMS) {
                        SessionManager.receiveRequestSMS(contact, _encryptedSms);
                        return;
                    }
                    break;
                }
            }
            //if none of the above apply, reject
            throw new FailedServiceException("Rejected incoming message");
        } catch (FailedServiceException
                | FailedToGetResultException
                | FailedToCreateSessionException
                | FailedToAcknowledgeSessionException
                | FailedToRetrieveContactException exception) {
            throw new FailedServiceException("receive sms message", exception);
        }
    }


    public SmsMessage getResultSms() throws FailedToGetResultException {
        if (_sms == null) {
            throw new FailedToGetResultException();
        }
        return _sms;
    }

    public Session.Status getResultStatus() throws FailedToGetResultException {
        return _sessionStatus;
    }

}

