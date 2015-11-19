package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.nio.charset.StandardCharsets;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class StoredSMS extends AbstractSMS {
    public StoredSMS (int date, String destinationAddress, String data) {
        super (date, destinationAddress, data.getBytes(StandardCharsets.UTF_8));
    }
}
