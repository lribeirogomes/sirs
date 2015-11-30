package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllContactsException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateContactException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class ContactManager {

    public static void createContact(String name, String phoneNumber) throws FailedToCreateContactException {
        DataManager dm;
        String contactId;
        int contactCount;

        try {
            // Create contact into storage
            dm = DataManager.getInstance();

            //each contact has id in this form: Contact0, Contact1, etc...
            contactCount = dm.getAttributeInt(dm.USER, dm.CONTACT_COUNT);
            contactId = dm.CONTACT_CLASS + contactCount;
            //increment
            dm.setAttribute(dm.USER, dm.CONTACT_COUNT, ++contactCount);
            //add all attributes
            dm.addAttribute(dm.USER, dm.CONTACT_TABLE, contactId);
            dm.setAttribute(contactId, dm.CONTACT_NAME, name);
            dm.setAttribute(contactId, dm.CONTACT_PHONE_NUMBER, phoneNumber);
            dm.setAttribute(contactId, dm.MESSAGE_COUNT, 0);
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException
                | FailedToAddAttributeException exception ) {
            throw new FailedToCreateContactException(exception);
        }
    }

    public static Contact retrieveContactByPhoneNumber(String phoneNumber) throws FailedToRetrieveContactException {
        DataManager dm;
        Contact contact;
        Set<String> contactIds;
        String contactPhoneNumber;
        String contactName;

        try {
            // Get all contact id's from storage
            dm = DataManager.getInstance();
            contactIds = dm.getAttributeSet(dm.USER, dm.CONTACT_TABLE);

            // For each contact id
            for (String contactId : contactIds) {
                //search phone number
                contactPhoneNumber = dm.getAttributeString(contactId, dm.CONTACT_PHONE_NUMBER);
                if (phoneNumber.equals(contactPhoneNumber)) {
                    contactName = dm.getAttributeString(contactId, dm.CONTACT_NAME);
                    return new Contact(contactId, contactName, contactPhoneNumber);
                }
            }
            return null;
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException exception ) {
            throw new FailedToRetrieveContactException(exception);
        }
    }


    public static ArrayList<Contact> retrieveAllContacts() throws FailedToRetrieveAllContactsException {
        DataManager dm;
        ArrayList<Contact> contacts;
        Set<String> contactIds;
        String contactName;
        String contactPhoneNumber;
        Contact contact;

        try {
            // Create contacts hash map
            contacts = new ArrayList<>();

            // Get all contact id's from storage
            dm = DataManager.getInstance();
            contactIds = dm.getAttributeSet(dm.USER, dm.CONTACT_TABLE);

            // For each contact id
            for (String contactId : contactIds) {
                // Get contact information from storage
                contactName = dm.getAttributeString(contactId, dm.CONTACT_NAME);
                contactPhoneNumber = dm.getAttributeString(contactId, dm.CONTACT_PHONE_NUMBER);

                // Add contact information into hash map
                contact = new Contact(contactId, contactName, contactPhoneNumber);
                contacts.add(contact);
            }

            // Return hash map of contacts
            return contacts;
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException exception ) {
            throw new FailedToRetrieveAllContactsException(exception);
        }
    }

    public static void updateContact(Contact contact) throws FailedToUpdateContactException {
        String contactId;
        String contactName;
        String contactPhoneNumber;
        DataManager dm;

        try {
            // Get information from contact
            contactId = contact.getId();
            contactName = contact.getName();
            contactPhoneNumber = contact.getPhoneNumber();

            // Update contact in storage
            dm = DataManager.getInstance();
            dm.setAttribute(contactId, dm.CONTACT_NAME, contactName);
            dm.setAttribute(contactId, dm.CONTACT_PHONE_NUMBER, contactPhoneNumber);
        } catch ( FailedToLoadDataBaseException exception ) {
            throw new FailedToUpdateContactException(exception);
        }
    }

    public static void deleteContact(Contact contact) throws FailedToDeleteContactException {
        String contactId;
        DataManager dm;

        try {
            // Get information from contact
            contactId = contact.getId();

            // Remove contact from storage
            dm = DataManager.getInstance();
            dm.cleanAttribute(contactId);
            dm.removeAttribute(dm.USER, dm.CONTACT_TABLE, contactId);

        } catch ( FailedToLoadDataBaseException
                | FailedToRemoveAttributeException exception ) {
            throw new FailedToDeleteContactException(exception);
        }
    }
}
