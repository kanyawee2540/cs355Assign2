package com.example.hw2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView head = (TextView)findViewById(R.id.No);
        TextView des = (TextView)findViewById(R.id.desc);
        String fortune_no = getIntent().getStringExtra("Fortune");
        String[] res = getResources().getStringArray(R.array.fortune);
        String result = res[Integer.parseInt(fortune_no)];
        String h = head.getText() + String.valueOf(Integer.parseInt(fortune_no)+1);
        head.setText(h);
        des.setText("\t\t\t\t\t"+result);
    }
}
