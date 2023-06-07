package com.example.myapplication;

import android.util.Log;

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

public class AddAttendanceManager {
    private String result;
    public String add_Attendance(String participant_name, String contest_name, int score){
        AddAttendanceRunnable addAttendance = new AddAttendanceRunnable();
        addAttendance.setParams(participant_name, contest_name, score);
        Thread thread = new Thread(addAttendance);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    };
    private class AddAttendanceRunnable implements Runnable {
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
