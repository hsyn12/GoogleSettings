package com.setting.dl.google.googlesettingupdate.ptt.message;

import android.support.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface IMessageReciever {
    
    @NonNull
    List<Message> recieveMessages(Gmail gmail, List<String> labels, String query);
}
