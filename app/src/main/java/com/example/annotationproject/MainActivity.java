package com.example.annotationproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.annotationlib.LActivity;
import com.example.annotationlib.LView;

@LActivity
public class MainActivity extends AppCompatActivity {

    @LView(R.id.mAnnotationBtn)
    Button mAnnotationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DIMainActivity.bindView(this);
        mAnnotationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "测试注解", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
