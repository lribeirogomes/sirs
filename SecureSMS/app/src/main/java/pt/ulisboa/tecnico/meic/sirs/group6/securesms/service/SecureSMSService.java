package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.SecureSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public abstract class SecureSMSService {
    public abstract void Execute() throws SecureSMSException;
}
