package tw.tcnr01.m1417;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class DBConnector {//非四大元件

    static InputStream is = null;
    static String line = null;
    static int code;
    static String mysql_code = null;
    static String result = null;
    static String connect_ip = "https://tcnr1091601.000webhostapp.com/android_mysql_connect/";//000webhost
    static String TAG = "tcnr01=>";
    static int httpstate;

    public static String executeQuery(String query_string) {
        //-----------localhost--------
//            HttpPost httpPost = new HttpPost("http://192.168.60.xx/android/android_connect_db.php");
//-------Web000 Hostinger-------
//    我的000webhost
        //HttpPost httpPost = new HttpPost("https://tcnr1702.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//    組長的000webhost
//            HttpPost httpPost = new HttpPost("https://tcnr1091601.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//            HttpPost httpPost = new HttpPost("https://tcnr1605.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//            HttpPost httpPost = new HttpPost("https://tcnr1608.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//            HttpPost httpPost = new HttpPost("https://tcnr1609.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//            HttpPost httpPost = new HttpPost("https://tcnr1624.000webhostapp.com/android_mysql_connect/android_connect_db.php");//班長// ------------------------------
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(connect_ip + "android_connect_db_all.php");
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            // selefunc_string -> 給php 使用的參數	query:選擇 insert:新增 update:更新 delete:刪除
            params.add(new BasicNameValuePair("selefunc_string", "query"));
            // query_string -> 給php 使用的參數
            params.add(new BasicNameValuePair("query_string", query_string));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // -----------------------------------------------------------------
            // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
            httpstate = httpResponse.getStatusLine().getStatusCode();
            // -----------------------------------------------------------------
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
        } catch (Exception e) {
            Log.d("TAG", "Exception e" + e.toString());
        }
        return result;

    }

    public static String executeInsert(String string, ArrayList<NameValuePair> nameValuePairs) {
        is = null;
        result = null;
        line = null;
        try {
            Thread.sleep(500); // 延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ---- 連結MySQL-------------------
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(connect_ip + "android_connect_db_all.php");
            // selefunc_string -> 給php 使用的參數	query:選擇 insert:新增 update:更新 delete:刪除
            nameValuePairs.add(new BasicNameValuePair("selefunc_string", "insert"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.d(TAG, "insert:新增錯誤1" + e.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "insert:新增錯誤2:" + e.toString());
        }
        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));
            if (code != 1) Log.d(TAG, "insert:新增錯誤3:" + "..重試..");
        } catch (Exception e) {
            Log.d(TAG, "insert:新增錯誤4:" + e.toString());
        }
        return result;

    }

    public static String executeUpdate(String string, ArrayList<NameValuePair> nameValuePairs) {
        is = null;
        result = null;
        line = null;
        String update_code = null;
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(connect_ip + "android_connect_db_all.php");
        try {
            // selefunc_string -> 給php 使用的參數	query:選擇 insert:新增 update:更新 delete:刪除
            nameValuePairs.add(new BasicNameValuePair("selefunc_string", "update"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            HttpResponse response;
            response = httpClient.execute(httpPost); //
            HttpEntity entity = response.getEntity();
            try {
                is = entity.getContent(); // InputStream is = null;
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "update:更新錯誤2:" + e.toString());
        }
        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));
            if (code == 1) {
                update_code = "更新成功";
            } else {
                update_code = "更新失敗";
            }
        } catch (Exception e) {
            Log.d(TAG, "update:更新錯誤3:" + e.toString());
        }
        return update_code;

    }


    public static String executeDelet(String string, ArrayList<NameValuePair> nameValuePairs) {
        is = null;
        result = null;
        line = null;
        mysql_code = null;
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // --------------------------------------------------------------------------------------
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(connect_ip + "android_connect_db_all.php");
        try {
            // selefunc_string -> 給php 使用的參數	query:選擇 insert:新增 update:更新 delete:刪除
            nameValuePairs.add(new BasicNameValuePair("selefunc_string", "delete"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            HttpResponse response;
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            try {
                is = entity.getContent();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "delete:刪除錯誤2:" + e.toString());
        }
        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));
            if (code == 1) {
                mysql_code = "刪除成功";
            } else {
                mysql_code = "刪除失敗";
            }
        } catch (Exception e) {
            Log.d(TAG, "delete:刪除錯誤3:" + e.toString());
        }
        return mysql_code;

    }

}

