package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;

import java.io.File;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.AddContactService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportContactCertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSmsException;

public class AddContactActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener {

    private EditText etContactName;
    private EditText etContactPhoneNumber;
    private EditText etContactCertificate;
    private CheckBox cbSelfSignedContact;
    private TextView tvContactCertificateValid;
    private TextView tvContactCertificateInvalid;
    private ImageButton bContactCertificate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        etContactName = (EditText) findViewById(R.id.etContactName);
        etContactPhoneNumber = (EditText) findViewById(R.id.etContactPhoneNumber);
        etContactCertificate = (EditText) findViewById(R.id.etContactCertificate);
        etContactCertificate.setKeyListener(null);
        bContactCertificate = (ImageButton) findViewById(R.id.bContactCertificate);
        bContactCertificate.setOnClickListener(customOnClickListener());
        cbSelfSignedContact = (CheckBox) findViewById(R.id.cbSelfSignedContact);
        tvContactCertificateValid = (TextView) findViewById(R.id.tvContactCertificateValid);
        tvContactCertificateInvalid = (TextView) findViewById(R.id.tvContactCertificateInvalid);
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
        } catch (FailedServiceException exception) {
            //should be dialog or snackbar
            toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    //file explorer stuff

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        try {
            ImportContactCertificateService service = new ImportContactCertificateService(file.getPath(), true);
            service.execute();
            if (!service.getResult()) {
                //prompt user to accept
                service = new ImportContactCertificateService(file.getPath(), false);
                service.execute();
                tvContactCertificateValid.setVisibility(View.INVISIBLE);
                tvContactCertificateInvalid.setVisibility(View.VISIBLE);
                //tvUserCertificateSelfSigned.setVisibility(View.VISIBLE);
            } else {
                tvContactCertificateValid.setVisibility(View.VISIBLE);
                tvContactCertificateInvalid.setVisibility(View.INVISIBLE);
                //tvUserCertificateSelfSigned.setVisibility(View.INVISIBLE);
            }
        } catch (SecureSmsException exception) {
            tvContactCertificateValid.setVisibility(View.INVISIBLE);
            tvContactCertificateInvalid.setVisibility(View.VISIBLE);
        }
        etContactCertificate.setText(file.getPath());
    }

    //custom listener
    private View.OnClickListener customOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileDialog(".pem");
            }
        };
    }

    public void showFileDialog(String extension) {
        FileDialog openDialog = new OpenFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, extension);
        openDialog.setArguments(args);
        openDialog.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        openDialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
    }
}
