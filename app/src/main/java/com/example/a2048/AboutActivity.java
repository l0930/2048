package com.example.a2048;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.a2048.Colletctor.ActivityCollector;
import com.example.a2048.Model.User;
import com.example.a2048.SQlite.SQLiteHelper;

/**
 * @author lmw
 */
public class AboutActivity extends AppCompatActivity {
    private SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        ActivityCollector.addActivity(this);
    }
}
