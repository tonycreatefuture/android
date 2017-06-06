package com.netease.nim.uikit.session.viewholder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.RegExpValidatorUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.activity.WebViewActivity;
import com.netease.nim.uikit.session.emoji.MoonUtil;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {
    private TextView bodyTextView;
    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        layoutDirection();

        bodyTextView = findViewById(R.id.nim_message_item_text_body);
        bodyTextView.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
//        String text = bodyTextView.getText().toString().trim();
//        if (RegExpValidatorUtil.IsUrl(text.toLowerCase())) {
//            bodyTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//            bodyTextView.getPaint().setAntiAlias(true);//抗锯齿
//        }
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
//        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void layoutDirection() {
        bodyTextView = findViewById(R.id.nim_message_item_text_body);
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(R.drawable.nim_message_item_left_selector);
            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            bodyTextView.setBackgroundResource(R.drawable.nim_message_item_right_selector);
            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }

    @Override
    protected void onItemClick() {
        String text = bodyTextView.getText().toString().trim();
        if (RegExpValidatorUtil.IsUrl(text)) {
            Intent intent = new Intent(NimUIKit.getContext(), WebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", text);
            intent.putExtra("item", text);
            NimUIKit.getContext().startActivity(intent);
        }
    }
}
