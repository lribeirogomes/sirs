package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class SmsValidator {
    public static boolean hasValidSession(String sender) throws MethodNotImplementedException {
        // has contact -> throw
        // has session
        // !awaitingAck -> throw
        // !timestamp.expired
        throw new MethodNotImplementedException();
    }

    public static boolean isValid(String sender, byte[] encryptedSms) throws MethodNotImplementedException {
        // is session valid
        // can decrypt
        // signature valid
        throw new MethodNotImplementedException();
    }
}
