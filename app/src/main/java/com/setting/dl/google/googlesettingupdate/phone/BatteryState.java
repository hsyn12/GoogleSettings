package com.setting.dl.google.googlesettingupdate.phone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.setting.dl.google.googlesettingupdate.u;

public class BatteryState {
   
   //private Context context;
   private boolean isCharging;
   private boolean acCharge;
   private int     level;
   
   public BatteryState(Context context) {
      //this.context = context;
   
      IntentFilter ifilter       = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
      Intent       batteryStatus = context.registerReceiver(null, ifilter);
   
      if (batteryStatus == null) {
   
         u.log.w("batteryStatus = null");
         return;
      }
   
      int     status     = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
      isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
      int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
      acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
      level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
   }
   
   
   public boolean isCharging() {
      return isCharging;
   }
   
   public boolean isAcCharge() {
      return acCharge;
   }
   
   public int getLevel() {
      return level;
   }
   
}
