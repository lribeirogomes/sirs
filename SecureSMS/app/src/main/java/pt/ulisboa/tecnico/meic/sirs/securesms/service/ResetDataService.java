package pt.ulisboa.tecnico.meic.sirs.securesms.service;


import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by Ana Beatriz on 30/11/2015.
 */

public class ResetDataService extends SecureSmsService {

    public ResetDataService() {}

    public void execute() throws FailedServiceException {
        try {
            DataManager dm = DataManager.getInstance();
            dm.dropAllTables();
        } catch (FailedToLoadDataBaseException exception) {
            throw new FailedServiceException("reset data", exception);
        }
    }
}
