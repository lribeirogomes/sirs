package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 18/11/15.
 */
public interface EncryptionService {
    byte[] getResult () throws FailedToGetResultException;
    void Execute () throws FailedToEncryptSMSException;
}
