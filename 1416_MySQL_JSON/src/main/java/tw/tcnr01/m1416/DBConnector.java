package tw.tcnr01.m1416;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DBConnector {//非四大元件
    public static String executeQuery(String query_string) {
        String result = "";
        String TAG = "oldpa=>";
        try {
            HttpClient httpClient = new DefaultHttpClient();
//-----------localhost--------
//            HttpPost httpPost = new HttpPost("http://192.168.60.xx/android/android_connect_db.php");
//-------Hostinger-------
            //HttpPost httpPost = new HttpPost("http://xxxxxxx.esy.es/android/android_connect_db.php");

//-------000webhost   組長----------
            //HttpPost httpPost = new HttpPost("https://tcnr1091601.000webhostapp.com/android_mysql_connect/android_connect_db.php");
            //HttpPost httpPost = new HttpPost("https://tcnr1605.000webhostapp.com/android_mysql_connect/android_connect_db.php");
            //HttpPost httpPost = new HttpPost("https://tcnr1608.000webhostapp.com/android_mysql_connect/android_connect_db.php");
            //HttpPost httpPost = new HttpPost("https://tcnr1609.000webhostapp.com/android_mysql_connect/android_connect_db.php");
//-------000webhost   班代----------
           HttpPost httpPost = new HttpPost("https://tcnr1624.000webhostapp.com/android_mysql_connect/android_connect_db.php");



            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", query_string));
            // query_string -> 給php 使用的參數
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
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
            Log.d(TAG, "Exception e" + e.toString());
        }
        return result;
    }
}

