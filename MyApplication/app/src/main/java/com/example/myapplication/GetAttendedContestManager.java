package com.example.myapplication;

import android.util.Log;

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

public class GetAttendedContestManager {
    private JSONArray result_arr;
    private String result;

    public JSONArray getAttendedContest(String participant_name){
        GetAttendedContestRunnable GetAttendedContest = new GetAttendedContestRunnable();
        GetAttendedContest.setParams(participant_name);
        Thread thread = new Thread(GetAttendedContest);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result_arr;
    };
    private class GetAttendedContestRunnable implements Runnable {
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
                        String deadline = dataObject.getString("deadline");
                        String award_name = dataObject.getString("award_name");
                        String award_avatar = dataObject.getString("award_avatar");

                        int type = dataObject.getInt("type");
                        int expected_amount = dataObject.getInt("expected_amount");
                        int award_score = dataObject.getInt("award_score");

                        // 输出 participant_name 和 score
                        Log.v("type: " , ""+ type);
                        Log.v("deadline: " , ""+ deadline);
                        Log.v("expected_amount: " , ""+ expected_amount);
                        Log.v("award_name: " , ""+ award_name);
                        Log.v("award_score: " , ""+ award_score);
                        Log.v("award_avatar: " , ""+ award_avatar);

                        // 在此处可以根据需要将 participant_name 和 score 存入你的 HashMap
                    }
                    Log.d("HTTP_output", "output!");
                    Log.d("SUCCESS result", "" +": "+result);
//                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
//                    InputStream inputStream =
//                            connection.getInputStream();
//                    // 取得輸入串流
//                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
//                    // 讀取輸入串流的資料
//                    String box = ""; // 宣告存放用字串
//                    String line = null; // 宣告讀取用的字串
//                    while((line = bufReader.readLine()) != null) {
//                        box += line + "\n";
//                        // 每當讀取出一列，就加到存放字串後面
//                    }
//                    inputStream.close(); // 關閉輸入串流
//                    result = box; // 把存放用字串放到全域變數
//                    Log.d("HTTP_output", "output!");
//                    Log.d("SUCCESS result", "" +": "+result);
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
