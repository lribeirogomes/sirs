package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;


import android.os.Bundle;
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
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.security.Security;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.R;
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

    private void showInbox() {

        try {
            GetAllLastMessagesService service = new GetAllLastMessagesService();
            service.execute();

            final ArrayList<SmsMessage> messages = service.getResult();

            //show list
            ListView inbox = (ListView) findViewById(R.id.lvInbox);

            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_2, android.R.id.text1, messages) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(messages.get(position).getContact().getName());
                    text2.setText(messages.get(position).getContent());
                    return view;
                }
            };
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

}
