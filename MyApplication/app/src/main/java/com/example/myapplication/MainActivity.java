package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{
    TextView textview; // 把視圖的元件宣告成全域變數
    String result; // 儲存資料用的字串
    String User_name, User_pass;
    Button test, btn_register;
    private final static String TAG = "HTTPURLCONNECTION test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.tv_show);
        test = findViewById(R.id.btn_show);
        btn_register = findViewById(R.id.btn_register);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Thread thread = new Thread(mutiThread);
                thread.start(); // 開始執行
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法
                ExampleDialog Dialog = new ExampleDialog();
                Dialog.show(getSupportFragmentManager(), "example dialog");
            }
        });

    }

    @Override
    public void applyTexts(String username, String password) {
        User_name = username;
        User_pass = password;
        Log.v("REGISTER_test", "ID:"+User_name+" PASS:"+User_pass);
    }
    /* ======================================== */

    // 建立一個執行緒執行的事件取得網路資料
    // Android 有規定，連線網際網路的動作都不能再主線程做執行
    // 畢竟如果使用者連上網路結果等太久整個系統流程就卡死了
    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://192.168.1.137/getdata.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線

                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode ==
                        HttpURLConnection.HTTP_OK){
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
                    Log.d("SUCCESS result", "" +": "+result);
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
                 Log.d("BAD result", "" +": "+result);
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    textview.setText(result); // 更改顯示文字
                }
            });
        }
    };

    private Runnable testThread = new Runnable(){
        public void run() {

        }
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("username", "panda");
//                jsonObject.put("password", "123456");
//
//                RequestQueue queue = Volley.newRequestQueue(getApplicationContext()); // MainActivity.this
//                JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST,
//                        "http://192.168.1.137/register_app.php",
//                        jsonObject,
//                        new com.android.volley.Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject jsonObject) {
//                                Log.d("Volley", "onResponse: jsonObject = " + jsonObject);
//                            }
//                        }, new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.d("Volley BAD", "onResponse: volleyError = " + volleyError.getMessage());
//                    }
//                });
//                queue.add(request);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    };
}