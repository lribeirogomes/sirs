package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import java.security.cert.CertPathValidatorException;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.UntrustedCertificateException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToImportException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportMyCertificatesService {
    private String _filename, _storagePassword;
    private boolean _validate;

    public ImportMyCertificatesService(String filename, boolean validate, String storagePassword) {
        _filename = filename;
        _storagePassword = storagePassword;
        _validate = validate;
    }

    public void Execute() throws FailedToImportException, UntrustedCertificateException{
        try {
            KeyManager km = KeyManager.getInstance(_storagePassword.toCharArray());
            km.importUserCertificates(_filename, true, _validate);
        } catch (FailedToLoadKeyStoreException | FailedToStoreException | InvalidCertificateException | FailedToRetrieveKeyException e) {
            throw new FailedToImportException(e.getMessage());
        } catch (CertPathValidatorException e){
            throw new UntrustedCertificateException("Certificate cannot be trusted");
        }
    }
}
