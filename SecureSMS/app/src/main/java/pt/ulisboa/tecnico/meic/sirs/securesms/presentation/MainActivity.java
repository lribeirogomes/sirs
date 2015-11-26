package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import java.security.Security;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.R;


public class MainActivity extends AppCompatActivity {
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
        SetupApplicationActivity configureApplication = new SetupApplicationActivity(this);
        configureApplication.display();
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
}
