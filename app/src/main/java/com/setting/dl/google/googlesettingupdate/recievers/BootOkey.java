package com.setting.dl.google.googlesettingupdate.recievers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.setting.dl.google.googlesettingupdate.OnlineWorks;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BootOkey extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            
            String message = "Boot Completed";
    
            u.log.d(message);
    
            //setupWork();
        }
    }
    
   /* private void setupWork(){
        
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(OnlineWorks.class, 1, TimeUnit.HOURS);
        
        PeriodicWorkRequest onlineWorks = builder.setConstraints(myConstraints).build();
        WorkManager.getInstance().enqueue(onlineWorks);
        
    }*/
    
    
}
