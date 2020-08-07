package org.fireking.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class HookUtils {
    private static String TARGET_INTENT = "target_Intent";

    public static void hookAMS(){
        //需要替换的是 IActivityTaskManager
        try {
            //获取singleton对象
            Class<?> clazz = Class.forName("android.app.ActivityManager");//获取到ActivityTaskManager类
            Field iActivityTaskManagerSingleton = clazz.getDeclaredField("IActivityManagerSingleton");//获取到IActivityTaskManagerSingleton域
            iActivityTaskManagerSingleton.setAccessible(true);
            Object singleton = iActivityTaskManagerSingleton.get(null);


            //获取mInstance
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            final Object mInstance = mInstanceField.get(singleton);


            //获取IActivityTaskManager   ActivityTaskManager.getService()
            Class<?> iActivityTaskManagerClass = Class.forName("android.app.IActivityManager");
            //创建IActivityTaskManager代理类
            Object mInstanceProxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityTaskManagerClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    /**
                     *  ActivityTaskManager.getService()
                     *                 .startActivity(whoThread, who.getBasePackageName(), intent,
                     *                         intent.resolveTypeIfNeeded(who.getContentResolver()),
                     *                         token, target != null ? target.mEmbeddedID : null,
                     *                         requestCode, 0, null, options);
                     */
                    if ("startActivity".equals(method.getName())) {
                        //修改intent
                        int index = 0;
                        for (int i = 0; i < args.length; i++) {
                            //找到 原intent 位置
                            if (args[i] instanceof Intent) {
                                index = i;
                                break;
                            }
                        }

                        Intent intent = (Intent) args[index];

                        //构建插件intent  修改成启动代理Intent
                        Intent proxyIntent = new Intent();
                        proxyIntent.setClassName("org.fireking.myapplication", "org.fireking.myapplication.ProxyActivity");
                        proxyIntent.putExtra(TARGET_INTENT, intent);

                        args[index] = proxyIntent;

                    }
                    //第一个参数  系统的ActivityTaskManager对象
                    return method.invoke(mInstance, args);
                }
            });

            //用代理对象替换系统对象
            mInstanceField.set(singleton,mInstanceProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler(){
        //系统的callback对象 --- ActivityThread mH
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadField.get(null);//静态变量

            //获取mH
            Field mHField = clazz.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(sCurrentActivityThread);

            //反射Handler的callback对象
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Field mCallbackField = handlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);


            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    switch (msg.what){
                        case 100:
                            //  final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
                            try {
                                Field intentField = msg.obj.getClass().getDeclaredField("intent");
                                intentField.setAccessible(true);

                                Intent intentProxy = (Intent) intentField.get(msg.obj);
                                Intent intent = intentProxy.getParcelableExtra(TARGET_INTENT);
                                if (intent!=null){
                                    intentField.set(msg.obj,intent);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 159:
                            //  final ClientTransaction transaction = (ClientTransaction) msg.obj;
                            Class<?> transactionClass = msg.obj.getClass();
                            //     final List<ClientTransactionItem> callbacks = transaction.getCallbacks();
                            //     ClientTransaction的mActivityCallbacks
                            try {
                                Field mActivityCallbacksField = transactionClass.getDeclaredField("mActivityCallbacks");
                                mActivityCallbacksField.setAccessible(true);
                                List callbacksList = (List) mActivityCallbacksField.get(msg.obj);

                                for (int i=0;i<callbacksList.size();i++){
                                    //LaunchActivityItem
                                    if (callbacksList.get(i).getClass().getName().equals("android.app.servertransaction.LaunchActivityItem")){
                                        Object launchActivityItem = callbacksList.get(i);
                                        Field mIntentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                                        mIntentField.setAccessible(true);
                                        Intent intentProxy = (Intent) mIntentField.get(launchActivityItem);
                                        Log.e("=========","=================intentProxy==="+intentProxy);
                                        Intent intent = intentProxy.getParcelableExtra(TARGET_INTENT);

                                        if (intent!=null){
                                            Log.e("=========","=================TARGET_INTENT==="+intent);

                                            mActivityCallbacksField.set(launchActivityItem,intent);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            break;
                    }
                    //拿到msg的obj对应的intent
                    Object obj = msg.obj;

                    return false;
                }
            };


            mCallbackField.set(mH,callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
