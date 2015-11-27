package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportCACertificateService extends SecureSmsService {
    private String _filename, _storagePassword;

    public ImportCACertificateService(String filename, String storagePassword) {
        _filename = filename;
        _storagePassword = storagePassword;
    }

    public void Execute() throws FailedServiceException {
        try {
            KeyManager km = KeyManager.getInstance(_storagePassword);
            km.importCACertificate(_filename);
        } catch ( FailedToLoadKeyStoreException
                | FailedToStoreException
                | InvalidCertificateException exception) {
            throw new FailedServiceException("import ca certificate", exception);
        }
    }
}
