package com.example.insert_message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         ArrayList<String> phoneNumbersList = new ArrayList<>();

        Button buttonAdd;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editTextNumbers = findViewById(R.id.editTextPhoneNumbers);

        buttonAdd = findViewById(R.id.loginButton);

//        // 设置 EditText 的监听器
//        editTextNumbers.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                // 检查操作 ID 或者事件是否为回车键
//                if (actionId == EditorInfo.IME_ACTION_DONE ||
//                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
//                    // 获取用户输入的电话号码
//                    String input = editTextNumbers.getText().toString().trim();
//                    if (!input.isEmpty()) {
//                        // 将电话号码添加到列表中
//                        phoneNumbersList.add(input);
//                        // 清空 EditText
//                        editTextNumbers.setText("");
//                        // 显示已添加的电话号码
//                        Toast.makeText(MainActivity.this, "Added: " + input, Toast.LENGTH_SHORT).show();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });



        buttonAdd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

//             获取用户输入的电话号码
            String[] input = editTextNumbers.getText().toString().split("\\s+");
            Collections.addAll(phoneNumbersList, input);

            for (String number : phoneNumbersList) {

                Long callRandomValue = getRandomTimestamp(5L,15L);

                String callTime = Long.toString(callRandomValue);
                Long doTime = getRandomTimestamp(getRandomTime(14),getRandomTime(18));

                String type = Integer.toString(getWeightedRandom());

                insertCallLog(
                        number,
                        callTime,
                        type,
                        "0",
                        doTime);
            }

            Toast.makeText(MainActivity.this, "插入成功!！", Toast.LENGTH_LONG).show();
        }

        });

    }

    /**
     * 插入一条通话记录
     * @param number 通话号码
     * @param duration 通话时长（响铃时长）以秒为单位 1分30秒则输入90
     * @param type  通话类型  1呼入 2呼出 3未接
     * @param isNew 是否已查看    0已看1未看
     */
    private void insertCallLog(String number, String duration, String type, String isNew,Long time ) {
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
        values.put(CallLog.Calls.DATE, time );
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.NEW, isNew);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG}, 1000);
        }
        getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }


    private Long getRandomTime(int hour1){
        // 获取当前时间的 Calendar 实例
        Calendar calendar = Calendar.getInstance();

        // 设置小时为，分钟、秒和毫秒为0
        calendar.set(Calendar.HOUR_OF_DAY, hour1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 获取当天13点的时间戳（毫秒数）
        return calendar.getTimeInMillis();
    }


    private  Long getRandomTimestamp(Long start, Long end) {
        // 创建一个随机数生成器
        Random random = new Random();

        // 生成 start 和 end 之间的随机 long 值
        return start + (long) (random.nextDouble() * (end - start));
    }

    private int getWeightedRandom() {
        // 创建一个随机数生成器
        Random random = new Random();

        // 生成一个 [0, 1) 之间的随机浮点数
        double randomValue = random.nextDouble();

        // 80% 取 1，20% 取 2
        if (randomValue < 0.8) {
            return 2;
        } else {
            return 3;
        }
    }
}