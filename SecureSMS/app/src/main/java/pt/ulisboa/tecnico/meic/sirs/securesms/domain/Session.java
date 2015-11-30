package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.security.Key;
import java.util.Date;
import java.util.Random;

import javax.crypto.SecretKey;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Session {
    private Boolean _receivedReq,
                    _awaitingAck;
    private Date _expirationDate;
    private SecretKey _sessionKey;
    private byte _ownSeqNumber, _contactSeqNumber;

    //Constructor to be used when making a new session
    public Session(SecretKey sessionKey){
        byte[] sequenceNumber = new byte[1];
        new Random().nextBytes(sequenceNumber);

        if(sequenceNumber[0] < 0) //We need positive bytes
            sequenceNumber[0] = (byte)( ((int)sequenceNumber[0]) * -1);

        _sessionKey = sessionKey;
        _ownSeqNumber = sequenceNumber[0];
        _awaitingAck = true;
        _contactSeqNumber = -1;
    }

    //Constructor to be used when building a Session from storage
    public Session(SecretKey sessionKey, byte ownSeqNumber, byte contactSeqNumber){
        _sessionKey = sessionKey;
        _ownSeqNumber = ownSeqNumber;
        _contactSeqNumber = contactSeqNumber;
        _awaitingAck = false;
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
}
