package com.setting.dl.google.googlesettingupdate.ptt.dispatch;

import android.support.annotation.NonNull;

import com.setting.dl.google.googlesettingupdate.ptt.command.CommandExecutor;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandExecutor;

import java.util.List;

public interface IDispatch {
    
    void publishMessages(List<ICommand> messages) throws CommandExecutor.OrderException, CommandExecutor.InvalidOrderArgumentException;
    
    void registerReciever(@NonNull ICommandExecutor reciever);
    void unregisterReciever(@NonNull ICommandExecutor reciever);

}
