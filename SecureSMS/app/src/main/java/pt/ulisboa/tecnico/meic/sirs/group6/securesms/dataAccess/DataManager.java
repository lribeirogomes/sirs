package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }
}
