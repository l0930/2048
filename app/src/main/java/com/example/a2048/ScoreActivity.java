package com.example.a2048;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.a2048.Colletctor.ActivityCollector;
import com.example.a2048.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lmw
 * 分数排行榜页面
 */
public class ScoreActivity extends AppCompatActivity {
    public final String url = "http://47.94.194.71:500/";
    public static final int success = 0;
    public static final int error = 1;
    public ListView listView;
    public List<User> users;
    /**
     * 消息机制，用于实现web线程何主线程之间的通信
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case success: {
                    users = (List<User>) msg.obj;
                    users.forEach(System.out::println);
                    listView.setAdapter(new MyAdapter());
                }
                ;
                break;
                case error: {
                    Toast.makeText(ScoreActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                ;
                break;
            }
        }
    };

    /**
     * ListView适配器
     */
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = View.inflate(ScoreActivity.this, R.layout.item, null);
            } else {
                view = convertView;
            }
            TextView account = view.findViewById(R.id.accountText);
            TextView score = view.findViewById(R.id.scoreText);
            User user = users.get(position);
            System.out.println(user);
            account.setText(user.getAccount());
            score.setText(user.getScore() + "");
            return view;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoer_activity);
        ActivityCollector.addActivity(this);
        listView = findViewById(R.id.rankList);
        new WorkThread().start();
    }


    /**
     * 联网获取数据，然后传给主线程
     */
    private class WorkThread extends Thread {
        @Override
        public void run() {
            String path = url + "select";
            System.out.println(path);
            String result = null;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                System.out.println("-------" + connection);
//                System.out.println("开始连接");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                int code = connection.getResponseCode();
//                System.out.println("响应码：" + code);
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
                    if (information != null) {
                        Gson gson = new Gson();
                        Type userListType = new TypeToken<ArrayList<User>>() {
                        }.getType();
                        List<User> users = gson.fromJson(information, userListType);
//                        users.forEach(System.out::println);
                        Message message = new Message();
                        message.what = error;
                        message.obj = null;
                        if (!users.isEmpty()) {
                            message.what = success;
                            message.obj = users;
                            handler.sendMessage(message);
                        }
                    }
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
}
