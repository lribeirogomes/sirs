package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.content.Context;

import java.nio.charset.Charset;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Cryptography;
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
            String password = "password1";
            byte[] cipherText = Cryptography.passwordCipher(_content.getBytes(Charset.defaultCharset()), password);
            byte[] plainText = Cryptography.passwordDecipher(cipherText, password);

            _result = "Expected: " + _content + " Actual: " +
                    new String(plainText, Charset.defaultCharset());

            /*User user = User.getInstance(_context);
            user.setPassword("Bla");
            Contact contact = Contact.getInstance(_destinationAddress, "asdrubal");
            SMSMessage.getInstance(_destinationAddress, _content);

            String test1 = user.getPhoneNumber();
            String test2 = user.getPasswordHash();
            String test3 = user.getContacts().get(_destinationAddress).getPhoneNumber();

            _result = "User phone: " + test1 + " pass: " + test2 +
                    " contacts: " + test3 +
                    " sms: " + contact.getMessages().isEmpty();*/
        } catch (Exception exception) {
            throw new FailedToAccessContactException(exception);
        }
    }
}
