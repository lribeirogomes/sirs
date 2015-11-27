package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AddContactService extends SecureSmsService {
    private String _contactName,
            _phoneNumber,
            _fileName;

    public AddContactService(String contactName, String phoneNumber, String fileName) {
        _contactName = contactName;
        _phoneNumber = phoneNumber;
        _fileName = fileName;
    }

    public void Execute() throws FailedServiceException {
        SecureSmsService createContactService,
                         importContactCertificateService;

        createContactService = new CreateContactService(_contactName, _phoneNumber);
        // TODO : implement extension identification
        //importContactCertificateService = new ImportContactCertificateService(_phoneNumber, true, _fileName);

        createContactService.Execute();
        //importContactCertificateService.Execute();
    }
}
