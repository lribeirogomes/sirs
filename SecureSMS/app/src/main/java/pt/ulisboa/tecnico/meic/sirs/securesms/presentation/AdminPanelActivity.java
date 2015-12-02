package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;

/**
 * Created by Ana Beatriz on 27/11/2015.
 */
public class AdminPanelActivity extends AppCompatActivity {

    private TextView tvUserList, tvContactList, tvMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        tvUserList = (TextView) findViewById(R.id.tvUserList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void resetData(View view) {
        try {
            String fileList = "";
            File folder = new File("/data/data/pt.ulisboa.tecnico.meic.sirs.securesms/shared_prefs");
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    Toast.makeText(getApplicationContext(), "Directory", Toast.LENGTH_SHORT);
                } else {
                    fileEntry.delete();
                }
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
    }


    public void inspectTables(View view) {
        try {
            String fileList = "";
            File folder = new File("/data/data/pt.ulisboa.tecnico.meic.sirs.securesms/shared_prefs");
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    Toast.makeText(getApplicationContext(), "Directory", Toast.LENGTH_SHORT);
                } else {
                    fileList = tvUserList.getText().toString();
                    tvUserList.setText(fileList + "\n" + fileEntry.getName());
                }
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
    }
/*
    public void inspectTables(View view) {
        tvUserList.setText("");
        String file;
        try(BufferedReader br = new BufferedReader(new FileReader("/data/data/pt.ulisboa.tecnico.meic.sirs.securesms/shared_prefs/User+351927519814Contact0SESSION.xml"))) {
            for(String line; (line = br.readLine()) != null; ) {
                file = tvUserList.getText().toString();
                tvUserList.setText(file + "\n" + line);
            }
            // line is not visible here.
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
    }*/
}