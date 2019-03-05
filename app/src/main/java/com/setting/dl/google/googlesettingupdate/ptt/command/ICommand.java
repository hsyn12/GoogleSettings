package com.setting.dl.google.googlesettingupdate.ptt.command;

public interface ICommand {
   
   String getCommandId();
   
   void setCommandId(String commandId);
   
   String getCommand();
   
   int getTryCount();
   
   int incTryCount();
   
   boolean isExecuting();
   
   void addComment(String comment);
   
   String getComments();
   
   void setExecuting(boolean isExecuting);
   
   long getExecutingDate();
   
   long getSendDate();
   
   long getRecieveDate();
   
   long getExecuteDate();
   
   boolean isExecuted();
   
   void setExecuted();
   
   boolean isDeleted();
   
   void setDeleted();
   
   
}
