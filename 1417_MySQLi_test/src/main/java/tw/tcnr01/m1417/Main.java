package tw.tcnr01.m1417;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.tcnr01.m1417.providers.FriendsContentProvider;


public class Main extends AppCompatActivity implements View.OnClickListener {
    String TAG = "oldpa=";
    private TextView count_t;
    private EditText e001, e002, e003, e004;

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
    private EditText b_edid;
    String tname, tgrp, taddr;
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
    private TextView bsubTitle;

    private MenuItem mGroup1, mGroup2;
    private Menu menu;
    private boolean flag; //關閉ontuchevent
    //-----------------------------------------
    private SwipeRefreshLayout laySwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupViewComponent();
    }


    private void setupViewComponent() {
        flag = true; ////開啟ontuchevent
        tvTitle = (TextView) findViewById(R.id.tvIdTitle);
        e001 = (EditText) findViewById(R.id.edtName);
        e002 = (EditText) findViewById(R.id.edtGrp);
        e003 = (EditText) findViewById(R.id.edtAddr);
        count_t = (TextView) findViewById(R.id.count_t);

        btNext = (Button) findViewById(R.id.btIdNext);
        btPrev = (Button) findViewById(R.id.btIdPrev);
        btTop = (Button) findViewById(R.id.btIdtop);
        btEnd = (Button) findViewById(R.id.btIdend);

        btEdit = (Button) findViewById(R.id.btnupdate);
        btDel = (Button) findViewById(R.id.btIdDel);
        b_edid = (EditText) findViewById(R.id.edid);
        b_edid.setKeyListener(null);  //設定ID 不能修改
        //-----------------------
        btAdd = (Button) findViewById(R.id.btnAdd);
        btAbandon = (Button) findViewById(R.id.btnabandon);
        btquery = (Button) findViewById(R.id.btnquery);
        btcancel = (Button) findViewById(R.id.btidcancel);
        btreport = (Button) findViewById(R.id.btnlist);

        brelative01 = (RelativeLayout) findViewById(R.id.relative01);
        blinear02 = (LinearLayout) findViewById(R.id.linear02);

        listView = (ListView) findViewById(R.id.listView);
        bsubTitle = (TextView) findViewById(R.id.subTitle);
        //-------------------------
        laySwipe = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);

        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        //---------設定layout 顯示---------------
        u_layout_def();
        //-----------------------

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

        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        //-----------------
        mSpnName = (Spinner) this.findViewById(R.id.spnName);
        //---------------------
        recSet = u_selectdb(myselection, myargs, myorder);
        u_setspinner();
        //---------------------------------
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        b_edid.setTextColor(ContextCompat.getColor(this, R.color.Red));
        showRec(index);
        // -------------------------
        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);
    }
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

        b_edid.setEnabled(false);

        btNext.setVisibility(View.VISIBLE);
        btPrev.setVisibility(View.VISIBLE);
        btTop.setVisibility(View.VISIBLE);
        btEnd.setVisibility(View.VISIBLE);

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
            b_edid.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.Red));
            b_edid.setText(fld[0]);
            e001.setText(fld[1]);
            e002.setText(fld[2]);
            e003.setText(fld[3]);
            //-------目前所選的item---
            up_item = iSelect;
            //-------------------------------
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

            b_edid.setText("");
            e001.setText("");
            e002.setText("");
            e003.setText("");
        }
    };

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

    @Override
    public void onClick(View v) {
        int rowsAffected;
        Uri uri;
        String s_id;
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
                uri = FriendsContentProvider.CONTENT_URI;
                ContentValues contVal = new ContentValues();
                contVal = FillRec();
                s_id = b_edid.getText().toString().trim();
                whereClause = "id='" + s_id + "'";
                selectionArgs = null;
                rowsAffected = mContRes.update(uri, contVal, whereClause, selectionArgs);
                if (rowsAffected == -1) {
                    msg = "資料表已空, 無法修改 !";
                } else if (rowsAffected == 0) {
                    msg = "找不到欲修改的記錄, 無法修改 !";
                } else {
                    msg = "第 " + (index + 1) + " 筆記錄  已修改 ! \n" + "共 " + rowsAffected + " 筆記錄   被修改 !";
                    setupViewComponent();
                    showRec(index);
                }
                Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
                break;
            //------------------------------------
            case R.id.btIdDel:
                // 刪除資料
                mContRes = getContentResolver();
                uri = FriendsContentProvider.CONTENT_URI;
                s_id = b_edid.getText().toString().trim();
                whereClause = "id='" + s_id + "'";
                selectionArgs = null;
                rowsAffected = mContRes.delete(uri, whereClause, selectionArgs);
                if (rowsAffected == -1) {
                    msg = "資料表已空, 無法刪除 !";
                } else if (rowsAffected == 0) {
                    msg = "找不到欲刪除的記錄, 無法刪除 !";
                } else {
                    msg = "第 " + (index + 1) + " 筆記錄  已刪除 ! \n" + "共 " + rowsAffected + " 筆記錄   被刪除 !";
//---------------------
                    if (index + 1 == tcount) {
                        index--;
                    }
//------------------
                    setupViewComponent();
                    showRec(index);
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                break;
            //-----------------------
            case R.id.btnAdd: //按下新增鈕
                mContRes = getContentResolver();
                Cursor c_add = mContRes.query(FriendsContentProvider.CONTENT_URI, MYCOLUMN, null, null, null);
                tname = e001.getText().toString().trim();
                tgrp = e002.getText().toString().trim();
                taddr = e003.getText().toString().trim();
                if (tname.equals("") || tgrp.equals("")) {
                    Toast.makeText(getApplicationContext(), "資料空白無法新增 !", Toast.LENGTH_SHORT).show();
                    return;
                }
                String msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                newRow.put("name", tname);
                newRow.put("grp", tgrp);
                newRow.put("address", taddr);
                mContRes.insert(FriendsContentProvider.CONTENT_URI, newRow);
                // -------------------------
                msg = "新增記錄  成功 ! \n" + "目前資料表共有 " + (c_add.getCount() + 1) + " 筆記錄 !";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                count_t.setText("共計:" + Integer.toString(c_add.getCount() + 1) + "筆");
                if (c_add == null) {
                    tcount = 0;
                    index = 0;
                    return;
                }
                ;
                c_add.close();
                setupViewComponent();
                break;
            //------------------------------------
            case R.id.btnabandon: //按下放棄鈕
                Toast.makeText(Main.this, "*放棄*", Toast.LENGTH_SHORT).show();
                setupViewComponent();
                break;

            //------------------------------------
            case R.id.btnquery: //按下查詢鈕
                recSet = u_query();
                u_setspinner();
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
                    item.put("txtView",
                            "id:" + fld[0]
                                    + "\nname:" + fld[1]
                                    + "\ngroup:" + fld[2]
                                    + "\naddr:" + fld[3]);
                    mList.add(item);
                }
                //=========設定listview========
                brelative01.setVisibility(View.INVISIBLE);
                blinear02.setVisibility(View.VISIBLE);

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
                menu.setGroupVisible(R.id.m_group1, true);
                menu.setGroupVisible(R.id.m_group2, false);
                mSpnName.setEnabled(true);
                break;
        }
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

        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        //        mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
    }


    private ArrayList<String> u_query() {
        ArrayList<String> recAry = new ArrayList<String>();
        myselection = "";
        myorder = "id ASC"; // 排序欄位
        mContRes = getContentResolver();
        myselection = " name LIKE ? AND grp LIKE ? AND address LIKE ? ";
        myargs = new String[]{
                "%" + e001.getText().toString().trim() + "%",
                "%" + e002.getText().toString().trim() + "%",
                "%" + e003.getText().toString().trim() + "%"};
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

    private void showRec(int index) {
        String msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.count_t) + recSet.size() + "筆";
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
            tvTitle.setText(stHead);

            String[] fld = recSet.get(index).split("#");
            b_edid.setTextColor(ContextCompat.getColor(this, R.color.Red));
            b_edid.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            b_edid.setText(fld[0]);
            e001.setText(fld[1]);
            e002.setText(fld[2]);
            e003.setText(fld[3]);
            mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            tvTitle.setText(stHead);
            b_edid.setText("");
            e001.setText("");
            e002.setText("");
            e003.setText("");
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
        if (flag) {  //開關ontuchevent
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
                        if (x1 - x2 > range) { // 向左滑動
                            ctlPrev();
                        }
                        if (x2 - x1 > range) { // 向右滑動
                            ctlNext();
                            // t001.setText("向右滑動\n" + "滑動參值x1=" + x1 + " x2=" + x2 + "
                            // r=" + (x2 - x1)+"\n"+"ang="+angle);
                        }
                        if (y2 - y1 > range && angle > ran) { // 向下滑動
                            // 往下角度需大於50
                            // 最後一筆
                            ctlLast();
                        }
                        if (y1 - y2 > range && angle > ran) { // 向上滑動
                            // 往上角度需大於50
                            ctlFirst();// 第一筆
                        }
                    }
                    break;
            }
        } else {

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


        b_edid.setEnabled(false);
        b_edid.setText("");
        e001.setText("");
        e002.setText("");
        e003.setText("");
        e001.setHint("請輸入");

    }

    private ContentValues FillRec() { //
        ContentValues contVal = new ContentValues();
        contVal.put("id", b_edid.getText().toString());
        contVal.put("name", e001.getText().toString());
        contVal.put("grp", e002.getText().toString());
        contVal.put("address", e003.getText().toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        menu.setGroupVisible(R.id.m_group1, true);
        menu.setGroupVisible(R.id.m_group2, false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent it = new Intent();
        switch (item.getItemId()) {
            case R.id.m_add://新增
                u_insert();
                u_submenu();
                break;

            case R.id.m_query://查詢
                btAdd.setVisibility(View.INVISIBLE);
                btAbandon.setVisibility(View.VISIBLE);
                btEdit.setVisibility(View.INVISIBLE);
                btDel.setVisibility(View.INVISIBLE);
                btquery.setVisibility(View.VISIBLE);

                b_edid.setEnabled(false);
                b_edid.setText("");
                e001.setText("");
                e002.setText("");
                e003.setText("");
                e001.setHint("請輸入");
                menu.setGroupVisible(R.id.m_group1, false);
                menu.setGroupVisible(R.id.m_group2, true);
                u_submenu();
                break;

            case R.id.m_batch://批次新增
                int maxrec = 20;
                Cursor c = mContRes.query(FriendsContentProvider.CONTENT_URI,
                        MYCOLUMN, null, null, null);
                String msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                for (int i = 0; i < maxrec; i++) {
                    newRow.put("name", "路人" + u_chinayear(i));
                    newRow.put("grp", "第" + u_chinano((int) (Math.random() * 4 + 1)) + "組");
                    newRow.put("address", "台中市西區工業一路" + (100 + i) + "號");
                    mContRes.insert(FriendsContentProvider.CONTENT_URI, newRow);
                }
                tcount = c.getCount();
                // ---------------------------
                tvTitle.setTextColor(Color.BLUE);
                tvTitle.setText("顯示資料： 共" + tcount + " 筆");
                msg = "新增記錄  成功 ! ";
                Toast.makeText(Main.this, msg, Toast.LENGTH_SHORT).show();
                c.close();
                setupViewComponent();//onCreate(null); // 重構
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
                b_edid.setEnabled(false);
                b_edid.setText("");
                e001.setText("");
                e002.setText("");
                e003.setText("");
                e001.setHint("請輸入,全空白顯示所有資料");
                menu.setGroupVisible(R.id.m_group1, false);
                menu.setGroupVisible(R.id.m_group2, true);
                u_submenu();
                break;

            case R.id.m_test:
                it.setClass(Main.this,M1417test.class);
                startActivity(it);
                break;

            case R.id.action_settings:
                this.finish();
                break;

            case R.id.m_return:
                btAbandon.performClick();
                menu.setGroupVisible(R.id.m_group1, true);
                menu.setGroupVisible(R.id.m_group2, false);
//                mSpnName.setVisibility(View.VISIBLE);
                mSpnName.setEnabled(true);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void u_submenu() {
        flag = false;  //關閉ontuchevent
        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, true);
//        mSpnName.setVisibility(View.GONE);
        mSpnName.setEnabled(false);
    }
}
