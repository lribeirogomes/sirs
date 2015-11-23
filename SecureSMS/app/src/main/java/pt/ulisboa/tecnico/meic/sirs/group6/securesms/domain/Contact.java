package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.security.Key;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Contact {
    private String _phoneNumber;
    private Key pubEncryptKey,
                pubSignKey;
    private Session _session;
    private List<SMSMessage> messages;

    public static Contact getInstance(String phoneNumber) {// throws FailedToGetContactException {
        // TODO: integrate obect with database

        return new Contact(phoneNumber);
    }

    private Contact(String phoneNumber) {
        _phoneNumber = phoneNumber;
    }

    public List<SMSMessage> getMessages() throws MethodNotImplementedException {
        throw new MethodNotImplementedException();
    }
}
