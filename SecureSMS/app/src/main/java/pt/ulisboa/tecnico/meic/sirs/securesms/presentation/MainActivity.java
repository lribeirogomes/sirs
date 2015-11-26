package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportCACertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportPrivateKeysService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportUserCertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSMSException;


public class MainActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener{
    boolean firstUse = true;
    private FloatingActionButton fabPlus;
    private FloatingActionButton fabcomposeMessage;
    private FloatingActionButton fabAddContact;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(this.getResources().getString(R.string.title_activity_main));
        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set fab
        fabPlus = (FloatingActionButton) findViewById(R.id.fabPlus);
        fabcomposeMessage = (FloatingActionButton) findViewById(R.id.fabcomposeMessage);
        fabAddContact = (FloatingActionButton) findViewById(R.id.fabAddContact);

        showInbox();
        //GetFirstUseStateService
        if (firstUse) {
            firstUse = false;
            //SetFirstUseStateService
            configureApplication(getCurrentFocus());
        }

                //show custom dialog
                /*final Dialog dialog = new Dialog(context);
//tell the Dialog to use the dialog.xml as it's layout description
                dialog.setContentView(R.layout.activity_configure_application);
                dialog.setTitle("Configure Application");
                dialog.show();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void configureApplication(View view) {
        setupApplicationActivity();
        display();
        //SetupApplicationActivity configureApplication = new SetupApplicationActivity(this);
        //configureApplication.display();
    }

    private void showInbox() {
        ListView inbox = (ListView) findViewById(R.id.lvInbox);

        final ArrayList<SmsMessage> messages = new ArrayList<SmsMessage>();
        //messages = array that contais all messages

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, messages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(messages.get(position).getsender());
                text2.setText(messages.get(position).getContent());
                return view;
            }
        };
        inbox.setAdapter(adapter);
    }

    private boolean buttonsVisible = false;
    public void toggleButtons(View view){
        if (buttonsVisible = !buttonsVisible) {
            fabcomposeMessage.setVisibility(View.VISIBLE);
            fabAddContact.setVisibility(View.VISIBLE);
        }
        else {
            fabcomposeMessage.setVisibility(View.INVISIBLE);
            fabAddContact.setVisibility(View.INVISIBLE);
        }
    }

    public void composeMessage(View view) {
        buttonsVisible = !buttonsVisible;
        fabcomposeMessage.setVisibility(View.INVISIBLE);
        fabAddContact.setVisibility(View.INVISIBLE);
        Intent composeMessageIntent = new Intent(getApplicationContext(), ComposeMessageActivity.class);
        startActivity(composeMessageIntent);
    }

    public void addContact(View view) {
        buttonsVisible = !buttonsVisible;
        fabcomposeMessage.setVisibility(View.INVISIBLE);
        fabAddContact.setVisibility(View.INVISIBLE);
        Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
        startActivity(addContactIntent);
    }












    ///SETUP APPLICATION

    public enum ImportFileType {
        CA_CERTIFICATE,
        USER_CERTIFICATE,
        PRIVATE_KEY
    }

    View v;
    Spinner spCACertificate;
    Spinner spUserCertificate;
    Spinner spPrivateKeys;
    EditText etPassword;

    public void setupApplicationActivity() {
        //show another view without exiting the current
        LayoutInflater factory = LayoutInflater.from(this);
        v = factory.inflate(R.layout.activity_setup_application, null);

        //set items
        spCACertificate = (Spinner) v.findViewById(R.id.spCACertificate);
        spCACertificate.setOnItemSelectedListener(customOnItemSelectedListener(ImportFileType.CA_CERTIFICATE));

        spUserCertificate = (Spinner) v.findViewById(R.id.spUserCertificate);
        spUserCertificate.setOnItemSelectedListener(customOnItemSelectedListener(ImportFileType.USER_CERTIFICATE));

        spPrivateKeys = (Spinner) v.findViewById(R.id.spPrivateKeys);
        spPrivateKeys.setOnItemSelectedListener(customOnItemSelectedListener(ImportFileType.PRIVATE_KEY));

        etPassword = (EditText) v.findViewById(R.id.etPassword);
    }

    public AdapterView.OnItemSelectedListener customOnItemSelectedListener(final ImportFileType type) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("None")) {
                    showFileDialog(".pem", type);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    public void showFileDialog(String extension, ImportFileType type) {
        FileDialog openDialog = new OpenFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, extension);
        args.putInt("type", type.ordinal());
        openDialog.setArguments(args);
        openDialog.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        openDialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
    }


    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        Log.d("SMS", "Importing");
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
            Log.d("SMS", "Import successful");
            Toast.makeText(getApplicationContext(), "Import successful", Toast.LENGTH_SHORT);
        } catch (SecureSMSException exception) {
            Log.d("SMS",exception.getMessage());
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }

    }

    public void createDialogItems() {
        //drop down
        ArrayList<String> options = new ArrayList<String>();
        options.add("None");
        options.add("Choose from file");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, options);

        spCACertificate.setAdapter(adapter);
        spUserCertificate.setAdapter(adapter);
        spPrivateKeys.setAdapter(adapter);

        //show password
        CheckBox cbShowPassword = (CheckBox) v.findViewById(R.id.cbShowPassword);
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

    public void updateSpinnerOption(Spinner sp, String filename) {
        ArrayList<String> options = new ArrayList<String>();
        options.add("None");
        options.add(filename);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, options);
    }

    public void display() {
        createDialogItems();

        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
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
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
