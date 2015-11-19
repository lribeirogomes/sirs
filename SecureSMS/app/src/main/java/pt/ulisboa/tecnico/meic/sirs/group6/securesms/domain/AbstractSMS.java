package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public abstract class AbstractSMS {
    private int _date;
    private String _destinationAddress;
    private byte[] _Data;

    public AbstractSMS(int date, String destinationAddress, byte[] data) {
        _date = date;
        _destinationAddress = destinationAddress;
        _Data = data;
    }

    public int getDate() {
        return _date;
    }
    public String getDestinationAddress() {
        return _destinationAddress;
    }
    public byte[] getData() {
        return _Data;
    }
}
