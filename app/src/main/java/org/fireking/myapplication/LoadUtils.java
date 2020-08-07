package org.fireking.myapplication;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class LoadUtils {
    static final String apkPath = "/sdcard/plugin-debug.apk";

    public static void loadClass(Context context){
        try {
            //获取pathList字段
            Class baseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = baseDexClassLoader.getDeclaredField("pathList");
            pathListField.setAccessible(true);



            /**
             * 获取插件的dexElements[]
             */
            //获取DexClassLoader类中的属性pathList值
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath,context.getCacheDir().getAbsolutePath(),null,context.getClassLoader());
            Object pluginPathList = pathListField.get(dexClassLoader);

            //获取pathList中的属性 dexElements[] 的值 （插件）
            Class pluginPathListClass = pluginPathList.getClass();
            Field pluginDexElementsField = pluginPathListClass.getDeclaredField("dexElements");
            pluginDexElementsField.setAccessible(true);
            Object[] pluginDexElements = (Object[]) pluginDexElementsField.get(pluginPathList);

            Log.e("====","=======pluginDexElements:="+pluginDexElements.length);

            /**
             * 获取宿主的dexElements[]
             */
            //获取PathClassLoader类中的属性pathList值
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Object hostPathList = pathListField.get(pathClassLoader);

            //获取pathList中的属性 dexElements[] 的值 （宿主）
            Class hostPathListClass = hostPathList.getClass();
            Field hostDexElementsField = hostPathListClass.getDeclaredField("dexElements");
            hostDexElementsField.setAccessible(true);
            Object[] hostDexElements = (Object[]) hostDexElementsField.get(hostPathList);
            Log.e("====","=======hostDexElements:="+hostDexElements.length);



            //新建Elements[]数组
            Object[] dexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(), hostDexElements.length + pluginDexElements.length);
            //copy两个数组的内容到新数组中
            System.arraycopy(pluginDexElements,0,dexElements,0,pluginDexElements.length);
            System.arraycopy(hostDexElements,0,dexElements,pluginDexElements.length,hostDexElements.length);
            //将新值赋给宿主的dexElements
            hostDexElementsField.set(hostPathList,dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
//            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
//            dexElementsField.setAccessible(true);
//
//            Class<?> classLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
//            Field pathListField = classLoaderClass.getDeclaredField("pathList");
//            pathListField.setAccessible(true);
//
//            // 1. 获取宿主的类加载器
//            ClassLoader pathClassLoader = context.getClassLoader();
//            Object hostPathList = pathListField.get(pathClassLoader);
//            // 目的：dexElements的对象
//            // new Test().print();
//            // 静态的： Test.print();
//            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);
//
//            // 2.插件,类加载器
//            // 版本  -- 7.0之后
//            ClassLoader pluginClassLoader = new DexClassLoader(apkPath,
//                    context.getCacheDir().getAbsolutePath(), null, pathClassLoader);
//            Object pluginPathList = pathListField.get(pluginClassLoader);
//            // 目的：dexElements的对象
//            // new Test().print();
//            // 静态的： Test.print();
//            Object[] pluginDexElements = (Object[]) dexElementsField.get(pluginPathList);
//
//            // 合并
//            // new Elements[];
//            Object[] newElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(),
//                    hostDexElements.length + pluginDexElements.length);
//
//            System.arraycopy(hostDexElements, 0, newElements, 0, hostDexElements.length);
//            System.arraycopy(pluginDexElements, 0, newElements, hostDexElements.length, pluginDexElements.length);
//
//            // 赋值到宿主的dexElements
//            // hostDexElements = newElements;
//            dexElementsField.set(hostPathList, newElements);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
