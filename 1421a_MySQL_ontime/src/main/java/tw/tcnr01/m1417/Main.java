package tw.tcnr01.m1417;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tw.tcnr01.m1417.providers.FriendsContentProvider;


public class Main extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    String TAG = "tcnr01=>";
    private TextView count_t;
    private EditText b_id,b_name, b_grp, b_address, e004;

    private static final String DB_FILE = "friends.db";
    private static final String DB_TABLE = "member";
    private static final int DBversion = 1;
    //-----------------
    private TextView tvTitle;
    private Button btNext, btPrev, btTop, btEnd;
    private ArrayList<String> recSet;
    private int index = 0;
    String msg = null;
    //--------------------------
    private float x1; // 觸控的 X 軸位置
    private float y1; // 觸控的 Y 軸位置
    private float x2;
    private float y2;
    int range = 50; // 兩點距離
    int ran = 60; // 兩點角度
    private Button btEdit, btDel;
    String tname, tgrp, taddr,taddress;
    private Spinner mSpnName;
    int up_item = 0;
    //------------------------------
    protected static final int BUTTON_POSITIVE = -1;
    protected static final int BUTTON_NEGATIVE = -2;
    private Button btAdd, btAbandon, btquery, btcancel, btreport;

    //--------------------------
    private static ContentResolver mContRes;
    private String[] MYCOLUMN = new String[]{"id", "name", "grp", "address"};
    int tcount;
    // ------------------
    public static String myselection = "";
    public static String myargs[] = new String[]{};
    public static String myorder = "id ASC"; // 排序欄位
    private RelativeLayout brelative01;
    private LinearLayout blinear02;
    private ListView listView;
    private TextView bsubTitle,b_editon;

    private MenuItem mGroup1, mGroup2;
    private Menu menu;
    private boolean flag; //關閉ontuchevent
    //-----------------------------------------
    private SwipeRefreshLayout laySwipe;
    private String s_id;

    // ----------定時更新--------------------------------
    private Long startTime;
    private Handler handler = new Handler();
    int autotime = 10;// 要幾秒的時間 更新匯入MySQL資料
    // ------------------
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private TextView nowtime;  //顯示更新時間及次數
    int old_index = 0;
    int update_time = 0;
    String showip;   //顯示手機ip
    private boolean touch_flag = true; //ontuchevent開關
    private RelativeLayout b_Relbutton;

    private boolean edittype = false; //控制編輯狀況
    private MenuItem b_m_add,b_m_query,b_m_batch,b_m_list,b_m_mysql,b_m_edit_start,b_m_edit_stop,b_m_return,getB_m_batch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        touch_flag = true; ////開啟ontuchevent
        tvTitle = (TextView) findViewById(R.id.tvIdTitle);
        b_id = (EditText) findViewById(R.id.edid);
        b_name = (EditText) findViewById(R.id.edtName);
        b_grp = (EditText) findViewById(R.id.edtGrp);
        b_address = (EditText) findViewById(R.id.edtAddr);
        count_t = (TextView) findViewById(R.id.count_t);

        btNext = (Button) findViewById(R.id.btIdNext);
        btPrev = (Button) findViewById(R.id.btIdPrev);
        btTop = (Button) findViewById(R.id.btIdtop);
        btEnd = (Button) findViewById(R.id.btIdend);

        btEdit = (Button) findViewById(R.id.btnupdate);
        btDel = (Button) findViewById(R.id.btIdDel);
        //-----------------------
        btAdd = (Button) findViewById(R.id.btnAdd);
        btAbandon = (Button) findViewById(R.id.btnabandon);
        btquery = (Button) findViewById(R.id.btnquery);
        btcancel = (Button) findViewById(R.id.btidcancel);
        btreport = (Button) findViewById(R.id.btnlist);

        brelative01 = (RelativeLayout) findViewById(R.id.relative01);
        blinear02 = (LinearLayout) findViewById(R.id.linear02);
        b_Relbutton = (RelativeLayout) findViewById(R.id.Relbutton);
        listView = (ListView) findViewById(R.id.listView);
        bsubTitle = (TextView) findViewById(R.id.subTitle);
        b_editon = (TextView) findViewById(R.id.edit_on);

        //監聽-----------------------
        btNext.setOnClickListener(this);
        btPrev.setOnClickListener(this);
        btTop.setOnClickListener(this);
        btEnd.setOnClickListener(this);
        btEdit.setOnClickListener(this);
        btDel.setOnClickListener(this);
        btAdd.setOnClickListener(this);
        btAbandon.setOnClickListener(this);
        btquery.setOnClickListener(this);
        btcancel.setOnClickListener(this);
        btreport.setOnClickListener(this);

        //-------------------------------------
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Aqua));
        //-----------------
        mSpnName = (Spinner) this.findViewById(R.id.spnName);
        nowtime = (TextView) findViewById(R.id.now_time);
        nowtime.setTextSize(12);  //更新時間字型
        // -------------------------
        if (edittype == false) {   //只在非編輯狀態下執行
            //---------設定layout 顯示---------------
            u_layout_def();  //按鈕顯示初始值
            //--------取得目前時間
            startTime = System.currentTimeMillis();
            // 設定Delay的時間
            handler.postDelayed(updateTimer, 1000); // 延遲
            java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis()); //  獲取當前時間
            String str = formatter.format(curDate);
            nowtime.setText(getString(R.string.now_time) + str);
        }

        //-----抓取SQLite資料-------------
        sqliteupdate(edittype);
        // ----------------------------------------
        recSet = u_selectdb(myselection, myargs, myorder);
        showRec(index);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));

        //-------------------------
      /*  laySwipe = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);

        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);*/



      /*  tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        //-----------------
        mSpnName = (Spinner) this.findViewById(R.id.spnName);
        //---------------------

        // -------------------------
        // 取得目前時間
        startTime = System.currentTimeMillis();
        // 設定Delay的時間
        handler.postDelayed(updateTimer, 10000); // 延遲
        // ---------------------------------------
        sqliteupdate(edittype); // 抓取SQLite資料

// ----------------------------------------

        recSet = u_selectdb(myselection, myargs, myorder);
        u_setspinner();
        //---------------------------------
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
        showRec(index);
        // -------------------------
        mSpnName.setOnItemSelectedListener(this);*/
    }

    private void sqliteupdate(boolean edittype) { // 抓取SQLite資料
        mContRes = getContentResolver();
        Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);
        tcount = c.getCount();
        // ---------------------------
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
        // 使用自訂義spinner layout spinner_style.xml
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < tcount; i++) {
            c.moveToPosition(i);
            adapter.add(c.getString(0) + "," + c.getString(1) + "," + c.getString(2) + "," + c.getString(3));
        }
        c.close();
        //--設定spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpnName.setAdapter(adapter);
        mSpnName.setOnItemSelectedListener(this);
        u_spinmove(up_item);//spinner 小窗跳到第幾筆

        if (!edittype){
            //------ 宣告鈴聲 ---------------------------
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100); // 100=max
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 500);
            toneG.release();
        }

    }

    // 固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            /*handler.postDelayed(this, autotime * 1000); // 真正延遲的時間
            // -------執行匯入MySQL
            dbmysql();
            // -------*/

            old_index = mSpnName.getSelectedItemPosition(); // 找到按何項
            Long spentTime = System.currentTimeMillis() - startTime;
            // 計算目前已過分鐘數
            Long minius = (spentTime / 1000) / 60;
            // 計算目前已過秒數
            Long seconds = (spentTime / 1000) % 60;
            handler.postDelayed(this, autotime * 1000); // 真正延遲的時間
            java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis()); //  獲取當前時間
            String str = formatter.format(curDate);
            // -------執行匯入MySQL -------------
            dbmysql();
            //---------------------------------
            ++update_time;
            nowtime.setText(getString(R.string.now_time) + "(每" + autotime + "秒)" + str + " " + minius + ":" + seconds + " (" + update_time + "次)");
            u_spinmove(old_index);
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int iSelect = mSpnName.getSelectedItemPosition(); // 找到按何項
        // -----------------------------------
        String s = "資料：共" + tcount + " 筆," + "你按下第  " + Integer.toString(iSelect + 1) + "項"; // 起始為0
        // ----------------------------------------
        tvTitle.setText(s);
        b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
        //--------判斷SQLite 是否有資料----------
        if (recSet.size() == 0) {
            Toast.makeText(getApplicationContext(), "初次安裝", Toast.LENGTH_LONG).show();
            recSet = u_selectdb(myselection, myargs, myorder);
        }
        //-----------------------------------
        String[] fld = recSet.get(position).split("#");
        b_id.setText(fld[0]);
        b_name.setText(fld[1]);
        b_grp.setText(fld[2]);
        b_address.setText(fld[3]);
    }

    private ListView.OnItemClickListener listviewOnItemClkLis = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = "你按下第 " + Integer.toString(position + 1) + "筆"
                    + ((TextView) view.findViewById(R.id.txtView))
                    .getText()
                    .toString();
            bsubTitle.setText(s);
        }
    };



    private void u_spinmove(int i) {  //spinner jump
        if (i < 0) index = 0;
        try {
            if (i > recSet.size()) index = recSet.size();
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
        mSpnName.setSelection(i, true); //spinner 小窗跳到第幾筆
        index = i;

    }

    // --------------------------------------------------
    private String NetwordDetect() { //取得目前手機使用的IP
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String IPaddress = Finduserip.NetwordDetect(CM, wm);
        return IPaddress;
    }


    /*private void sqliteupdate(edittype) {//重新產生下拉功能表
        mContRes = getContentResolver();
        Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);
        c.moveToFirst(); // 一定要寫，不然會出錯
        tcount = c.getCount();
        // ---------------------------
        tvTitle.setTextColor(Color.BLUE);
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        b_id.setTextColor(ContextCompat.getColor(this,R.color.Red));
        // -------------------------
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        for (int i = 0; i < tcount; i++) {
            c.moveToPosition(i);
            adapter.add(c.getString(0) + "-" + c.getString(1) + "," + c.getString(2) + "," + c.getString(3));
        }
        c.close();

        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpnName.setAdapter(adapter);
        mSpnName.setOnItemSelectedListener(this);
        mSpnName.setSelection(up_item, true); //spinner 小窗跳到第幾筆

        //------ 宣告鈴聲 ---------------------------
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100); // 100=max
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 1000);
        toneG.release();
        //-----------------------------------------


    }*/

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            laySwipe.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    laySwipe.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refresh done!", Toast.LENGTH_SHORT).show();
                }
            }, 9000);
            btreport.callOnClick();
            laySwipe.setRefreshing(false);
        }
    };

    private void u_layout_def() {   //設定起始畫面
        btAdd.setVisibility(View.INVISIBLE);
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);

        brelative01.setVisibility(View.VISIBLE);
        blinear02.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.INVISIBLE);

        b_id.setEnabled(false);
        b_Relbutton.setVisibility(View.INVISIBLE);
        b_editon.setVisibility(View.INVISIBLE);


       /* btNext.setVisibility(View.VISIBLE);
        btPrev.setVisibility(View.VISIBLE);
        btTop.setVisibility(View.VISIBLE);
        btEnd.setVisibility(View.VISIBLE);*/

    }

    private Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,
                                   long id) {
            int iSelect = mSpnName.getSelectedItemPosition(); //找到按何項
            String[] fld = recSet.get(iSelect).split("#");
            String s = "資料：共" + recSet.size() + " 筆," + "你按下  " + String.valueOf(iSelect + 1) + "項";
            //起始為0
            tvTitle.setText(s);
            b_id.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.Red));
            b_id.setText(fld[0]);
            b_name.setText(fld[1]);
            b_grp.setText(fld[2]);
            b_address.setText(fld[3]);
            //-------目前所選的item---
            up_item = iSelect;
            //-------------------------------
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

            b_id.setText("");
            b_name.setText("");
            b_grp.setText("");
            b_address.setText("");
        }
    };





    @Override
    public void onClick(View v) {
        int rowsAffected;
        Uri uri;
        String whereClause;
        String[] selectionArgs;
        //---------------------------
        switch (v.getId()) {
            case R.id.btIdNext:
                ctlNext();
                break;
            case R.id.btIdPrev:
                ctlPrev();
                break;
            case R.id.btIdtop:
                ctlFirst();
                break;
            case R.id.btIdend:
                ctlLast();
                break;
            //------------------------------------
            case R.id.btnupdate:
                // 資料更新
                old_index = mSpnName.getSelectedItemPosition();
                uri = FriendsContentProvider.CONTENT_URI;
                ContentValues contVal = new ContentValues();
                contVal = FillRec();
                s_id = b_id.getText().toString().trim();

                whereClause = "id='" + s_id + "'";
                selectionArgs = null;
                rowsAffected = mContRes.update(uri, contVal, whereClause, selectionArgs);

                if (rowsAffected == -1) {
                    msg = "資料表已空, 無法修改 !";
                } else if (rowsAffected == 0) {
                    msg = "找不到欲修改的記錄, 無法修改 !";
                } else {
                    msg = "第 " + (old_index + 1) + " 筆記錄  已修改 ! \n" + "共 " + rowsAffected + " 筆記錄   被修改 !";
                    //-------------------------------------
                    mysql_update(); // 執行MySQL更新
                    //-------------------------------------
                    setupViewComponent();
                    u_spinmove(old_index);//spinner 小窗跳到第幾筆
                }
                Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();

                break;
            //------------------------------------
            case R.id.btIdDel:
                // 刪除資料
                old_index = mSpnName.getSelectedItemPosition();
                uri = FriendsContentProvider.CONTENT_URI;
                s_id = b_id.getText().toString().trim();
                whereClause = "id='" + s_id + "'";
                selectionArgs = null;
                rowsAffected = mContRes.delete(uri, whereClause, selectionArgs);

                if (rowsAffected == -1) {
                    msg = "資料表已空, 無法刪除 !";
                } else if (rowsAffected == 0) {
                    msg = "找不到欲刪除的記錄, 無法刪除 !";
                } else {
                    // ---------------------------
                    //mysql_del();// 執行MySQL刪除
                    // ---------------------------
                    msg = "第 " + (old_index + 1) + " 筆記錄  已刪除 ! \n" + "共 " + rowsAffected + " 筆記錄   被刪除 !";
                    // ---------------------------
                    mysql_del();// 執行MySQL刪除
                    // ---------------------------
                    setupViewComponent();
                    u_spinmove(old_index - 1);//--- spinner 跳到被刪除的前一筆,若無資料時填0 -----
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                break;
            //-----------------------
            case R.id.btnAdd: //按下新增鈕
                Cursor c_add = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);
                // 查詢name跟在b_name上打得是否有有此筆資料
                tname = b_name.getText().toString().trim();
                tgrp = b_grp.getText().toString().trim();
                taddress = b_address.getText().toString().trim();

                if (tname.equals("") || tgrp.equals("")) {
                    Toast.makeText(getApplicationContext(), "資料空白無法新增 !", Toast.LENGTH_SHORT).show();
                    return;
                }

                String msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                newRow.put("name", tname);
                newRow.put("grp", tgrp);
                newRow.put("address", taddress);
                mContRes.insert(FriendsContentProvider.CONTENT_URI, newRow);
                //-------直接增加到MySQL-------------------------------
                mysql_insert();
                dbmysql();  //回抓MySQL 資料到 SQLite
                setupViewComponent();
                int endno = c_add.getCount();
                u_spinmove(endno);//spinner 小窗跳到第幾筆
                //----------------------------------------
                msg = "新增記錄  成功 ! \n" + "目前資料表共有 " + (c_add.getCount() + 1) + " 筆記錄 !";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                count_t.setText("共計:" + Integer.toString(c_add.getCount() + 1) + "筆");

                if (c_add == null) {
                    tcount = 0;
                    index = 0;
                    up_item = 0;
                    return;
                };

                break;

            //------------------------------------
            case R.id.btnabandon: //按下放棄鈕
                Toast.makeText(Main.this, "*返回編輯處理*", Toast.LENGTH_SHORT).show();
                u_menu_edit_main();
                u_spinmove(index);//spinner 小窗跳到第幾筆
                break;

            //------------------------------------
            case R.id.btnquery: //按下查詢鈕
                recSet = u_query();
                up_item = 0;
                index = 0;
                u_setspinner();
                showRec(index);

                break;
            //------------------------------------
            case R.id.btnlist: //按下列表鈕
                recSet = u_query();
                bsubTitle.setText("顯示資料： 共 " + recSet.size() + " 筆");
                //===========取SQLite 資料=============
                List<Map<String, Object>> mList;
                mList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < recSet.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    String[] fld = recSet.get(i).split("#");
                    item.put("imgView", R.drawable.userconfig);
                    item.put("txtView", "id:" + fld[0] + "\nname:" + fld[1] + "\ngroup:" + fld[2] + "\naddr:" + fld[3]);
                    mList.add(item);
                }
                //=========設定listview========
                brelative01.setVisibility(View.INVISIBLE);
                blinear02.setVisibility(View.VISIBLE);
                b_Relbutton.setVisibility(View.INVISIBLE);

                SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        mList,
                        R.layout.list_item,
                        new String[]{"imgView", "txtView"},
                        new int[]{R.id.imgView, R.id.txtView}
                );
                listView.setAdapter(adapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(listviewOnItemClkLis);

                break;
            //------------------------------------
            case R.id.btidcancel:
                brelative01.setVisibility(View.VISIBLE);
                blinear02.setVisibility(View.INVISIBLE);
                b_Relbutton.setVisibility(View.VISIBLE);
                old_index = mSpnName.getSelectedItemPosition();
                u_menu_main();
                edittype = false;
                dbmysql();
                setupViewComponent();
                u_spinmove(old_index);//spinner 小窗跳到第幾筆
                break;
        }
    }

    private void u_menu_main() {
        menu.setGroupVisible(R.id.m_group1, true);
        menu.setGroupVisible(R.id.m_group2, false);
        menu.setGroupVisible(R.id.m_group3, false);
        b_Relbutton.setVisibility(View.INVISIBLE);
        mSpnName.setVisibility(View.VISIBLE);
        u_button_ontouch();
    }

    private void u_menu_edit_main() {
        handler.removeCallbacks(updateTimer); //關閉自動匯入的thread

        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, true);
        menu.setGroupVisible(R.id.m_group3, false);
        b_Relbutton.setVisibility(View.VISIBLE);
        b_editon.setVisibility(View.VISIBLE);
        btAdd.setVisibility(View.INVISIBLE);
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);
        mSpnName.setVisibility(View.VISIBLE);
        u_button_ontouch();
        touch_flag = true;  //開啟ontuchevent
        index = mSpnName.getSelectedItemPosition(); // 找到按何項
        showRec(index);
    }

    private void u_button_ontouch() {//            編輯中
        btTop.setVisibility(View.VISIBLE);
        btNext.setVisibility(View.VISIBLE);
        btPrev.setVisibility(View.VISIBLE);
        btEnd.setVisibility(View.VISIBLE);
    }

    private void u_menu_return() {
        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, false);
        menu.setGroupVisible(R.id.m_group3, true);
    }




    private ArrayList<String> u_selectdb(String myselection, String[] myargs, String myorder) {
        ArrayList<String> recAry = new ArrayList<String>();
        mContRes = getContentResolver();
        Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);

        tcount = c.getCount();
        int columnCount = c.getColumnCount();
        while (c.moveToNext()) {
            String fldSet = "";
            for (int ii = 0; ii < columnCount; ii++)
                fldSet += c.getString(ii) + "#";
            recAry.add(fldSet);
        }
        c.close();
        return recAry;

    }


    private void u_setspinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);

        for (int i = 0; i < recSet.size(); i++) {
            String[] fld = recSet.get(i).split("#");
            adapter.add(fld[0] + " " + fld[1] + " " + fld[2] + " " + fld[3]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnName.setAdapter(adapter);
        u_spinmove(up_item);//spinner 小窗跳到第幾筆

    }


    private ArrayList<String> u_query() {
        ArrayList<String> recAry = new ArrayList<String>();
        myselection = "";
        myorder = "id ASC"; // 排序欄位
        myselection = " name LIKE ? AND grp LIKE ? AND address LIKE ? ";
        myargs = new String[]{"%" + b_name.getText().toString() + "%",
                "%" + b_grp.getText().toString() + "%",
                "%" + b_address.getText().toString() + "%"};
        mContRes = getContentResolver();
        Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, myselection, myargs, myorder);
        tcount = c.getCount();
        int columnCount = c.getColumnCount();
        while (c.moveToNext()) {
            String fldSet = "";
            for (int ii = 0; ii < columnCount; ii++)
                fldSet += c.getString(ii) + "#";
            recAry.add(fldSet);
        }
        c.close();
        return recAry;
    }

    private void showRec(int show_index) {
        String msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (show_index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.count_t) + recSet.size() + "筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
            tvTitle.setText(stHead);

            String[] fld = recSet.get(show_index).split("#");
            b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            b_id.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            b_id.setText(fld[0]);
            b_name.setText(fld[1]);
            b_grp.setText(fld[2]);
            b_address.setText(fld[3]);
            u_spinmove(show_index);//spinner 小窗跳到第幾筆
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
            tvTitle.setText(stHead);
            b_id.setText("");
            b_name.setText("");
            b_grp.setText("");
            b_address.setText("");
        }
        count_t.setText(msg);
    }

    //------------------------------------------------
    private void ctlFirst() {
        // 第一筆
        index = 0;
        showRec(index);
    }

    private void ctlPrev() {
        // 上一筆
        index--;
        if (index < 0)
            index = recSet.size() - 1;
        showRec(index);

    }

    private void ctlNext() {
        // 下一筆
        index++;
        if (index >= recSet.size())
            index = 0;
        showRec(index);
    }


    private void ctlLast() {
        // 最後一筆
        index = recSet.size() - 1;
        showRec(index);
    }

    //---------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touch_flag) {  //開關ontuchevent
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 按下
                    x1 = event.getX(); // 觸控按下的 X 軸位置
                    y1 = event.getY(); // 觸控按下的 Y 軸位置
                    break;
                case MotionEvent.ACTION_MOVE: // 拖曳
                    break;
                case MotionEvent.ACTION_UP: // 放開
                    x2 = event.getX(); // 觸控放開的 X 軸位置
                    y2 = event.getY(); // 觸控放開的 Y 軸位置
                    // 判斷左右的方法，因為屏幕的左上角是：0，0 點右下角是max,max
                    // 並且移動距離需大於 > range
                    float xbar = Math.abs(x2 - x1);
                    float ybar = Math.abs(y2 - y1);
                    double z = Math.sqrt(xbar * xbar + ybar * ybar);
                    int angle = Math.round((float) (Math.asin(ybar / z) / Math.PI * 180));// 角度
                    if (x1 != 0 && y1 != 0) {
                        if (x1 - x2 > range) ctlPrev();// 向左滑動
                        if (x2 - x1 > range) ctlNext();// 向右滑動
                        if (y2 - y1 > range && angle > ran) ctlLast();// 向下滑動
                        if (y1 - y2 > range && angle > ran) ctlFirst();// // 向上滑動
                    }
                    break;
            }
        } else {
//不執行 onTouch
        }
        return super.onTouchEvent(event);
    }


    private void u_insert() {
        btAdd.setVisibility(View.VISIBLE);
        btAbandon.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btNext.setVisibility(View.INVISIBLE);
        btPrev.setVisibility(View.INVISIBLE);
        btTop.setVisibility(View.INVISIBLE);
        btEnd.setVisibility(View.INVISIBLE);
        b_id.setEnabled(false);

        showip = NetwordDetect();
        b_address.setText(showip);

        b_id.setText("");
        b_name.setText("");
        b_grp.setText("");
        //b_address.setText("");
        b_name.setHint("請輸入");

    }

    private ContentValues FillRec() { //
        ContentValues contVal = new ContentValues();
        contVal.put("id", b_id.getText().toString());
        contVal.put("name", b_name.getText().toString());
        contVal.put("grp", b_grp.getText().toString());
        contVal.put("address", b_address.getText().toString());
        return contVal;
    }


    private String u_chinano(int input_i) {
        String c_number = "";
        String china_no[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        c_number = china_no[input_i % 10];

        return c_number;
    }

    private String u_chinayear(int input_i) {
        String c_number = "";
        String china_no[] = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        c_number = china_no[input_i % 10];

        return c_number;
    }


    // ---------------------------------------------
    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    Uri uri = FriendsContentProvider.CONTENT_URI;
                    mContRes.delete(uri, null, null); // 刪除所有資料
                    msg = "資料表已空 !";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    break;
                case BUTTON_NEGATIVE:
                    msg = "放棄刪除所有資料 !";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    break;
            }
            setupViewComponent();
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();// 不執行這行
        Toast.makeText(getApplication(), "禁用返回鍵", Toast.LENGTH_SHORT).show();
    }

    //-------------------------------------------------------
    private void mysql_insert() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("name", tname));
        nameValuePairs.add(new BasicNameValuePair("grp", tgrp));
        nameValuePairs.add(new BasicNameValuePair("address", taddress));

        try {
            Thread.sleep(500);//延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //-----------------------------------------------
        String result = DBConnector.executeInsert("SELECT * FROM member", nameValuePairs);
        //-----------------------------------------------



    }
