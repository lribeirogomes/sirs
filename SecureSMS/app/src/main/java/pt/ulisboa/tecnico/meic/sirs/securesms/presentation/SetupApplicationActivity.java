package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;

import java.io.File;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportCACertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportPrivateKeysService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportUserCertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.SecureSmsService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSMSException;

/**
 * Created by Ana Beatriz on 25/11/2015.
 */
public class SetupApplicationActivity implements FileDialog.OnFileSelectedListener {
    public enum ImportFileType {
        CA_CERTIFICATE,
        USER_CERTIFICATE,
        PRIVATE_KEY
    }

    View view;
    Spinner spCACertificate;
    Spinner spUserCertificate;
    Spinner spPrivateKeys;
    EditText etPassword;
    AppCompatActivity mainActivity;

    public SetupApplicationActivity(AppCompatActivity activity) {
        //show another view without exiting the current
        mainActivity = activity;
        LayoutInflater factory = LayoutInflater.from(activity);
        view = factory.inflate(R.layout.activity_setup_application, null);

        //set items
        spCACertificate = (Spinner) view.findViewById(R.id.spCACertificate);
        spCACertificate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("None")) {
                    showFileDialog(".pem", ImportFileType.CA_CERTIFICATE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spUserCertificate = (Spinner) view.findViewById(R.id.spUserCertificate);
        spUserCertificate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("None")) {
                    showFileDialog(".pem", ImportFileType.USER_CERTIFICATE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spPrivateKeys = (Spinner) view.findViewById(R.id.spPrivateKeys);
        spPrivateKeys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("None")) {
                    showFileDialog(".pem", ImportFileType.PRIVATE_KEY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etPassword = (EditText) view.findViewById(R.id.etPassword);
    }

    public void showFileDialog(String extension, ImportFileType type) {
        FileDialog openDialog = new OpenFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, extension);
        args.putInt("type", type.ordinal());
        openDialog.setArguments(args);
        openDialog.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        openDialog.show(mainActivity.getSupportFragmentManager(), OpenFileDialog.class.getName());
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        ImportFileType type = ImportFileType.values()[dialog.getArguments().getInt("type")];
        try {
            switch (type) {
                case CA_CERTIFICATE: {
                    ImportCACertificateService service = new ImportCACertificateService(file.getPath(), "storagePassword");
                    service.Execute();
                    break;
                }
                case USER_CERTIFICATE: {
                    ImportUserCertificateService service = new ImportUserCertificateService(file.getPath(), true, "storagePassword");
                    service.Execute();
                    if (!service.getResult()) {
                    //prompt user to accept
                        service = new ImportUserCertificateService(file.getPath(), false, "password");
                        service.Execute();
                    }
                    break;
                }
                case PRIVATE_KEY: {
                    ImportPrivateKeysService service = new ImportPrivateKeysService(file.getPath(), "password12345", "storagePassword");
                    service.Execute();
                    break;
                }
            }
            Toast.makeText(mainActivity.getApplicationContext(),"Import successful", Toast.LENGTH_SHORT);
        } catch (SecureSMSException exception) {
            Toast.makeText(mainActivity.getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }

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


