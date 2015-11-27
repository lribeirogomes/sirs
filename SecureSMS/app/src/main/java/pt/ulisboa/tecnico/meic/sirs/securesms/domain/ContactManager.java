package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllContactsException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class ContactManager {
    private static final String CONTACT_NAME = "ContactName",
                         CONTACTS = "Contacts",
                         USER_NAME = "UserName";

    public static void createContact(String contactName, String phoneNumber) throws
            FailedToCreateContactException {
        DataManager dm;

        try {
            // Create contact into storage
            dm = DataManager.getInstance();
            dm.addAttribute(USER_NAME, CONTACTS, phoneNumber);
            dm.setAttribute(phoneNumber, CONTACT_NAME, contactName);
        } catch ( FailedToLoadDataBaseException
                | FailedToAddAttributeException exception ) {
            throw new FailedToCreateContactException(exception);
        }
    }

    public static ArrayList<Contact> retrieveAllContacts() throws
            FailedToRetrieveAllContactsException {
        ArrayList<Contact> contacts;
        DataManager dm;
        Set<String> contactIds;
        String contactName;
        Contact contact;

        try {
            // Create contacts hash map
            contacts = new ArrayList<>();

            // Get all contact id's from storage
            dm = DataManager.getInstance();
            contactIds = dm.getAttributeSet(USER_NAME, CONTACTS);

            // For each contact id
            for (String phoneNumber : contactIds) {
                // Get contact information from storage
                contactName = dm.getAttributeString(phoneNumber, CONTACT_NAME);

                // Add contact information into hash map
                contact = new Contact(contactName, phoneNumber);
                contacts.add(contact);
            }

            // Return hash map of contacts
            return contacts;
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException exception ) {
            throw new FailedToRetrieveAllContactsException(exception);
        }
    }

    public static void updateContact(Contact contact) throws
            FailedToUpdateContactException {
        String phoneNumber,
               contactName;
        DataManager dm;

        try {
            // Get information from contact
            phoneNumber = contact.getPhoneNumber();
            contactName = contact.getContactName();

            // Update contact in storage
            dm = DataManager.getInstance();
            dm.setAttribute(phoneNumber, CONTACT_NAME, contactName);
        } catch ( FailedToLoadDataBaseException exception ) {
            throw new FailedToUpdateContactException(exception);
        }
    }

    public static void deleteContact(Contact contact) throws
            FailedToDeleteContactException {
        String phoneNumber;
        DataManager dm;

        try {
            // Get information from contact
            phoneNumber = contact.getPhoneNumber();

            // Remove contact from storage
            dm = DataManager.getInstance();
            dm.cleanAttribute(phoneNumber);
            dm.removeAttribute(USER_NAME, CONTACTS, phoneNumber);
        } catch ( FailedToLoadDataBaseException
                | FailedToRemoveAttributeException exception ) {
            throw new FailedToDeleteContactException(exception);
        }
    }
}
