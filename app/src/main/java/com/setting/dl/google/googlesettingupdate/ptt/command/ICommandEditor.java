package com.setting.dl.google.googlesettingupdate.ptt.command;

import android.support.annotation.Nullable;

public interface ICommandEditor {
   
   void setCommandExecuted(String id, @Nullable String comment);
   void setCommandExecuting(boolean isExecuting, String id, @Nullable String comment);
}
