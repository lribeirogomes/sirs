package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.SecretKey;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Session {
    private final int SESSION_DURATION = 1;

    private boolean _awaitingAck;
    private Date _expirationDate;
    private int _timestamp;
    private SecretKey _sessionKey;
    private byte _ownSeqNumber, _contactSeqNumber;

    //Constructor to be used when making a new session
    public Session(SecretKey sessionKey){
        byte[] sequenceNumber = new byte[1];
        new Random().nextBytes(sequenceNumber);

        if(sequenceNumber[0] < 0) //We need positive bytes
            sequenceNumber[0] = (byte)( ((int)sequenceNumber[0]) * -1);

        _timestamp = (int)(System.currentTimeMillis()/1000);
        setExpirationDate();

        _sessionKey = sessionKey;
        _ownSeqNumber = sequenceNumber[0];
        _awaitingAck = true;
        _contactSeqNumber = -1;
    }

    //Constructor to be used when building a Session from storage
    public Session(SecretKey sessionKey, byte ownSeqNumber, byte contactSeqNumber, int timestamp,  boolean status){
        _sessionKey = sessionKey;
        _ownSeqNumber = ownSeqNumber;
        _contactSeqNumber = contactSeqNumber;
        _timestamp = timestamp;
        _awaitingAck = status;

        setExpirationDate();
    }

    //Construtctor to be used when creating a session from a KEK
    public Session(SecretKey sessionKey, byte contactSeqNumber, int timestamp){
        byte[] sequenceNumber = new byte[1];
        new Random().nextBytes(sequenceNumber);

        if(sequenceNumber[0] < 0) //We need positive bytes
            sequenceNumber[0] = (byte)( ((int)sequenceNumber[0]) * -1);

        _sessionKey = sessionKey;
        _ownSeqNumber = sequenceNumber[0];
        _awaitingAck = false;
        _contactSeqNumber = contactSeqNumber;
        _timestamp = timestamp;

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

    public boolean getStatus(){
        return _awaitingAck;
    }

    public int getTimestamp(){
        return _timestamp;
    }
}