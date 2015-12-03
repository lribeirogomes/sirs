package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.SecretKey;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Session {
    public enum Status{
        NonExistent,
        PartialReqReceived,
        AwaitingAck,
        Established
    }

    private final int SESSION_DURATION = 1;
    private final String NO_PENDING_SMS = "NoPendingSms";

    private Status _status;
    private Date _expirationDate;
    private int _timestamp;
    private SecretKey _sessionKey;
    private byte _ownSeqNumber, _contactSeqNumber;
    private String _pendingSmsId;

    //Constructor to be used when initiating a new session
    public Session(SecretKey sessionKey){
        byte[] sequenceNumber = new byte[1];
        new Random().nextBytes(sequenceNumber);

        if(sequenceNumber[0] < 0) //We need positive bytes
            sequenceNumber[0] = (byte)( ((int)sequenceNumber[0]) * -1);

        _timestamp = (int)(System.currentTimeMillis()/1000);
        setExpirationDate();

        _sessionKey = sessionKey;
        _ownSeqNumber = sequenceNumber[0];
        _status = Status.AwaitingAck;
        _contactSeqNumber = -1;
        _pendingSmsId = NO_PENDING_SMS;
    }

    //Constructor to be used when building a Session from storage
    public Session(SecretKey sessionKey, byte ownSeqNumber, byte contactSeqNumber, int timestamp,  Status status, String pendingSmsId){
        _sessionKey = sessionKey;
        _ownSeqNumber = ownSeqNumber;
        _contactSeqNumber = contactSeqNumber;
        _timestamp = timestamp;
        _status = status;
        _pendingSmsId = pendingSmsId;

        setExpirationDate();
    }

    //Constructor to be used when getting the first part of the KEK
    public Session(){
        _status = Status.PartialReqReceived;
        _sessionKey = null;
        _ownSeqNumber = -1;
        _contactSeqNumber = -1;
        _timestamp = 0;
        _pendingSmsId = NO_PENDING_SMS;
    }

    //Construtctor to be used when creating a session from a full KEK
    public Session(SecretKey sessionKey, byte contactSeqNumber, int timestamp){
        byte[] sequenceNumber = new byte[1];
        new Random().nextBytes(sequenceNumber);

        if(sequenceNumber[0] < 0) //We need positive bytes
            sequenceNumber[0] = (byte)( ((int)sequenceNumber[0]) * -1);

        _sessionKey = sessionKey;
        _ownSeqNumber = sequenceNumber[0];
        _status = Status.Established;
        _contactSeqNumber = contactSeqNumber;
        _timestamp = timestamp;
        _pendingSmsId = NO_PENDING_SMS;

        setExpirationDate();
    }

    private void setExpirationDate(){
        Date creationDate = new Date(((long)_timestamp)*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(creationDate);
        cal.add(Calendar.HOUR_OF_DAY, SESSION_DURATION);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        _expirationDate = cal.getTime();
    }

    public boolean hasExpired(){
        //this validation is for established sessions only, otherwise everything depending on this check fails
        if (_status != Status.Established) return false;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        if(cal.getTime().after(_expirationDate)) //Check if is expired
            return true;
        cal.add(Calendar.HOUR_OF_DAY, SESSION_DURATION);
        if(cal.getTime().before(_expirationDate)) //Check if is not yet valid
            return true;
        return false;
    }

    public byte getMySequenceNumber(){
        return _ownSeqNumber;
    }

    public byte getContactSequenceNumber(){
        return _contactSeqNumber;
    }

    public void setContactSequenceNumber(byte seqNum){
        _contactSeqNumber = seqNum;
    }

    public void incrementMySequenceNumber(){
        _ownSeqNumber++;
        if(_ownSeqNumber < 0) //Check for overflow
            _ownSeqNumber = 0;
    }

    public void incrementContactSequenceNumber(){
        _contactSeqNumber++;
        if(_contactSeqNumber < 0) //Check for overflow
            _contactSeqNumber = 0;
    }

    public SecretKey getSessionKey(){
        return _sessionKey;
    }

    public Status getStatus(){
        return _status;
    }

    public void setEstablished(){
        _status = Status.Established;
    }

    public int getTimestamp(){
        return _timestamp;
    }

    public void setPendingSmsId(String id){
        _pendingSmsId = id;
    }

    public void removePendingSms(){
        _pendingSmsId = NO_PENDING_SMS;
    }

    public String getPendingSmsId(){
        return _pendingSmsId;
    }

    public boolean hasPendingSms(){
        return !(_pendingSmsId.equals(NO_PENDING_SMS));
    }

}