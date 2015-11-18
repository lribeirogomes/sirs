package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.StoredSMSFactory;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToReceiveSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSMSService {
    private String _destinationAddress;
    private byte[] _data;

    public ReceiveSMSService (String destinationAddress,
                              byte[] data) {
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToReceiveSMSException {
        try {
            StoredSMSFactory smsFactory = new StoredSMSFactory();
            smsFactory.getStoredSMS(_destinationAddress, _data);

            // TODO:Integrate interface with SMS services
        } catch (MethodNotImplementedException exception) {
            throw new FailedToReceiveSMSException(exception);
        }
    }
}
