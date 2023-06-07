package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{
    private EditText editTextUsername;
    private EditText editTextPassword;
    TextView textview; // 把視圖的元件宣告成全域變數
    String result; // 儲存資料用的字串
    JSONArray result_arr;
    String User_name, User_pass, login_User_name, login_User_pass;
    Button btn_login, btn_register;
    private final static String TAG = "HTTPURLCONNECTION test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textview = findViewById(R.id.tv_show);
        btn_login = findViewById(R.id.btn_show);
        btn_register = findViewById(R.id.btn_register);
        editTextUsername = findViewById(R.id.edit_username);
        editTextPassword = findViewById(R.id.edit_password);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
//                login_User_name = editTextUsername.getText().toString();
//                login_User_pass = editTextPassword.getText().toString();
//                Thread thread = new Thread(mutiThread);
//                thread.start(); // 開始執行
//                add_Contest("我想睡覺好累==", 1, "12:30:00", 30, "King", 5, "test.png");
//                add_Attendance("sally", "我想睡覺好累==", 0);
//                add_Friend("sally", "yicheng");
//                getContestParticipant("再睡5分鐘");
//                getAttendedContest("sally");
//                getFriend("marow");
                getAward("marow");
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
                URL url = new URL("http://140.116.82.9:9000/login.php");
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
                user.put("username", login_User_name);
                user.put("userpass", login_User_pass);
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

            // 當這個執行緒完全跑完後執行
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    textview.setText(result); // 更改顯示文字
//                }
//            });
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

    public class AddContestRunnable implements Runnable {
        private String group_name;
        private int type;
        private String deadline;
        private int expected_amount;
        private String award_name;
        private int award_score;
        private String award_avatar;

        public void setParams(String group_name, int type, String deadline, int expected_amount, String award_name, int award_score, String award_avatar) {
            this.group_name = group_name;
            this.type = type;
            this.deadline = deadline;
            this.expected_amount = expected_amount;
            this.award_name = award_name;
            this.award_score = award_score;
            this.award_avatar = award_avatar;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/addContest.php");
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
                Map<String, Object> contestData = new HashMap<>();
                contestData.put("group_name", group_name);
                contestData.put("type", type);
                contestData.put("deadline", deadline);
                contestData.put("expected_amount", expected_amount);
                contestData.put("award_name", award_name);
                contestData.put("award_score", award_score);
                contestData.put("award_avatar", award_avatar);

                JSONObject response_j = new JSONObject(contestData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    // 先将返回的字符串中的 <br> 替换为空字符串
//                    String cleanedResponse = response.replace("<br>", "");
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
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
    }
    private String add_Contest(String group_name, int type, String deadline, int expected_amount, String award_name, int award_score, String award_avatar){
        AddContestRunnable addContest = new AddContestRunnable();
        addContest.setParams(group_name, type, deadline, expected_amount, award_name, award_score, award_avatar);
        Thread thread = new Thread(addContest);
        thread.start();
        return result;
    };

    public class AddAttendanceRunnable implements Runnable {
        private String participant_name;
        private String contest_name;
        private int score;

        public void setParams(String participant_name, String contest_name, int score) {
            this.participant_name = participant_name;
            this.contest_name = contest_name;
            this.score = score;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/addAttendance.php");
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
                Map<String, Object> AttendanceData = new HashMap<>();
                AttendanceData.put("participant_name", participant_name);
                AttendanceData.put("contest_name", contest_name);
                AttendanceData.put("score", score);

                JSONObject response_j = new JSONObject(AttendanceData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    // 先将返回的字符串中的 <br> 替换为空字符串
//                    String cleanedResponse = response.replace("<br>", "");
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
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
    }
    private String add_Attendance(String participant_name, String contest_name, int score){
        AddAttendanceRunnable addAttendance = new AddAttendanceRunnable();
        addAttendance.setParams(participant_name, contest_name, score);
        Thread thread = new Thread(addAttendance);
        thread.start();
        return result;
    };

    public class AddFriendRunnable implements Runnable {
        private String my_name;
        private String friend_name;

        public void setParams(String my_name, String friend_name) {
            this.my_name = my_name;
            this.friend_name = friend_name;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/addFriend.php");
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
                Map<String, Object> FriendData = new HashMap<>();
                FriendData.put("my_name", my_name);
                FriendData.put("friend_name", friend_name);

                JSONObject response_j = new JSONObject(FriendData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
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
    }
    private String add_Friend(String my_name, String friend_name){
        AddFriendRunnable addFriend = new AddFriendRunnable();
        addFriend.setParams(my_name, friend_name);
        Thread thread = new Thread(addFriend);
        thread.start();
        return result;
    };


    public class GetContestParticipantRunnable implements Runnable {
        private String contest_name;

        public void setParams(String contest_name) {
            this.contest_name = contest_name;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/getContestParticipant.php");
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
                Map<String, Object> FriendData = new HashMap<>();
                FriendData.put("contest_name", contest_name);

                JSONObject response_j = new JSONObject(FriendData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    result_arr = dataArray;
                    //讀取output的方法
                    for (int i = 0; i < dataArray.length(); i++) {
                        // 获取当前数组元素（一个 JSONObject）
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        // 从 dataObject 中获取 participant_name 和 score 值
                        String participantName = dataObject.getString("participant_name");
                        int score = dataObject.getInt("score");

                        // 输出 participant_name 和 score
                        Log.v("Participant Name: " , ""+ participantName);
                        Log.v("Score: ", ""+score);

                        // 在此处可以根据需要将 participant_name 和 score 存入你的 HashMap
                    }
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
    }
    private JSONArray getContestParticipant(String contest_name){
        GetContestParticipantRunnable GetContestParticipant = new GetContestParticipantRunnable();
        GetContestParticipant.setParams(contest_name);
        Thread thread = new Thread(GetContestParticipant);
        thread.start();
        return result_arr;
    };

    public class GetAttendedContestRunnable implements Runnable {
        private String participant_name;

        public void setParams(String participant_name) {
            this.participant_name = participant_name;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/getAttendedContest.php");
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
                Map<String, Object> FriendData = new HashMap<>();
                FriendData.put("participant_name", participant_name);

                JSONObject response_j = new JSONObject(FriendData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    result_arr = dataArray;
                    //讀取output的方法
                    for (int i = 0; i < dataArray.length(); i++) {
                        // 获取当前数组元素（一个 JSONObject）
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        // 从 dataObject 中获取 participant_name 和 score 值
                        String ContestName = dataObject.getString("contest_name");

                        // 输出 participant_name 和 score
                        Log.v("ContestName: " , ""+ ContestName);

                        // 在此处可以根据需要将 participant_name 和 score 存入你的 HashMap
                    }
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
    }
    private JSONArray getAttendedContest(String participant_name){
        GetAttendedContestRunnable GetAttendedContest = new GetAttendedContestRunnable();
        GetAttendedContest.setParams(participant_name);
        Thread thread = new Thread(GetAttendedContest);
        thread.start();
        return result_arr;
    };
    public class GetFriendRunnable implements Runnable {
        private String my_name;

        public void setParams(String my_name) {
            this.my_name = my_name;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/getFriend.php");
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
                Map<String, Object> FriendData = new HashMap<>();
                FriendData.put("my_name", my_name);

                JSONObject response_j = new JSONObject(FriendData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    result_arr = dataArray;
                    //讀取output的方法
                    for (int i = 0; i < dataArray.length(); i++) {
                        // 获取当前数组元素（一个 JSONObject）
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        // 从 dataObject 中获取 my_name 和 score 值
                        String FriendName = dataObject.getString("friend_name");

                        // 输出 my_name
                        Log.v("FriendName: " , ""+ FriendName);

                    }
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
    }
    private JSONArray getFriend(String my_name){
        GetFriendRunnable GetFriend = new GetFriendRunnable();
        GetFriend.setParams(my_name);
        Thread thread = new Thread(GetFriend);
        thread.start();
        return result_arr;
    };
    public class GetAwardRunnable implements Runnable {
        private String user_name;

        public void setParams(String user_name) {
            this.user_name = user_name;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://140.116.82.9:9000/getAward.php");
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
                Map<String, Object> FriendData = new HashMap<>();
                FriendData.put("user_name", user_name);

                JSONObject response_j = new JSONObject(FriendData);
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = bufferedReader.readLine();

                    // 将响应内容转换为 JSON 对象
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    result = status;
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    result_arr = dataArray;
                    //讀取output的方法
                    for (int i = 0; i < dataArray.length(); i++) {
                        // 获取当前数组元素（一个 JSONObject）
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        // 从 dataObject 中获取 user_name 和 score 值
                        String AwardAvatar = dataObject.getString("award_avatar");

                        // 输出 user_name
                        Log.v("AwardAvatar: " , ""+ AwardAvatar);

                    }
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
    }
    private JSONArray getAward(String user_name){
        GetAwardRunnable GetAward = new GetAwardRunnable();
        GetAward.setParams(user_name);
        Thread thread = new Thread(GetAward);
        thread.start();
        return result_arr;
    };
}