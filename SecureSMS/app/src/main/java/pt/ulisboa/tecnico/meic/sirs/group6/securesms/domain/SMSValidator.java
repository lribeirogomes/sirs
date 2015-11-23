package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class SMSValidator {
    public static boolean hasValidSession(String sender) throws MethodNotImplementedException {
        // has contact -> throw
        // has session
        // !awaitingAck -> throw
        // !timestamp.expired
        throw new MethodNotImplementedException();
    }

    public static boolean isValid(String sender, byte[] encryptedSMS) throws MethodNotImplementedException {
        // is session valid
        // can decrypt
        // signature valid
        throw new MethodNotImplementedException();
    }
}
