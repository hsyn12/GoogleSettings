package com.setting.dl.google.googlesettingupdate.ptt.message;

import java.io.File;

public interface IMessage {
   
   String getId();
   String getSubject();
   void setSubject(String subject);
   String getBody();
   void setBody(String body);
   long getDate();
   
   void setId(String id);
   MessageType getMessageType();
   File getFile();
   void setFile(File file);
   boolean equals(Object o);
   int hashCode();
}
