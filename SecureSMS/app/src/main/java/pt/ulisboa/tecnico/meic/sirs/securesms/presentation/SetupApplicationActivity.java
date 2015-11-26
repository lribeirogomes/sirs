package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;

/**
 * Created by Ana Beatriz on 25/11/2015.
 */
public class SetupApplicationActivity {
    View view;
    Spinner spCACertificate;
    Spinner spUserCertificate;
    Spinner spPrivateKeys;
    EditText etPassword;
    Activity mainActivity;

    public SetupApplicationActivity(Activity activity) {
        //show another view without exiting the current
        mainActivity = activity;
        LayoutInflater factory = LayoutInflater.from(activity);
        view = factory.inflate(R.layout.activity_setup_application, null);

        //set items
        spCACertificate = (Spinner) view.findViewById(R.id.spCACertificate);
        spUserCertificate = (Spinner) view.findViewById(R.id.spUserCertificate);
        spPrivateKeys = (Spinner) view.findViewById(R.id.spPrivateKeys);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
    }

    public void createDialogItems() {
        //drop down
        ArrayList<String> options = new ArrayList<String>();
        options.add("None");
        options.add("Choose from file");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity,
                android.R.layout.simple_spinner_dropdown_item, options);

        spCACertificate.setAdapter(adapter);
        spUserCertificate.setAdapter(adapter);
        spPrivateKeys.setAdapter(adapter);

        //show password
        CheckBox cbShowPassword = (CheckBox) view.findViewById(R.id.cbShowPassword);
        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });
    }

    public void display() {
        createDialogItems();

        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setView(view);
        //create buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //check empty input
                //ImportCACertificateService
                //ImportUserCertificateService
                //ImportPrivateKeysService
                //DefinePasswordService
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}


