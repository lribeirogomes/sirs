package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import java.security.cert.CertPathValidatorException;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.ImportKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToImportException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.UntrustedCertificateException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportPrivateKeyService {
    private String _filename, _keyPassword, _storagePassword;

    public ImportPrivateKeyService(String filename, String keyPassword, String storagePassword) {
        _filename = filename;
        _keyPassword = keyPassword;
        _storagePassword = storagePassword;
    }

    public void Execute() throws FailedToImportException {
        try {
            KeyManager km = KeyManager.getInstance(_storagePassword.toCharArray());
            km.importPrivateKey(_filename, _keyPassword);
        } catch (FailedToLoadKeyStoreException | FailedToStoreException | ImportKeyException e) {
            throw new FailedToImportException(e.getMessage());
        }
    }
}
