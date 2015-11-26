package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public abstract class SecureSmsService {
    public abstract void Execute() throws SecureSMSException;
}
