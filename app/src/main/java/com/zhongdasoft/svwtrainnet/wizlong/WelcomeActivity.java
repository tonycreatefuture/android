package com.zhongdasoft.svwtrainnet.wizlong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.zhongdasoft.svwtrainnet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {


    @Bind(R.id.btnSchedule)
    Button btnSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnSchedule.setText("快来学习");

    }
    @OnClick(R.id.btnVCT)
    public void gotoVct() {
        Intent intent = new Intent(this, LiveCourseDetailActivity.class);
        intent.putExtra(LiveCourseDetailActivity.INTENT_KEY_URL, "http://www.meilizhongguo.org/contents/61/415.html");
        startActivity(intent);
    }

    public void gotoStudy() {

    }
}
