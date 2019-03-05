package com.setting.dl.google.googlesettingupdate.concurrent.interfaces;

@FunctionalInterface
public interface IBackgroundWorker<R> {
   
   R doit();
}
