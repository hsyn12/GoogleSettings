package com.setting.dl.google.googlesettingupdate.concurrent.interfaces;

@FunctionalInterface
public interface IWorkerProccessor<R, X> {
   
   X onWorkProccess(R result);
}
