package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class KeyStoreIsLockedException extends Exception{
    public KeyStoreIsLockedException(){
        super("The Keystore hasn't been unlocked yet.");
    }
}
