package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class StoredSMS extends AbstractSMS {
    public StoredSMS (int date, String destinationAddress, byte[] data) {
        super (date, destinationAddress, data);
    }
}
