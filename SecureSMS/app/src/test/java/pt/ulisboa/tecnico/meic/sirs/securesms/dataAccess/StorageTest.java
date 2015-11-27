package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Set;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class StorageTest extends AndroidTestCase {

    private long _startTime;

    @Before
    public void setUp() {
        _startTime = new Date().getTime();
    }

    @After
    public void tearDown() {
        long endTime = new Date().getTime();
        long difference = endTime - _startTime;
        System.out.println("Elapsed milliseconds: " + difference);
    }

    @Test
    public void testStorage() throws Exception {
        DataManager dm = DataManager.getInstance(getContext());

        dm.add("teste", "Corpo");
        dm.add("teste", "Xuxa");
        dm.add("teste", "Mente");
        dm.remove("teste", "Xuxa");

        Set<String> testes = dm.getAll("teste");
        String boss = "";

        for (String teste : testes ) {
            boss += teste;
        }

        assertEquals("CorpoMente", boss);
    }
}