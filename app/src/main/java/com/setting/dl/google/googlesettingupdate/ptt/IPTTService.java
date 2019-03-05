package com.setting.dl.google.googlesettingupdate.ptt;

import com.setting.dl.google.googlesettingupdate.ptt.box.ICommandsDeleter;
import com.setting.dl.google.googlesettingupdate.ptt.box.IHaveCommand;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.IInboxChecker;
import com.setting.dl.google.googlesettingupdate.ptt.deleters.ISentMessageDeleter;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandEditor;
import com.setting.dl.google.googlesettingupdate.ptt.box.IMessagebox;

public interface IPTTService extends IMessagebox, ISentMessageDeleter, IInboxChecker, ICommandEditor, ICommandsDeleter, IHaveCommand {
   
}
