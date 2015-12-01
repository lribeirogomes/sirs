package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.security.cert.CertPathValidatorException;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 19/11/15.
 */
public class ImportContactCertificateService extends SecureSmsService {
    private String _filename;
    private boolean _validate;
    private boolean _isValid;

    public ImportContactCertificateService(String filename, boolean validate) {
        _filename = filename;
        _validate = validate;
    }

    public void execute() throws FailedServiceException {
        try {
            KeyManager km = KeyManager.getInstance();
            km.importUserCertificates(_filename, false, _validate);
        } catch ( KeyStoreIsLockedException
                | FailedToStoreException
                | InvalidCertificateException
                | FailedToRetrieveKeyException exception) {
            throw new FailedServiceException("import contact certificate", exception);
        } catch (CertPathValidatorException e) {
            _isValid = false;
        }
        _isValid = true;
    }

    public boolean getResult() {
        return _isValid;
    }
}
