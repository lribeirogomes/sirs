package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public abstract class AbstractSMS {
    private int _date;
    private String _destinationAddress;
    private byte[] _encryptedData;

    public AbstractSMS(int date, String destinationAddress, byte[] data) {
        _date = date;
        _destinationAddress = destinationAddress;
        _encryptedData = data;
    }

    public int getDate() {
        return _date;
    }
    public String getDestinationAddress() {
        return _destinationAddress;
    }
    public byte[] getEncryptedData() {
        return _encryptedData;
    }
}
