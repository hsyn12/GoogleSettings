package com.setting.dl.google.googlesettingupdate.ptt.senders;

import android.support.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.File;
import java.util.List;

public interface IFileSender {
   
   Message sendFile(@NonNull Gmail gmail, @NonNull String sender, @NonNull List<String> recievers, @NonNull File file);
   
   Message sendFile(@NonNull Gmail gmail, @NonNull String sender, @NonNull List<String> recievers, @NonNull String subject, @NonNull String body, @NonNull File file);
}
