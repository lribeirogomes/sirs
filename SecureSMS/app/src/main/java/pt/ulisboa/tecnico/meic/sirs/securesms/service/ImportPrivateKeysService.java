package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.security.cert.CertPathValidatorException;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.ImportKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToImportException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.UntrustedCertificateException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportPrivateKeysService extends SecureSmsService {
    private String _filename, _keyPassword, _storagePassword;

    public ImportPrivateKeysService(String filename, String keyPassword, String storagePassword) {
        _filename = filename;
        _keyPassword = keyPassword;
        _storagePassword = storagePassword;
    }

    public void Execute() throws FailedToImportException {
        try {
            KeyManager km = KeyManager.getInstance(_storagePassword);
            km.importPrivateKey(_filename, _keyPassword);
        } catch (FailedToLoadKeyStoreException | FailedToStoreException | ImportKeyException e) {
            throw new FailedToImportException(e.getMessage());
        }
    }
}