//-------------------------------------------------

    private void mysql_update() {

        s_id = b_id.getText().toString();
        tname = b_name.getText().toString();
        tgrp = b_grp.getText().toString();
        taddress = b_address.getText().toString();

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", s_id ));
        nameValuePairs.add(new BasicNameValuePair("name", tname));
        nameValuePairs.add(new BasicNameValuePair("grp", tgrp));
        nameValuePairs.add(new BasicNameValuePair("address", taddress));
        String result = DBConnector.executeUpdate("SELECT * FROM member", nameValuePairs);

        //匯入MySQL
        dbmysql();
    }

    private void mysql_del() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", s_id));
        String result = DBConnector.executeDelet("DELETE From member ", nameValuePairs);

        //匯入MySQL
        dbmysql();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        u_menu_main();
//
        b_m_add = menu.findItem(R.id.m_add);
        b_m_query = menu.findItem(R.id.m_query);
        b_m_batch = menu.findItem(R.id.m_batch);
        b_m_list = menu.findItem(R.id.m_list);
        b_m_mysql = menu.findItem(R.id.m_mysql);
        b_m_edit_start = menu.findItem(R.id.m_edit_start);
        b_m_edit_stop = menu.findItem(R.id.m_edit_stop);
        b_m_return = menu.findItem(R.id.m_return);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent();
        switch (item.getItemId()) {
            case R.id.m_add://新增
                u_insert();
                btTop.setVisibility(View.INVISIBLE);
                btNext.setVisibility(View.INVISIBLE);
                btPrev.setVisibility(View.INVISIBLE);
                btEnd.setVisibility(View.INVISIBLE);
                mSpnName.setVisibility(View.VISIBLE);
                touch_flag = false;  //關閉ontuchevent
                u_menu_return();
                break;

            case R.id.m_query://查詢
                btAdd.setVisibility(View.INVISIBLE);
                btAbandon.setVisibility(View.VISIBLE);
                btEdit.setVisibility(View.INVISIBLE);
                btDel.setVisibility(View.INVISIBLE);
                btquery.setVisibility(View.VISIBLE);
                b_id.setEnabled(false);
                b_id.setText("");
                b_name.setText("");
                b_grp.setText("");
                b_address.setText("");
                b_name.setHint("請輸入");
                u_menu_return();
                break;

            case R.id.m_batch://批次新增
                int maxrec = 5;
                Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI,
                        MYCOLUMN, null, null, null);
                String msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                for (int i = 0; i < maxrec; i++) {
                    // ------新增至MySQL--------------
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("name", "路人" + u_chinayear(i)));
                    nameValuePairs.add(new BasicNameValuePair("grp", "第" + u_chinano((int) (Math.random() * 4 + 1)) + "組"));
                    nameValuePairs.add(new BasicNameValuePair("address", "台中市西區工業一路" + (100 + i) + "號"));
                    String result = DBConnector.executeInsert("SELECT * FROM member", nameValuePairs);
                    // --------------------
                }
                tcount = c.getCount();
                // ---------------------------
                tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
                tvTitle.setText("顯示資料： 共" + tcount + " 筆");
                msg = "新增記錄  成功 ! ";
                Toast.makeText(Main.this, msg, Toast.LENGTH_SHORT).show();
                c.close();
                setupViewComponent();// 重構
                break;

            case R.id.m_clear://清空資料
                // 清空
                MyAlertDialog aldDial = new MyAlertDialog(Main.this);
                aldDial.getWindow().setBackgroundDrawableResource(R.color.Yellow);
                aldDial.setTitle("清空所有資料");

                aldDial.setMessage("資料刪除無法復原\n確定將所有資料刪除嗎?");
                aldDial.setIcon(android.R.drawable.ic_delete);
                aldDial.setCancelable(false); //返回鍵關閉
                aldDial.setButton(BUTTON_POSITIVE, "確定清空", aldBtListener);
                aldDial.setButton(BUTTON_NEGATIVE, "取消清空", aldBtListener);
                aldDial.show();
                break;

            case R.id.m_list://列表
                btAdd.setVisibility(View.INVISIBLE);
                btAbandon.setVisibility(View.VISIBLE);
                btEdit.setVisibility(View.INVISIBLE);
                btDel.setVisibility(View.INVISIBLE);
                btquery.setVisibility(View.INVISIBLE);
                btreport.setVisibility(View.VISIBLE);

                brelative01.setVisibility(View.VISIBLE);
                blinear02.setVisibility(View.INVISIBLE);
                b_id.setEnabled(false);
                b_id.setText("");
                b_name.setText("");
                b_grp.setText("");
                b_address.setText("");
                b_name.setHint(getString(R.string.hint));
                u_menu_return();
                break;

            case R.id.m_mysql: // 匯入MySQL
                dbmysql();
                break;

            case R.id.m_edit_start:  //啟用編輯
                u_menu_edit_main();
                edittype = true;
                break;

            case R.id.m_edit_stop: //關閉編輯
                old_index = mSpnName.getSelectedItemPosition();
                u_menu_main();
                edittype = false;
                dbmysql();
                setupViewComponent();
                u_spinmove(old_index);//spinner 小窗跳到第幾筆

                break;

            case R.id.m_return:
                btAbandon.performClick(); //觸發放棄按鈕
                break;

            case R.id.action_settings:
                this.finish();
                // finish()：結束當前 Activity，不會立即釋放內存。遵循 android 內存管理機制。
                // exit()：結束當前組件如 Activity，並立即釋放當前 Activity 所占資源。
                // killProcess()：結束當前組件如 Activity，並立即釋放當前Activity  所占資源。
                // restartPackage()：結束整個 App，包括 service 等其它 Activity 組件。
