package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToAccessContactException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class TestingService extends SecureSMSService {

    private Context _context;
    private String _destinationAddress, _content;
    private String _result;

    public TestingService(Context context) {
        _context = context;
        _destinationAddress = "964248700";
        _content = "Hello World";
    }

    public String getResult() {
        return _result;
    }

    public void Execute() throws FailedToAccessContactException {
        try {
            /*String password = "password1";
            byte[] cipherText = Cryptography.passwordCipher(_content.getBytes(Charset.defaultCharset()), password);
            byte[] plainText = Cryptography.passwordDecipher(cipherText, password);

            _result = "Expected: " + _content + " Actual: " +
                    new String(plainText, Charset.defaultCharset());*/

            User user = User.getInstance(_context);
            user.setPassword("Bla");
            user.setPhoneNumber("42");
            Contact contact = Contact.getInstance(_destinationAddress, "asdrubal");
            SMSMessage.getInstance(_destinationAddress, _content);

            String test1 = user.getPhoneNumber();
            String test2 = user.getPasswordHash();
            String test3 = user.getContacts().get(_destinationAddress).getPhoneNumber();
            String test4 = "";
            for (SMSMessage sms : contact.getMessages()) {
                test4 += sms.getContent();
            }

            _result = "User phone: " + test1 + " pass: " + test2 +
                    " contacts: " + test3 +
                    " sms: " + test4;
        } catch (Exception exception) {
            throw new FailedToAccessContactException(exception);
        }
    }
}
