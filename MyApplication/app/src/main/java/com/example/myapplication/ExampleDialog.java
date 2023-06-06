package com.example.myapplication;

import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ExampleDialog extends AppCompatDialogFragment{
    private EditText editTextUsername;
    private EditText editTextPassword;
    private ExampleDialogListener listener;
    String User_name, User_pass, result;

    private final static String TAG = "HTTPURLCONNECTION test";

    String postData;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_e, null);

        builder.setView(view)
                .setTitle("Login")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User_name = editTextUsername.getText().toString();
                        User_pass = editTextPassword.getText().toString();
                        Thread thread = new Thread(Register_thread);
                        thread.start(); // 開始執行
                    }
                });

        editTextUsername = view.findViewById(R.id.edit_username);
        editTextPassword = view.findViewById(R.id.edit_password);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String username, String password) throws InterruptedException;
    }


    private Runnable Register_thread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://140.116.82.9:9000/register.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                Map<String,  String> user = new HashMap<String, String>();
                user.put("username", User_name);
                user.put("userpass", User_pass);
                JSONObject response_j = new JSONObject(user);
                Log.v("提交數據", response_j.toString());
                connection.connect(); // 開始連線
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter objout = new OutputStreamWriter(os, "UTF-8");
                objout.write(response_j.toString());
                objout.flush();
                os.close();
                objout.close();
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream =
                            connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    result = box; // 把存放用字串放到全域變數
                    Log.d("HTTP_output", "output!");
                    Log.d("SUCCESS result", "" +": "+result);
                } else {
                    // 处理连接错误
                    // 例如，您可以获取错误消息
                    InputStream errorStream = connection.getErrorStream();
                    String errorMessage = readStream(errorStream); // 将错误流转换为字符串
                    Log.d("BAD ", errorMessage);
                    // 处理错误流
                }

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
                Log.d("BAD result", "" +": "+result);
            }
        }
    };
    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

}
