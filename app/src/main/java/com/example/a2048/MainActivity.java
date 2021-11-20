package com.example.a2048;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lmw
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                Toast.makeText(this, "已经获取该权限", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.INTERNET,
                        },
                        1);
            }
        }
    }

    public void gotoPlay(View view) {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        startActivity(intent);
    }

    public void gotoRank(View view) {
        Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
        startActivity(intent);
    }

    public void about(View view) {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    public void exit(View view) {
        this.finish();
        if (PlayActivity.playActivity != null) {
            PlayActivity.playActivity.finish();
        }
        System.exit(0);
    }
}

