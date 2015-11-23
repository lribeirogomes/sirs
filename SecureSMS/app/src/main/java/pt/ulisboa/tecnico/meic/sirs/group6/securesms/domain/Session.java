package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.security.Key;
import java.util.Date;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Session {
    private Boolean receivedReq,
                    awaitingAck;
    private Date expirationDate;
    private Key sessionKey;
    private int ownSeqNumber,
                contactSeqNumber;
}
