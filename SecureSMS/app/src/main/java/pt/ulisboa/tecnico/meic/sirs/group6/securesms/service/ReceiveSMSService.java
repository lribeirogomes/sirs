package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.content.Context;
import android.content.Intent;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.ShowSMSActivity;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMS;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToReceiveSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSMSService {
    private Context _context;
    private String _destinationAddress;
    private byte[] _data;

    public ReceiveSMSService (Context context,
                              String destinationAddress,
                              byte[] data) {
        _context = context;
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToReceiveSMSException {
        try {
            SMS sms = SMS.getInstance(_destinationAddress, _data);

            Intent result = new Intent(_context, ShowSMSActivity.class);
            result.putExtra("Destination Address", sms.getDestinationAddress());
            result.putExtra("Data", new String(sms.getContent(), Charset.defaultCharset()));
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(result);
        } catch (FailedToGetSMSException exception) {
            throw new FailedToReceiveSMSException(exception);
        }
    }
}
