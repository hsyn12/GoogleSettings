package com.setting.dl.google.googlesettingupdate;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Run<T> {
    
    private final Callable<T>         callable;
    private final CallableCallback<T> callback;
    private final Activity            activity;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public Run(
            @Nullable final Activity activity,
            @NonNull final Callable<T> callable,
            @NonNull final CallableCallback<T> callback) {
        
        this.callable = callable;
        this.callback = callback;
        this.activity = activity;
        
        new Thread(this::run).start();
    }
    
    private void run() {
    
        Future<T> future      = executorService.submit(callable);
        T         returnValue = null;
    
        try {
        
            returnValue = future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    
    
        executorService.shutdown();
    
        if (activity != null) {
        
            T finalReturnValue = returnValue;
            activity.runOnUiThread(() -> callback.call(finalReturnValue));
        }
        else {
        
            callback.call(returnValue);
        }
    }
    
    public interface CallableCallback<T> {
        
        void call(@Nullable T returnValue);
    }
    
    public static void run(final Runnable runnable, final long delay) {
        
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }
    
    public static void run(final Runnable runnable) {
        
        new Handler(Looper.getMainLooper()).post(runnable);
    }
    
    public static Thread runThread(final Runnable runnable){
    
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, 0L);*/
        
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
        
    }
    
    public static void runThread(final Runnable runnable, final long delay){
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
        
    }
    
    public static void runOnMainThread(Activity activity, Runnable runnable) {
        
        activity.runOnUiThread(runnable);
    }
    
}
