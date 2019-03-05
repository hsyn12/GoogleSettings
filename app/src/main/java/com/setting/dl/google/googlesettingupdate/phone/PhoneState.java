package com.setting.dl.google.googlesettingupdate.phone;

import android.app.usage.UsageStats;
import android.content.Context;

import com.setting.dl.google.googlesettingupdate.u;
import com.setting.dl.google.googlesettingupdate.usage.UsageManager;

import java.util.List;

public class PhoneState {
   
   //private Context          context;
   private boolean          screenON;
   private boolean          wifiON;
   private boolean          airplaneModeON;
   private BatteryState     batteryState;
   private List<UsageStats> lastApps;
   
   public PhoneState(Context context) {
      //this.context = context;
      
      screenON = u.isScreenOn(context);
      wifiON = u.isWifiConnected(context);
      airplaneModeON = u.isAirplaneModeOn(context);
      lastApps = UsageManager.getInstance(context).getLastUsageStats(60_000);
      batteryState = new BatteryState(context);
   }
   
   public boolean isScreenON() {
      return screenON;
   }
   
   public boolean isWifiON() {
      return wifiON;
   }
   
   public boolean isAirplaneModeON() {
      return airplaneModeON;
   }
   
   public BatteryState getBatteryState() {
      return batteryState;
   }
   
   public List<UsageStats> getLastApps() {
      return lastApps;
   }
   
}
