package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.SendSmsMessageService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

public class ComposeMessageActivity extends AppCompatActivity {
    //request code so this activity knows when the ChooseContactActivity is ready to return
    private static final int CHOOSE_CONTACT_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showMessages();
    }

    //dummy list to emulate getMessagesService
    private ArrayList<String> getMessages = new ArrayList<String>();

    /*display all messages exchanged with this contact*/
    private void showMessages() {
        ListView inbox = (ListView) findViewById(R.id.lvMessages);

        final ArrayList<String> messages = getMessages;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, messages);
        inbox.setAdapter(adapter);
    }

    /*add contacts to send the message*/
    public void chooseContact(View view) {
        Intent chooseContactIntent = new Intent(this, ChooseContactActivity.class);
        //tell activity to return a list of names
        startActivityForResult(chooseContactIntent, CHOOSE_CONTACT_REQ);
    }

    public void displayContactsToSend(ArrayList<String> contactsToSend) {
        final TableRow rowReceivers = (TableRow) findViewById(R.id.trReceivers);
        for (String contactName: contactsToSend) {
            //create contact to display
            Button bReceiver = (Button) LayoutInflater.from(this).inflate(R.layout.button, null);
            bReceiver.setText(contactName);
            bReceiver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup layout = (ViewGroup) v.getParent();
                    layout.removeView(v);
                }
            });
            //put contact into table
            rowReceivers.addView(bReceiver);
        }
    }

    /*receive result from activity - choose contacts*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent chooseContactIntent) {

        if (requestCode == CHOOSE_CONTACT_REQ) {
            if(resultCode == RESULT_OK){
                //put returned contacts in the list
                displayContactsToSend(chooseContactIntent.getExtras().getStringArrayList("contactsToSendNames"));
            }
            if (resultCode == RESULT_CANCELED) {
                //if there's no result
            }
        }
    }

    public void sendMessage(View view) {
        EditText etMessage = (EditText) findViewById(R.id.etMessage);
        String message = etMessage.getText().toString();
        try {
            if (!message.equals("")) {
                //service.execute();
                etMessage.setText("");

                //SendMessageService
                //StoreMessageService
                sendSms();

                getMessages.add(message);
                showMessages();
                //hide keyboard again
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        }catch (Exception exception) {
                //show exception
        }
    }

    public void sendSms() {
        ArrayList<String> contacts = new ArrayList<String>();
        contacts.add("+351927519814");
        try {

            SendSmsMessageService service = new SendSmsMessageService(contacts, "Hello World 2.0");
            service.execute();

            Toast toast = Toast.makeText(getApplicationContext(), "sent", Toast.LENGTH_SHORT);
            toast.show();
        } catch (FailedServiceException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
