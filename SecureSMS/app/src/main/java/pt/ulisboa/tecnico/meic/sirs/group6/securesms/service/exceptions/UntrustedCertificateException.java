package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class UntrustedCertificateException extends SecureSMSException{
    public UntrustedCertificateException(String m){
        super(m);
    }
}
