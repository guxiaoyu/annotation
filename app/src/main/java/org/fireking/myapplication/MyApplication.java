package org.fireking.myapplication;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicStampedReference;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        LoadUtils.loadClass(this);

        HookUtils.hookAMS();
        HookUtils.hookHandler();



    }
    AtomicStampedReference<Integer> stampedReference;
    @Override
    public void onCreate() {
        super.onCreate();
        stampedReference = new AtomicStampedReference<>(100,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int stamp = stampedReference.getStamp();
                System.out.println("=================="+Thread.currentThread().getName() + " 的版本号为：" + stamp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stampedReference.compareAndSet(100, 101, stampedReference.getStamp(), stampedReference.getStamp() + 1 );
                stampedReference.compareAndSet(101, 100, stampedReference.getStamp(), stampedReference.getStamp() + 1 );
            }
        },"A").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int stamp = stampedReference.getStamp();
                System.out.println("=================="+Thread.currentThread().getName() + " 的版本号为：" + stamp);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean b = stampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
                System.out.println("=================="+b); // false
                System.out.println("=================="+stampedReference.getReference()); // 100
                System.out.println("=================="+stampedReference.getStamp()); // 3
            }
        },"B").start();
    }
}
