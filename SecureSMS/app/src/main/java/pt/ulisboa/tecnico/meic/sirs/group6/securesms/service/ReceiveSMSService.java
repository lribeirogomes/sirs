package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.content.Context;
import android.content.Intent;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.ShowSMSActivity;
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
            //StoredSMSFactory smsFactory = new StoredSMSFactory();
            //StoredSMS sms = smsFactory.getStoredSMS(_destinationAddress, _data);

            Intent result = new Intent(_context, ShowSMSActivity.class);
            result.putExtra("Destination Address", _destinationAddress);//sms.getDestinationAddress());
            result.putExtra("Data", new String( _data, Charset.defaultCharset()));//sms.getData(), Charset.defaultCharset()));
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(result);
        } catch (Exception exception) {//MethodNotImplementedException exception) {
            throw new FailedToReceiveSMSException(exception);
        }
    }
}
