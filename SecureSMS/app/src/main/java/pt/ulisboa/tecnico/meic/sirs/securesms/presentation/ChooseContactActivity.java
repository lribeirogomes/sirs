package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.R;

public class ChooseContactActivity extends AppCompatActivity {

    private ArrayList<Contact> getContacts = new ArrayList<Contact>();
    private ArrayList<String> contactsToSend = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getContacts.add(new Contact("Ana", "+351927519814"));
        //getContacts.add(new Contact("Ana", "+351927519814"));
        //getContacts.add(new Contact("Ana", "+351927519814"));
        //getContacts.add(new Contact("Ana", "+351927519814"));
        //getContacts.add(new Contact("Ana", "+351927519814"));
        //getContacts.add(new Contact("Ana", "+351927519814"));

        ListView lvContacts = (ListView) findViewById(R.id.lvContacts);

        ContactAdapter adapter = new ContactAdapter(this,
                R.layout.list_item_contact, getContacts);
        lvContacts.setAdapter(adapter);
    }

    public void confirmChooseContact(View view) {
        //make array with chosen contacts
        Intent outputIntent = new Intent();
        outputIntent.putStringArrayListExtra("contactsToSend", contactsToSend);
        setResult(getParent().RESULT_OK, outputIntent);
        finish();
    }

    /*allows to populate an item list with a Contact object*/
    private class ContactAdapter extends ArrayAdapter<Contact> {
        private ArrayList<Contact> _contacts;

        public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> contacts) {
            super(context, textViewResourceId, contacts);
            _contacts = new ArrayList<Contact>();
            _contacts.addAll(contacts);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //duplicate item so it doesn't get the same view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_contact, null);
            LinearLayout llContact = (LinearLayout) convertView.findViewById(R.id.llContact);
            TextView tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
            TextView tvContactPhoneNumber = (TextView) convertView.findViewById(R.id.tvContactPhoneNumber);
            final CheckBox cbContact = (CheckBox) convertView.findViewById(R.id.cbContact);

            //write the attributes of each contact
            tvContactName.setText(_contacts.get(position).getName());
            tvContactPhoneNumber.setText(_contacts.get(position).getPhoneNumber());
            //if whole item is clicked, checkbox toggles
            llContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbContact.setChecked(!cbContact.isChecked());
                    if (cbContact.isChecked()) {
                        contactsToSend.add(_contacts.get(position).getName());
                    }
                    else contactsToSend.remove(_contacts.get(position).getName());
                }
            });
            return convertView;
        }
    }
}
