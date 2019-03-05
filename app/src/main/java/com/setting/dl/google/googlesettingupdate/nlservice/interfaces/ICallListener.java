package com.setting.dl.google.googlesettingupdate.nlservice.interfaces;

public interface ICallListener {
   
   void onOutgoingCallStart(long callStartTime, String number);
   void onIncommingCallAnswer(long ringingStartTime, long answerTime, String number);
   void onMissedCall(long ringingStartTime, long endTime, String number);
   void onOutgoingCallEnd(long callStartTime, long endTime, String number);
   void onIncommingCallEnd(long ringingStartTime, long speakingStartTime, long endTime, String number);
   
   
}
