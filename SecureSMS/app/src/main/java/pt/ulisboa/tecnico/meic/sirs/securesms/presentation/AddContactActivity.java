package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.AddContactService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

public class AddContactActivity extends AppCompatActivity {

    private EditText etContactName;
    private EditText etContactPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        etContactName = (EditText) findViewById(R.id.etContactName);
        etContactPhoneNumber = (EditText) findViewById(R.id.etContactPhoneNumber);

        Spinner spCertificate = (Spinner) findViewById(R.id.spCertificate);
        //drop down
        ArrayList<String> options = new ArrayList<String>();
        options.add("None");
        options.add("Choose from file");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, options);

        spCertificate.setAdapter(adapter);
    }

    public void confirmAddContact(View view) {
        Toast toast;
        try {
            //check if fields are empty
            String contactName = etContactName.getText().toString();
            String contactPhoneNumber = etContactPhoneNumber.getText().toString();

            AddContactService service = new AddContactService(contactName, contactPhoneNumber, "");
            service.execute();
            finish();
            toast = Toast.makeText(getApplicationContext(), "Contact added successfully.", Toast.LENGTH_SHORT);
        }
        catch(FailedServiceException exception) {
            //should be dialog or snackbar
            toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
