package com.setting.dl.google.googlesettingupdate.ptt.command;

public interface ICommandExecutor {
    
    void executeCommand(ICommand command) throws CommandExecutor.OrderException, CommandExecutor.InvalidOrderArgumentException;
}
