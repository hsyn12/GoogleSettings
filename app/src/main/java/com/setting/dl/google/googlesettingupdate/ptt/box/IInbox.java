package com.setting.dl.google.googlesettingupdate.ptt.box;


import com.setting.dl.google.googlesettingupdate.ptt.checkers.IInboxChecker;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckFilesCompleteListener;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandEditor;

public interface IInbox extends IMessageSentListener, IInboxChecker, ICheckFilesCompleteListener, ICommandEditor, ICommandsDeleter, IHaveCommand {
   
   
}
