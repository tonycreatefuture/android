package com.netease.nim.uikit.session.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.fragment.MessageFragment;

import java.util.List;

/**
 * Created by zhoujianghua on 2015/9/10.
 */
public abstract class BaseMessageActivity extends UI {

    protected String sessionId;

    private SessionCustomization customization;

    private MessageFragment messageFragment;

    protected abstract MessageFragment fragment();
    protected abstract int getContentViewId();
    protected abstract void initToolBar();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewId());
        initToolBar();
        parseIntent();

        messageFragment = (MessageFragment) switchContent(fragment());
    }

    @Override
    public void onBackPressed() {
        if (messageFragment == null || !messageFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (messageFragment != null) {
            messageFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (customization != null) {
            customization.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private void parseIntent() {
        sessionId = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        customization = (SessionCustomization) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);

        if (customization != null) {
            addRightCustomViewOnActionBar(this, customization.buttons);
        }
    }

    // 添加action bar的右侧按钮及响应事件
    private void addRightCustomViewOnActionBar(UI activity, List<SessionCustomization.OptionsButton> buttons) {
        if (buttons == null || buttons.size() == 0) {
            return;
        }

        Toolbar toolbar = getToolBar();
        if (toolbar == null) {
            return;
        }

        LinearLayout view = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.nim_action_bar_custom_view, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (final SessionCustomization.OptionsButton button : buttons) {
            ImageView imageView = new ImageView(activity);
            imageView.setImageResource(button.iconId);
            imageView.setBackgroundResource(R.drawable.nim_nim_action_bar_button_selector);
            imageView.setPadding(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.onClick(BaseMessageActivity.this, v, sessionId);
                }
            });
            view.addView(imageView, params);
        }

        toolbar.addView(view, new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT | Gravity.CENTER));
//        setTextSize();
    }

//    protected void setTextSize() {
//        final TextView title = (TextView) findViewById(R.id.toolbar_title);
////        title.setText("光大银联龙腾联名白金卡，新申请该卡客户自审批通过日起");
//        textSize = 22f;
//        ViewTreeObserver vto = title.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//                title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                title.getHeight();
//                double w0 = 0, w1 = 1;
//                while (w1 >= w0 && textSize > 15f) {
//                    w0 = title.getWidth();//控件宽度
//                    w1 = title.getPaint().measureText(title.getText().toString());//文本宽度
//                    //需要换行就显示该控件
//                    textSize = textSize - 1;
//                    title.setTextSize(textSize - 1);
//                }
//
//            }
//        });
//    }

}
