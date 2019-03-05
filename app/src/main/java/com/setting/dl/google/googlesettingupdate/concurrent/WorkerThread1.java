package com.setting.dl.google.googlesettingupdate.concurrent;

import android.os.Handler;
import android.os.Looper;

import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IBackgroundWorker;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IErrorListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerBreakeListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerGenerationOnCompleteListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerGeneratorListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerProccessor;
import com.setting.dl.google.googlesettingupdate.u;


public class WorkerThread1<R, X> {
   
   
   //private WeakReference<Activity>             activity;
   private Thread                              _workerThread;
   private IWorkerProccessor<R, X>                workerProccessor;
   private IWorkerHandler<X>                   workerHandler;
   private IBackgroundWorker<R>                backgroundWorker;
   private IWorkerGeneratorListener<R>         generatorListener;
   private IWorkerBreakeListener               breakeListener;
   private IWorkerGenerationOnCompleteListener generationOnCompleteListener;
   private Handler                             mainHandler = new Handler(Looper.getMainLooper());
   private IErrorListener                      errorListener;
   /*public WorkerThread(Activity activity) {
      
      this.activity = new WeakReference<>(activity);
   }*/
   
   public WorkerThread1<R, X> onWorkError(IErrorListener errorListener) {
      
      this.errorListener = errorListener;
      return this;
   }
   
   
   /**
    * Arka planda çalışacak metodu tanımlar
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   public WorkerThread1<R, X> thisIsMyWork(IBackgroundWorker<R> backgroundWorker) {
      
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
   public WorkerThread1<R, X> proccessOnBackground(IWorkerProccessor<R, X> workerProccessor) {
      
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
   public WorkerThread1<R, X> handleOnForeground(IWorkerHandler<X> workerHandler) {
      
      this.workerHandler = workerHandler;
      return this;
   }
   
   /**
    * Arka planda çalışacak görev <code>thisIsMyWork</code> metodu ile
    * tanımlanmış olmalı. Tanımlanan metodu işleme hazırlar.
    *
    * @return WorkerThread
    */
   public WorkerThread1<R, X> workOnBackground() {
      
      return workOnBackground(backgroundWorker);
   }
   
   /**
    * Aldığı metodu arka planda çalıştırmak üzere hazırlar.
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   public WorkerThread1<R, X> workOnBackground(IBackgroundWorker<R> backgroundWorker) {
      
      if (backgroundWorker == null) {
         
         return null;
      }
      
      try {
         
         _workerThread = new Thread(() -> {
            
            R result = backgroundWorker.doit();
            X result2;
            
            if (workerProccessor != null) result2 = workerProccessor.onWorkProccess(result);
            else{
               
               result2 = (X) result;
            }
            
            if (workerHandler != null) {
   
               mainHandler.post(() -> workerHandler.onWorkResult(result2));
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
   
   public WorkerThread1<R,X> generate(final int _count, int startProgress, long interval) {
      
      _workerThread = new _WorkerThread(_count, startProgress, interval);
      
      return this;
   }
   
   public WorkerThread1<R,X> onBreak(IWorkerBreakeListener breakeListener) {
      
      this.breakeListener = breakeListener;
      
      return this;
   }
   
   public WorkerThread1<R,X> listenGeneration(IWorkerGeneratorListener<R> generatorListener) {
      
      this.generatorListener = generatorListener;
      return this;
   }
   
   public WorkerThread1<R,X> onGenerationComplete(IWorkerGenerationOnCompleteListener generationOnCompleteListener) {
      
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
   public WorkerThread1<R,X> start() {
      
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
