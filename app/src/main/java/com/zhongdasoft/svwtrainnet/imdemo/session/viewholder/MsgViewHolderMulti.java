package com.zhongdasoft.svwtrainnet.imdemo.session.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.session.activity.WebViewActivity;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.imdemo.session.extension.MultiAttachment;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/1/10 16:08
 * 修改人：tony
 * 修改时间：2017/1/10 16:08
 * 修改备注：
 */

public class MsgViewHolderMulti extends MsgViewHolderBase {
    private MultiAttachment attachment;
    private ImageView message_item_multi_image;
    private TextView message_item_multi_title;
    private TextView message_item_multi_desc;

    private RelativeLayout message_item_multi_container;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_multi;
    }

    @Override
    protected void inflateContentView() {
        message_item_multi_container = findViewById(R.id.message_item_multi_container);
        message_item_multi_image = findViewById(R.id.message_item_multi_image);
        message_item_multi_title = findViewById(R.id.message_item_multi_title);
        message_item_multi_desc = findViewById(R.id.message_item_multi_desc);

    }

    @Override
    protected void bindContentView() {
        message_item_multi_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        message_item_multi_container.setOnLongClickListener(longClickListener);
        attachment = (MultiAttachment) message.getAttachment();
        Glide.with(NimUIKit.getContext()).load(attachment.getImgUrl()).into(message_item_multi_image);
        message_item_multi_title.setText(attachment.getTitle());
        message_item_multi_desc.setText(attachment.getContent());
    }

    //    //若是要自己修改气泡背景
//// 当是发送出去的消息时，内容区域背景的drawable id
//    @Override
//    protected int rightBackground() {
//        return com.netease.nim.uikit.R.drawable.nim_message_item_right_selector;
//    }
    @Override
    protected void onItemClick() {
        Intent intent = new Intent(NimUIKit.getContext(), WebViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", attachment.getTitle());
        intent.putExtra("item", attachment.getWebUrl());
        NimUIKit.getContext().startActivity(intent);
    }
}
