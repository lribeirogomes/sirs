package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.DeleteSessionService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.GetMessagesByContactService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsMessageService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.SendSessionAcknowledgmentService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.SendSmsMessageService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by Ana Beatriz on 30/11/2015.
 */
public class ShowContactMessagesActivity extends AppCompatActivity {
    public static final String CONTACT_NUMBER_TO_SHOW = "pt.ulisboa.tecnico.meic.sirs.securesms.contactnumbertoshow";
    public static final String SHOW_ACK_DIALOG = "pt.ulisboa.tecnico.meic.sirs.securesms.showackdialog";

    private String _contactPhoneNumber;

    private AlertDialog ackDialog;
    private Button bConfirmRequest;
    private Button bDeclineRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact_messages);
        setTitle(this.getResources().getString(R.string.title_activity_main));
        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        _contactPhoneNumber = bundle.getString(CONTACT_NUMBER_TO_SHOW);
        boolean showAckDialog = bundle.getBoolean(SHOW_ACK_DIALOG);

        if (showAckDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowContactMessagesActivity.this);

            LayoutInflater factory = LayoutInflater.from(this);
            builder.setView(factory.inflate(R.layout.dialog_session_request, null));
            ackDialog = builder.create();
            ackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bConfirmRequest = (Button) findViewById(R.id.bConfirmRequest);
            bDeclineRequest = (Button) findViewById(R.id.bDeclineRequest);
            ackDialog.show();
        }

        showMessages();
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

    private void showMessages() {
        try {
            GetMessagesByContactService service = new GetMessagesByContactService(_contactPhoneNumber);
            service.execute();
            final ArrayList<SmsMessage> _messages = service.getResult();
            //show list
            ListView contactMessages = (ListView) findViewById(R.id.lvMessages);

            ArrayAdapter<SmsMessage> adapter = new ArrayAdapter<SmsMessage>(this,
                    android.R.layout.simple_list_item_1, _messages) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setText(_messages.get(position).getContent());
                    return view;
                }
            };
            contactMessages.setAdapter(adapter);

        } catch (FailedServiceException
                | FailedToGetResultException exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Todo:refactor this, probably implement InterfaceManager
    public void sendMessage(View view) {
        EditText etMessage = (EditText) findViewById(R.id.etMessage);
        String message = etMessage.getText().toString();
        ArrayList<String> contactsToSend = new ArrayList<String>();
        contactsToSend.add(_contactPhoneNumber);
        try {
            if (!message.equals("")) {
                SendSmsMessageService service = new SendSmsMessageService(contactsToSend, etMessage.getText().toString());
                service.execute();
                etMessage.setText("");

                showMessages();
                //hide keyboard again
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        } catch (FailedServiceException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void confirmRequest(View view) {
        try {
            SendSessionAcknowledgmentService service = new SendSessionAcknowledgmentService(_contactPhoneNumber);
            service.execute();
        }catch (FailedServiceException exception) {
            Toast.makeText(ShowContactMessagesActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
        }
        ackDialog.dismiss();
    }

    public void declineRequest(View view) {
        try {
            DeleteSessionService service = new DeleteSessionService(_contactPhoneNumber);
            service.execute();
        }catch (FailedServiceException exception) {
            Toast.makeText(ShowContactMessagesActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
        }
        finish();
    }

}