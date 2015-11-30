package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;


public class BeginApplicationService extends SecureSmsService {
    Context _context;

    public BeginApplicationService(Context context) {
        _context = context;
    }

    public void execute() throws FailedServiceException {
        try {
            DataManager.createDataManager(_context);
        }
        catch (FailedToCreateDataBaseException exception) {
            throw new FailedServiceException("begin application", exception);
        }
    }
}
