package tw.tcnr01.m1423;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class M1423 extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static ContentResolver ContRes;
    private ArrayAdapter<String> adapter;
    private List<String> allValues = new ArrayList<String>();
    private List<String> allPhoneNums = new ArrayList<String>();
    String TAG = "tcnr01=>";
    private String phone_no;
    private ListView list001;
    private int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private List<String> allMusicNum = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1423);

        setupViewComponent();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    private void setupViewComponent() {
        //若版本高於android5.0則詢問權限
        list001 = (ListView) findViewById(R.id.list01);
        List<Map<String, Object>> mList;
        mList = new ArrayList<Map<String, Object>>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            //版本較低或已許可權限
            ContRes = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = null;
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;
            Cursor cur = ContRes.query(uri, projection, selection, selectionArgs, sortOrder);

            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                //取得歌名
                Map<String, Object> item = new HashMap<String, Object>();
                String title = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));//歌名
                String album = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//專輯名
                String artist = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));//作者
                String dispname = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));//音樂文件名
                String datapath = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//音樂文件的路徑

                allMusicNum.add(datapath);
                item.put("title", title);
                item.put("album", album);
                item.put("artist", artist);
                item.put("dispname", dispname);
                item.put("datapath", datapath);
                mList.add(item);
                cur.moveToNext();
            }
            cur.close();

            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    mList,
                    R.layout.list,
                    new String[]{"title", "album", "artist", "dispname", "datapath"},
                    new int[]{R.id.t001, R.id.t002, R.id.t003, R.id.t004, R.id.t005}
            );

            list001.setAdapter(adapter);
            list001.setOnItemClickListener(listClk);
        }
    }


    private ListView.OnItemClickListener listClk = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String musicNum = allMusicNum.get(position);
            String s = "你按下" + Integer.toString(position) + "筆=>" + musicNum;
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            Intent it = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + musicNum);
            it.setDataAndType(uri, "audio/mp3");
            startActivity(it);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {


        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// 已經取得權限grantResults[0] manifest 權限.....
                setupViewComponent();
                //onCreate(null);
            } else {
                Toast.makeText(this, "直到取得權限, 否則無法顯示資料",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
