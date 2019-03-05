package com.setting.dl.google.googlesettingupdate.concurrent;

import android.os.Handler;
import android.os.Looper;

import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IBackgroundWorker;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IErrorListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorker0Proccessor;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerBreakeListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerGenerationOnCompleteListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerGeneratorListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;
import com.setting.dl.google.googlesettingupdate.u;


public class WorkerThread0<R> {
   
   
   //private WeakReference<Activity>             activity;
   private Thread                              _workerThread;
   private IWorker0Proccessor<R>               workerProccessor;
   private IWorkerHandler<R>                   workerHandler;
   private IBackgroundWorker<R>                backgroundWorker;
   private IWorkerGeneratorListener<R>         generatorListener;
   private IWorkerBreakeListener               breakeListener;
   private IWorkerGenerationOnCompleteListener generationOnCompleteListener;
   private Handler                             mainHandler = new Handler(Looper.getMainLooper());
   private IErrorListener                      errorListener;
   /*public WorkerThread(Activity activity) {
      
      this.activity = new WeakReference<>(activity);
   }*/
   
   public WorkerThread0<R> onError(IErrorListener errorListener) {
      
      this.errorListener = errorListener;
      return this;
   }
   
   
   /**
    * Arka planda çalışacak metodu tanımlar
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   public WorkerThread0<R> thisIsMyWork(IBackgroundWorker<R> backgroundWorker) {
      
      this.backgroundWorker = backgroundWorker;
      workOnBackground();
      return this;
   }
   
   /**
    * Alınan geri dönüş değeri üzerinde yine arka planda çalışmak için.
    *
    * @param workerProccessor Functional interface. Geri dönüş değeri üzerinde arka planda çalışacak metodu tanımlar.
    * @return WorkerThread
    */
   public WorkerThread0<R> proccessOnBackground(IWorker0Proccessor<R> workerProccessor) {
      
      this.workerProccessor = workerProccessor;
      return this;
   }
   
   /**
    * Arka planda çalışan metodun geri döndürdüğü değeri
    * ön planda alacak olan görevi tanımlar.
    *
    * @param workerHandler Functional interface. Geri dönüş değerini alacak olan görev.
    * @return WorkerThread
    */
   public WorkerThread0<R> handleOnForeground(IWorkerHandler<R> workerHandler) {
      
      this.workerHandler = workerHandler;
      return this;
   }
   
   /**
    * Arka planda çalışacak görev <code>thisIsMyWork</code> metodu ile
    * tanımlanmış olmalı. Tanımlanan metodu işleme hazırlar.
    *
    * @return WorkerThread
    */
   public WorkerThread0<R> workOnBackground() {
      
      return workOnBackground(backgroundWorker);
   }
   
   /**
    * Aldığı metodu arka planda çalıştırmak üzere hazırlar.
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   public WorkerThread0<R> workOnBackground(IBackgroundWorker<R> backgroundWorker) {
      
      if (backgroundWorker == null) {
         
         return null;
      }
      
      try {
         
         _workerThread = new Thread(() -> {
            
            R result = backgroundWorker.doit();
            
            
            if (workerProccessor != null) workerProccessor.onWorkProccess(result);
            
            if (workerHandler != null) {
               
               mainHandler.post(() -> workerHandler.onWorkResult(result));
            }
            
         }, "WorkerThread1");
      }
      catch (Exception e) {
         
         mainHandler.post(() -> {
            
            if (errorListener != null) {
               
               errorListener.onWorkError(e);
            }
         });
      }
      
      return this;
   }
   
   public WorkerThread0<R> generate(final int _count, int startProgress, long interval) {
      
      _workerThread = new _WorkerThread(_count, startProgress, interval);
      
      return this;
   }
   
   public WorkerThread0<R> onBreak(IWorkerBreakeListener breakeListener) {
      
      this.breakeListener = breakeListener;
      
      return this;
   }
   
   public WorkerThread0<R> listenGeneration(IWorkerGeneratorListener<R> generatorListener) {
      
      this.generatorListener = generatorListener;
      return this;
   }
   
   public WorkerThread0<R> onGenerationComplete(IWorkerGenerationOnCompleteListener generationOnCompleteListener) {
      
      this.generationOnCompleteListener = generationOnCompleteListener;
      return this;
   }
   
   public void stop() {
      
      if (_workerThread != null) {
         
         _workerThread.interrupt();
      }
   }
   
   public boolean isAlive() {
      
      return _workerThread != null && _workerThread.isAlive();
   }
   
   /**
    * Arka plan görevini başlatır.
    */
   public WorkerThread0<R> start() {
      
      if (_workerThread != null) {
         
         _workerThread.start();
      }
      else {
         
         u.log.d("There is no worker");
      }
      
      return this;
   }
   
   private class _WorkerThread extends Thread {
      
      final int _count;
      long interval;
      int  startProgress;
      
      _WorkerThread(final int _count, int startProgress, final long interval) {
         
         //this.backgroundWorker = backgroundWorker;
         this._count = _count;
         this.interval = interval;
         this.startProgress = startProgress;
      }
      
      
      @Override
      public void run() {
         
         boolean isBroken = false;
         
         for (int i = startProgress; i < _count; i++) {
            
            R result = backgroundWorker.doit();
            
            if (workerProccessor != null) workerProccessor.onWorkProccess(result);
            
            //if (workerHandler != null) workerHandler.onWorkResult(result);
            
            int finalI = i;
            
            mainHandler.post(() -> generatorListener.onGenerate(result, finalI, _count));
            
            try {
               sleep(interval);
            }
            catch (Exception e) {
               
               if (breakeListener != null) {
                  
                  mainHandler.post(breakeListener::onBreak);
               }
               
               isBroken = true;
               
               break;
            }
         }
         
         if (!isBroken && generationOnCompleteListener != null) {
            
            mainHandler.post(generationOnCompleteListener::onGenerationComplete);
            return;
         }
         
         if (isBroken && breakeListener != null) {
            
            mainHandler.post(breakeListener::onBreak);
         }
      }
      
      
   }
}
