package com.setting.dl.google.googlesettingupdate.ptt.box;


import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;

public interface IOutbox {
   
   void addToOutbox(IMessage message);
   
}
