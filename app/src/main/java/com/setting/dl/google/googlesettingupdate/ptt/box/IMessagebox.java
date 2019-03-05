package com.setting.dl.google.googlesettingupdate.ptt.box;

import com.setting.dl.google.googlesettingupdate.ptt.checkers.IInboxChecker;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckFilesCompleteListener;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckSent;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandEditor;

public interface IMessagebox extends IOutbox, IInboxChecker, ICheckFilesCompleteListener, ICheckSent, ICommandEditor, IHaveInbox {
   
   
}
