package org.fireking.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.fireking.routerlibrary.annotation.Router;

@Router(path = "/main/main01")
public class MainActivity extends AppCompatActivity {

    TextView tv_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_button = findViewById(R.id.tv_button);
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("=====","========="+ ClassLoader.getSystemClassLoader());
//                ClassLoader classLoader = getClassLoader();
//                while (classLoader!=null){
//                    Log.e("======","======while:"+classLoader);
//                    classLoader = classLoader.getParent();
//                }
//                Log.e("=====","======Activity==="+ Activity.class.getClassLoader());
//                Log.e("=====","======AppCompatActivity==="+ AppCompatActivity.class.getClassLoader());
//
//                try {
//                    Class<?> clazz = Class.forName("org.fireking.plugin.Test");
//                    Log.e("=====","=====clazz="+clazz);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }

//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("org.fireking.myapplication","org.fireking.myapplication.SeconedActivity"));
//                startActivity(intent);

                startActivity(new Intent(MainActivity.this,SeconedActivity.class));
            }
        });
    }



}
