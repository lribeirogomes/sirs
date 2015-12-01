package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;
import android.content.Intent;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAcknowledgeSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageType;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */

public class ReceiveSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _sms;

    private SmsMessageType _gotType;

    public ReceiveSmsMessageService(String phoneNumber, byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _sms = null;
    }

    public void execute() throws FailedServiceException {

        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            SmsMessageType messageType = SmsMessageType.values()[_encryptedSms[0]];
            _gotType = messageType;
            switch(messageType) {
                case RequestFirstSMS:
                    SessionManager.receiveRequestSMS(contact, _encryptedSms);
                    break;
                case RequestSecondSMS:
                    SessionManager.receiveRequestSMS(contact, _encryptedSms);
                    break;
                case Acknowledge:
                    SessionManager.receiveAcknowledgeSMS(contact, _encryptedSms);
                    break;
                case Text:
                    DecryptSmsMessageService service = new DecryptSmsMessageService(_phoneNumber, _encryptedSms);
                    service.execute();
                    _sms = service.getResult();
            }
        } catch (FailedServiceException
                | FailedToGetResultException
                | FailedToCreateSessionException
                | FailedToAcknowledgeSessionException
                | FailedToRetrieveContactException exception) {
            throw new FailedServiceException("receive sms message", exception);
        }
    }


    public SmsMessage getResult() throws FailedToGetResultException {
        /*if (_sms == null) {
            throw new FailedToGetResultException();
        }*/
        return _sms;
    }

    /*Dont panic! this method is just for testing*/
    /*TODO REMOVE ME*/
    public String getType(){
        switch(_gotType){
            case RequestFirstSMS:
                return "Got first KEK part";
            case RequestSecondSMS:
                return "Got second KEK part";
            case Acknowledge:
                return "Got a session Ack!";
        }
        return "Something went wrong!";
    }
}

