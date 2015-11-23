package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToImportException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportCACertificateService extends SecureSMSService {
    private String _filename, _storagePassword;

    public ImportCACertificateService(String filename, String storagePassword) {
        _filename = filename;
        _storagePassword = storagePassword;
    }

    public void Execute() throws FailedToImportException {
        try {
            KeyManager km = KeyManager.getInstance(_storagePassword);
            km.importCACertificate(_filename);
        } catch (FailedToLoadKeyStoreException | FailedToStoreException | InvalidCertificateException e) {
            throw new FailedToImportException(e.getMessage());
        }
    }
}
