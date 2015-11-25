package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToFirstUseException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class FirstUseService extends SecureSMSService {
    private Context _context;
    private User _result = null;

    public FirstUseService(Context context) {
        _context = context;
    }

    public User getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedToFirstUseException {
        try {
            User user = User.getInstance(_context);

            _result = user;
        } catch (FailedToGetPasswordException exception) {
            throw new FailedToFirstUseException(exception);
        }
    }
}
