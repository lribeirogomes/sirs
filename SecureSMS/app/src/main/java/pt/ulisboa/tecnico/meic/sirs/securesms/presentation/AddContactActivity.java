package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;

public class AddContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        //check if fields are empty
        //AddContactService
        finish();
        Toast toast = Toast.makeText(getApplicationContext(), "Contact added successfully.", Toast.LENGTH_SHORT);
        toast.show();
    }



}
