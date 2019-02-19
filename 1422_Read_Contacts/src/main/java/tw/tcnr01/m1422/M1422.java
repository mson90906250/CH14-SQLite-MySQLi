package tw.tcnr01.m1422;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
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

public class M1422 extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static ContentResolver ContRes;
    private ArrayAdapter<String> adapter;
    private List<String> allValues = new ArrayList<String>();
    private List<String> allPhoneNums = new ArrayList<String>();
    String TAG = "tcnr01=>";
    private String phone_no;
    private ListView list001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1422);

        setupViewComponent();
    }

    private void setupViewComponent() {
        list001 = (ListView) findViewById(R.id.list001);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
            // 等待onRequestPermissionsResult(int, String[], int[])覆寫方法來取得權限

        } else {
            // Android版本低於6.0版或已經取得權限
            // Uri contacts = Uri.parse("content://contacts/people");
            ContRes = getContentResolver();
            Uri uri = ContactsContract.Contacts.CONTENT_URI;

            String[] projection = null;
            String selection = null;
            String[] selectionArgs = null;


            List<Map<String, Object>> mList;
            mList = new ArrayList<Map<String, Object>>();
            // String sortOrder = null;
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor cur = ContRes.query(uri, projection, selection, selectionArgs, sortOrder);
            // --
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                Map<String, Object> item = new HashMap<String, Object>();
                //取得聯絡人的姓名
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // 取得聯系電話
                // 需要先取得出這個聯系人的id
                String contactsId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                // 由電話保存在其他的表中，因此這裏要進行關聯查詢
                Cursor c2 = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactsId},
                        null
                );

                String num = null;
                phone_no = "";
                c2.moveToFirst();

                try {
                    num = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    allPhoneNums.add(num);
                } catch (Exception e) {
                    allPhoneNums.add("000");
                    e.printStackTrace();
                }

                c2.moveToFirst();
                while (!c2.isAfterLast()) {
                    num = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phone_no += num + "\n";
                    c2.moveToNext();
                }
                c2.close();


                // 取得Email 資訊---------------
                Cursor emailCur = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{contactsId}, null);

                String email = "";

                emailCur.moveToFirst();
                while (!emailCur.isAfterLast()) {
                    int code = -1;
                    try {
                        code = Integer.valueOf(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    String codestr = Integer.toString(code);

                    switch (code) {
                        case -1://notfound
                            email += codestr + ":  \n";
                            break;
                        case 1://Work
                            email += codestr + ":" + emailCur.getString(emailCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Email.DATA)) + ";\n";
                            break;
                        default:
                            email += codestr + ":" + emailCur.getString(emailCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Email.DATA)) + ";\n";
                            break;
                    }
                    emailCur.moveToNext();
                }
                emailCur.close();
                // ------------------------------
                // 取得地址資訊-------------------
                String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] addrWhereParams = new String[]{contactsId,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                Cursor addrCur = getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);

                String address = "";

                addrCur.moveToFirst();
                while (!addrCur.isAfterLast()) {
                    String street = addrCur.getString(addrCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    String city = addrCur.getString(addrCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                    String state = addrCur.getString(addrCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                    String postalCode = addrCur.getString(addrCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                    String country = addrCur.getString(addrCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                    int code = -1;
                    try {
                        code = Integer.valueOf(addrCur.getString(addrCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredPostal.TYPE)));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    String codestr = Integer.toString(code);

                    switch (code) {
                        case -1:
                            address +="-1;\n";
                            break;
                        case 1:
                            //住家地址
                            address += codestr+"Home:" + street + city + state + country + postalCode + ";\n";
                            break;
                        case 2:
                            //辦公室地址
                            address += codestr+"Office" + street + city + state + country + postalCode + ";\n";
                            break;
                        default:
                            address +=codestr+ "Other" + street + city + state + country + postalCode + ";\n";
                            break;
                    }
                    addrCur.moveToNext();
                }

                addrCur.close();

                // --------------------------------


                item.put("contactsId", contactsId);
                item.put("name", name);
                item.put("phone_no", phone_no);
                item.put("email", email);
                item.put("address", address);
                mList.add(item);
                //----
                cur.moveToNext();

            }
            cur.close();

            //---設定ListView
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    mList,
                    R.layout.list,
                    new String[]{"contactsId", "name", "phone_no", "email", "address"},
                    new int[]{R.id.t001, R.id.t002, R.id.t003, R.id.t004, R.id.t005}
            );

            list001.setAdapter(adapter);
            list001.setOnItemClickListener(listviewOnItemClkLis);
        }
    }

    AdapterView.OnItemClickListener listviewOnItemClkLis = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 取得要撥的電話號碼
            String phoneNum = allPhoneNums.get(position);
            String s = "你按下 " + Integer.toString(position) + "筆=>" + phoneNum;
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT)
                    .show();
//            // 使用Intent來切換到打電話的界面上，並將要播的電話傳進去，
            Intent in = new Intent();
//            // 設置現在要切換的功能
            in.setAction(Intent.ACTION_DIAL);
            in.setData(Uri.parse("tel:" + phoneNum));
            startActivity(in);

            // //叫出撥號程式
            // Uri uri = Uri.parse("tel:0800000123");
            // Intent it = new Intent(Intent.ACTION_DIAL, uri);
            // startActivity(it);
            //
            // //直接打電話出去
            // Uri uri = Uri.parse("tel:0800000123");
            // Intent it = new Intent(Intent.ACTION_CALL, uri);
            // startActivity(it);
            //
            // //用這個，要在 AndroidManifest.xml 中，加上
//    <uses-permission android:name="android.permission.CALL_PHONE"></uses-permission>
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupViewComponent();
            } else {
                Toast.makeText(this, getString(R.string.noPermission), Toast.LENGTH_SHORT).show();
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