//                btAbandon.performClick();
                break;

        }

        return super.onOptionsItemSelected(item);

    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimer);
    }

    private void dbmysql() {


        //跟SQLite有關
        mContRes = getContentResolver();
        Cursor cur = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);
        cur.moveToFirst(); // 一定要寫，不然會出錯

        try{
            String result = DBConnector.executeQuery("SELECT * FROM member");//跟MySQLi有關
            Log.d(TAG,result);
            // 選擇讀取特定欄位
            // String result = DBConnector.executeQuery("SELECT id,name FROM
            // member");
            /*******************************************************************************************
             * SQL 結果有多筆資料時使用JSONArray 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             *******************************************************************************************/

            String r = result.toString().trim();
            //以下程式碼一定要放在前端藍色程式碼執行之後，才能取得狀態碼
            //存取類別成員 DBConnector.httpstate 判定是否回應 200(連線要求成功)
            Log.d(TAG, "httpstate="+DBConnector.httpstate );
            if (DBConnector.httpstate == 200) {
                Uri uri = FriendsContentProvider.CONTENT_URI;
                mContRes.delete(uri, null, null);
                Toast.makeText(getBaseContext(), "已經完成由伺服器會入資料",
                        Toast.LENGTH_LONG).show();
            } else {
                int checkcode=DBConnector.httpstate/100;
                switch(checkcode){
                    case 1:
                    msg="資訊回應(code:"+DBConnector.httpstate+")";
                    break;
                    case 2:
                    msg="已經完成由伺服器會入資料(code:"+DBConnector.httpstate+")";
                    break;
                    case 3:
                    msg="伺服器重定向訊息，請稍後在試(code:"+DBConnector.httpstate+")";
                    break;
                    case 4:
                    msg="用戶端錯誤回應，請稍後在試(code:"+DBConnector.httpstate+")";
                    break;
                    case 5:
                    msg="伺服器errorresponses，請稍後在試(code:"+DBConnector.httpstate+")";
                    break;
                }
                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
            }


            JSONArray jsonArray = new JSONArray(result);

            if (jsonArray.length()>0){//確認MySQL有無資料

                Uri uri = FriendsContentProvider.CONTENT_URI;
                mContRes.delete(uri, null, null); // 刪除SQLite所有資料

                for (int i=0;  i<jsonArray.length(); i++){
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    //
                    ContentValues newRow = new ContentValues();
                    // --(1) 自動取的欄位
                    // --取出 jsonObject
                    // 每個欄位("key","value")-----------------------
                    Iterator itt = jsonData.keys();
                    while (itt.hasNext()) {
                        String key = itt.next().toString();
                        String value = jsonData.getString(key); // 取出欄位的值
                        if (value == null) {
                            continue;
                        } else if ("".equals(value.trim())) {
                            continue;
                        } else {
                            jsonData.put(key, value.trim());
                        }
                        // ------------------------------------------------------------------
                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
                        // -------------------------------------------------------------------
                        Log.d(TAG, "第" + i + "個欄位 key:" + key + " value:" + value);

                    }

                    // ---(2) 使用固定已知欄位---------------------------
                    // newRow.put("id", jsonData.getString("id").toString());
                    // newRow.put("name",
                    // jsonData.getString("name").toString());
                    // newRow.put("grp", jsonData.getString("grp").toString());
                    // newRow.put("address", jsonData.getString("address")
                    // .toString());

                    //-------------------加入SQLite---------------------------------------
                    mContRes.insert(FriendsContentProvider.CONTENT_URI, newRow);
                    tvTitle.setTextColor(Color.BLUE);
                    tvTitle.setText("顯示資料： 共加入" + Integer.toString(jsonArray.length()) + " 筆");

                 }

            }else {
                Toast.makeText(Main.this, "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
        cur.close();

        sqliteupdate(edittype);

//       setupViewComponent();//重構

    }

    private void u_submenu() {
        flag = false;  //關閉ontuchevent
        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, true);
//        mSpnName.setVisibility(View.GONE);
        mSpnName.setEnabled(false);
    }





    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
