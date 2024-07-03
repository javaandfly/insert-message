package com.example.phone;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {


        /**
         * 插入一条通话记录
         * @param number 通话号码
         * @param duration 通话时长（响铃时长）以秒为单位 1分30秒则输入90
         * @param type  通话类型  1呼入 2呼出 3未接
         * @param isNew 是否已查看    0已看1未看
         */
        private void insertCallLog(String number, String duration, String type, String isNew) {
            //在通讯录查询是否存在该联系人，若存在则把名字信息也插入到通话记录中
            String name = "";
            String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
            //设置查询条件
            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='"+number+"'";
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    cols, selection, null, null);
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                name = cursor.getString(nameFieldColumnIndex);
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.CACHED_NAME, name);
            values.put(CallLog.Calls.NUMBER, number);
            values.put(CallLog.Calls.DATE, System.currentTimeMillis() );
            values.put(CallLog.Calls.DURATION, duration);
            values.put(CallLog.Calls.TYPE, type);
            values.put(CallLog.Calls.NEW, isNew);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALL_LOG}, 1000);
            }
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }
    }

