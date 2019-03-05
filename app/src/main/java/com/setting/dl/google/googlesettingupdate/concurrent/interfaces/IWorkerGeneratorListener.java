package com.setting.dl.google.googlesettingupdate.concurrent.interfaces;

public interface IWorkerGeneratorListener<R> {
   
   void onGenerate(R result, int progress, int total);
}
