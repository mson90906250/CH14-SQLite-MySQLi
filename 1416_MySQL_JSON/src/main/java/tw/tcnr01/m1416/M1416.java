package tw.tcnr01.m1416;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class M1416 extends AppCompatActivity implements View.OnClickListener {
    String sqlctl;
    String TAG = "tcnr01=>";
    private Button b001, b002, b003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1416);
        setupViewComponent();
//-------------抓取遠端資料庫設定執行續------------------------------
        StrictMode.setThreadPolicy(new
                StrictMode.
                        ThreadPolicy.Builder().
                detectDiskReads().
                detectDiskWrites().
                detectNetwork().
                penaltyLog().
                build());
        StrictMode.setVmPolicy(
                new
                        StrictMode.
                                VmPolicy.
                                Builder().
                        detectLeakedSqlLiteObjects().
                        penaltyLog().
                        penaltyDeath().
                        build());
//---------------------------------------------------------------------
    }

    private void setupViewComponent() {
        b001 = (Button) findViewById(R.id.button1);
        b002 = (Button) findViewById(R.id.button2);
        b003 = (Button) findViewById(R.id.button3);
        b001.setOnClickListener(this);
        b002.setOnClickListener(this);
        b003.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                sqlctl = "SELECT * FROM member ORDER BY id ASC";
                Mysqlsel(sqlctl);
                break;
            case R.id.button2:
                sqlctl = "SELECT * FROM area ORDER BY id ASC";
                Mysqlsel(sqlctl);
                break;
            case R.id.button3:
                sqlctl = "SELECT member.id, member.name, member.grp, member.address, area.location FROM member, area "
                        + "WHERE member.id = area.fid ORDER BY  member.id ASC";
                Mysqlsel(sqlctl);
                break;
        }
    }


    private void Mysqlsel(String sqlctl) {
//--設定layout tablelyout-----------------------------
        TableLayout user_list = (TableLayout) findViewById(R.id.user_list);
        user_list.removeAllViews();
        user_list.setStretchAllColumns(true);
        TableLayout.LayoutParams row_layout =
                new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams view_layout =
                new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
//------------------------------------------------------
        try {
            String result = DBConnector.executeQuery(sqlctl);
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            //幾筆資料
            JSONArray jsonArray = new JSONArray(result);
            // ---
            //幾個欄位
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                // // 取出 jsonObject 中的字段的值的空格
                Iterator itt = jsonData.keys();
                TableRow tr = new TableRow(M1416.this);
                tr.setLayoutParams(row_layout);
//    tr.setGravity(Gravity.CENTER_HORIZONTAL);
                while (itt.hasNext()) {
                    String key = itt.next().toString();
                    String value = jsonData.getString(key);
                    if (value == null) {
                        continue;
                    } else if ("".equals(value.trim())) {
                        continue;
                    } else {
                        jsonData.put(key, value.trim());
                    }
                    // --
                    TextView tv = new TextView(M1416.this);// tv 繼承TextView
                    tv.setId(i); // 寫入配置碼ID 代號
                    tv.setText(value);
                    tv.setLayoutParams(view_layout);
                    tr.addView(tv);
                }
                user_list.addView(tr);
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.m1416, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}