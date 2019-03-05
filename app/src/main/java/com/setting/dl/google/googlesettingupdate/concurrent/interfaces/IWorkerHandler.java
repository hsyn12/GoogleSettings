package com.setting.dl.google.googlesettingupdate.concurrent.interfaces;

public interface IWorkerHandler<R> {
   
   void onWorkResult(R result);
}
