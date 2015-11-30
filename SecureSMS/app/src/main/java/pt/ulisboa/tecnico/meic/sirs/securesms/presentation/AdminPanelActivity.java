package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Security;
import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.AuthenticateUserService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.BeginApplicationService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ResetDataService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

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
        tvContactList = (TextView) findViewById(R.id.tvContactList);
        tvMessageList = (TextView) findViewById(R.id.tvMessageList);
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
            ResetDataService service = new ResetDataService();
            service.execute();
            Toast toast = Toast.makeText(getApplicationContext(), "data reset", Toast.LENGTH_SHORT);
            toast.show();
        } catch (FailedServiceException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //TODO: put this in a service, sorry
    public void inspectTables(View view) {
        try {
            DataManager dm = DataManager.getInstance();

            Set<String> userTable = dm.getTable(dm.USER_TABLE);
            for (String user : userTable) {
                String currentUserList = tvUserList.getText().toString();
                tvUserList.setText(currentUserList + "\n" + user);
            }
            Set<String> contactTable = dm.getTable(dm.CONTACT_TABLE);
            for (String contact : contactTable) {
                String currentContactList = tvContactList.getText().toString();
                tvUserList.setText(currentContactList + "\n" + contact);
            }
            Set<String> messageTable = dm.getTable(dm.MESSAGE_TABLE);
            for (String message : messageTable) {
                String currentMessageList = tvMessageList.getText().toString();
                tvUserList.setText(currentMessageList + "\n" + message);
            }
            Toast toast = Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT);
            toast.show();

        } catch (FailedToLoadDataBaseException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), "oops", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
