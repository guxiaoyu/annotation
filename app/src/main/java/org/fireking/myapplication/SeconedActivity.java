package org.fireking.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import org.fireking.routerlibrary.annotation.Router;

@Router(path = "main/main02")
public class SeconedActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("======","=====我是跳转类=========");
    }
}
