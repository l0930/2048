package com.example.a2048;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.example.a2048.Model.User;
import com.example.a2048.SQlite.SQLiteHelper;
import com.example.a2048.View.GameView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author lmw
 */
public class PlayActivity extends Activity {
    private final String url = "http://47.94.194.71:500/";
    /**
     * 本地文件存储路径
     */
    private final String FILE_NAME = "MyData";
    /**
     * 最高分
     */
    private final String HIGHEST_SCORE = "highest_score";
    /**
     * 本地数据库同步远程的标志
     */
    private final int NEED_SYNC = 1;
    /**
     * success和 error 为handler中的判断what
     */
    public static final int success = 0;
    public static final int error = 1;
    private TextView textScore;
    private TextView textHighestScore;
    private Button buttonReplay;
    /**
     * 进程通信数据
     */
    User userSend = null;
    private int score = 0;
    private int highestScore = 0;

    private SQLiteHelper sqLiteHelper;
    public static PlayActivity playActivity;

    public PlayActivity() {
        playActivity = this;
    }

    /**
     * 消息机制，线程通信
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case success: {
                    System.out.println("同步成功");
                    Toast.makeText(PlayActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlayActivity.playActivity, MainActivity.class);
                    startActivity(intent);
                }
                break;
                case error: {
                    System.out.println("同步失败");
                    Toast.makeText(PlayActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlayActivity.playActivity, MainActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    };

    /**
     * web线程，获取数据
     */
    private class WorkedThread extends Thread {
        @Override
        public void run() {
            System.out.println("userSend-------" + userSend);
            String path = url + "insert?account=" + userSend.getAccount() + "&score=" + userSend.getScore();
            System.out.println("url-------" + path);
            String result = null;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                System.out.println("-------" + connection);
                System.out.println("开始连接");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                int code = connection.getResponseCode();
                System.out.println("响应码：" + code);
                if (code == 200) {
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int len = -1;
                    byte[] buffer = new byte[1024];
                    try {
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    inputStream.close();
                    byteArrayOutputStream.close();
                    String information = new String(byteArrayOutputStream.toByteArray());
                    Message message = new Message();
                    message.what = error;
                    if (information != null) {
                        if (1 == Integer.valueOf(information)) {
                            message.what = success;
                        }
                    }
                    handler.sendMessage(message);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println("创建URL失败");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("获取连接失败");
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        textScore = findViewById(R.id.textScore);
        textHighestScore = findViewById(R.id.textHighestScore);
        buttonReplay = findViewById(R.id.buttonReplay);
        //读取最高分
        SharedPreferences shp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        highestScore = shp.getInt(HIGHEST_SCORE, 0);
        textHighestScore.setText("HighestScore : " + highestScore);
        sqLiteHelper = new SQLiteHelper(this, "record.db", null, 1);
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView.gameView.replayGame();
            }
        });
    }

    /**
     * 提加分数，即在页面上动态更新最高分分数
     *
     * @param score
     */
    public void addScore(int score) {
        this.score += score;
        textScore.setText("Score : " + this.score);
        //更新最高分
        updateHighestScore(this.score);
    }

    /**
     * 修改最高分，从本地文件中读取
     *
     * @param score
     */
    private void updateHighestScore(int score) {
        if (score > highestScore) {
            highestScore = score;
            textHighestScore.setText("HighestScore : " + score);
            //存储最高分
            SharedPreferences shp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();

            editor.putInt(HIGHEST_SCORE, highestScore);
            editor.apply();
        }
    }

    public void clearScore() {
        score = 0;
        textScore.setText("Score : " + 0);
    }

    /**
     * 添加本地数据库，分别为三种情况
     *
     * @param user
     * @return
     */
    public int insertSQLite(User user) {
        int SQLiteState = 0;
        System.out.println(user);
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        int partScore = 0;
        String selectSql = "select score from record where account=?";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, new String[]{user.getAccount()});
//        System.out.println(cursor.getCount());
//        while (cursor.moveToNext()) {
//            System.out.println(cursor.getInt(0));
//        }
        if (cursor.getCount() == 0) {

            ContentValues values = new ContentValues();
            values.put("account", user.getAccount());
            values.put("score", user.getScore());
            long record = sqLiteDatabase.insert("record", null, values);
            System.out.println("无此人+------record-------" + record);
            SQLiteState = NEED_SYNC;
        } else {
            while (cursor.moveToNext()) {
                partScore = cursor.getInt(0);
            }
//            System.out.println(partScore);
            if (user.getScore() > partScore) {
                String whereClause = "account=?";
                String whereArgs[] = new String[]{user.getAccount()};
                ContentValues values = new ContentValues();
                values.put("score", user.getScore());
                int record = sqLiteDatabase.update("record", values, whereClause, whereArgs);
                System.out.println("有此人,切破纪录--------record----------" + record);
                SQLiteState = NEED_SYNC;
            } else {
                System.out.println("有此人，但是没破纪录");
            }
        }
        sqLiteDatabase.close();
        return SQLiteState;
    }

    /**
     * 游戏结束，显示输入框，用户输入用户名，然后提交数据
     */
    public void over() {
        final EditText editText = new EditText(PlayActivity.playActivity);
        AlertDialog alertDialog = new AlertDialog
                .Builder(PlayActivity.playActivity)
                .setTitle("您的分数是：" + score)
                .setMessage("请输入您的用户名：")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        User user = new User(s, score);
                        System.out.println("User-------" + user);
                        int state = insertSQLite(user);
                        if (NEED_SYNC == state) {
                            userSend = user;
                            new WorkedThread().start();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }).create();
        alertDialog.show();
    }
}
