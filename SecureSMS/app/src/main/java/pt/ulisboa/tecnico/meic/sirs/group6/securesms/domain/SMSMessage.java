package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SMSMessage {
    private Date _date;
    private String _sender;
    private byte[] _content;

    public static SMSMessage getInstance(String password, String sender, String content)
            throws FailedToGetSMSException {
        byte [] result;
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        Date date = calendar.getTime();

        try {
            // Encrypt SMSMessage Body using password
            result = Cryptography.passwordCipher(content.getBytes(Charset.defaultCharset()), password);

            // Store Encrypted SMSMessage
            //StoreSMSService storageService = new StoreSMSService(date, sender, result);
            //storageService.Execute();

            return new SMSMessage(date ,sender, content.getBytes(Charset.defaultCharset())) ;
        } catch (
                NullPointerException |
                IllegalArgumentException |
                FailedToEncryptException exception) {//|
                        //FailedToStoreSMSException exception) {
            throw new FailedToGetSMSException(exception);
        }
    }

    public static SMSMessage getInstance(String sender, byte[] content)
            throws FailedToGetSMSException {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        int date = calendar.get(GregorianCalendar.DATE);

        try {
            return null ;
        } catch (
                NullPointerException |
                        IllegalArgumentException exception) {//|
                        //FailedToStoreSMSException exception) {
            throw new FailedToGetSMSException(exception);
        }
    }

    protected SMSMessage(Date date, String sender, byte[] data) {
        _date = date;
        _sender = sender;
        _content = data;
    }

    public Date getdate() {
        return _date;
    }
    public String getsender() {
        return _sender;
    }
    public byte[] getContent() {
        return _content;
    }
}
