package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.security.Security;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.GetAllLastMessagesService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;


public class ShowInboxActivity extends AppCompatActivity {
    private FloatingActionsMenu fabMenu;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inbox);
        setTitle(this.getResources().getString(R.string.title_activity_main));
        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        showInbox();
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

    private void showInbox() {
        try {
            GetAllLastMessagesService service = new GetAllLastMessagesService();
            service.execute();

            ArrayList<SmsMessage> messages = service.getResult();

            //show list
            ListView inbox = (ListView) findViewById(R.id.lvInbox);

            MessagePreviewAdapter adapter = new MessagePreviewAdapter(this,
                    R.layout.list_item_message_preview, messages);
            inbox.setAdapter(adapter);

        } catch (FailedServiceException | FailedToGetResultException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void composeMessage(View view) {
        fabMenu.collapse();
        Intent composeMessageIntent = new Intent(getApplicationContext(), ComposeMessageActivity.class);
        startActivity(composeMessageIntent);
    }

    public void addContact(View view) {
        fabMenu.collapse();
        Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
        startActivity(addContactIntent);
    }

    /*allows to populate an item list with a SmsMessage object*/
    private class MessagePreviewAdapter extends ArrayAdapter<SmsMessage> {
        private ArrayList<SmsMessage> _messages;

        public MessagePreviewAdapter(Context context, int textViewResourceId, ArrayList<SmsMessage> messages) {
            super(context, textViewResourceId, messages);
            _messages = new ArrayList<SmsMessage>();
            _messages.addAll(messages);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //duplicate item so it doesn't get the same view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_preview, null);

            //Show contactMessagesActivity should start when item is clicked
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    SmsMessage sms =  _messages.get(position);
                    Intent showContactMessagesIntent = new Intent(getApplicationContext(), ShowContactMessagesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ShowContactMessagesActivity.CONTACT_NUMBER_TO_SHOW, sms.getContact().getPhoneNumber());
                    bundle.putBoolean(ShowContactMessagesActivity.SHOW_ACK_DIALOG, false);
                    showContactMessagesIntent.putExtras(bundle);
                    startActivity(showContactMessagesIntent);
                }});

            TextView tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
            TextView tvMessagePreview = (TextView) convertView.findViewById(R.id.tvMessagePreview);

            //write the attributes of each contact
            tvContactName.setText(_messages.get(position).getContact().getName());
            tvMessagePreview.setText(_messages.get(position).getContent());
            //TODO:truncate content
            return convertView;
        }
    }
}
