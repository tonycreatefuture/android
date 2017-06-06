package com.zhongdasoft.svwtrainnet.module.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.ClipUtil;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.Screen;
import com.zhongdasoft.svwtrainnet.util.SoftVersion;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.lang.ref.WeakReference;

public class MoreAboutUsActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private View publish;
    private View website;
    private View wechat;
    private View sharedApp;
    private View phone;
    private View workTime;
    private View qr;
    private View qa;
    private LinearLayout ll_website;
    private LinearLayout ll_weChat;
    private LinearLayout ll_sharedApp;
    private LinearLayout ll_phone;
    private LinearLayout ll_qr;
    private LinearLayout ll_qa;

    private TextView itemTitle;
    private TextView itemDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);
        Screen screen = Scale.getScreen(wr);
        int height = (int) (screen.getPxHeight() * 0.15f);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.versionInfo);
        rl.getLayoutParams().height = height;
        ImageView im = (ImageView) findViewById(R.id.im_image);
        im.getLayoutParams().width = (int) (height * 0.7f);
        im.getLayoutParams().height = (int) (height * 0.7f);

        TextView tvVersion = (TextView) findViewById(R.id.tv_title);
        String versionCode = SoftVersion.getVersionCode(this);
        tvVersion.setText(getResources().getString(R.string.VersionInfo, versionCode));
        tvVersion.setTextSize(height * 0.05f >= 24 ? 24 : height * 0.05f);

        if ("0".equals(MyProperty.getValueByKey("PublishMode", "0"))) {
            rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    readyGo(TestWebserviceActivity.class);
                }
            });
        }

        init();

        ll_website.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipAndShow(getResources().getString(R.string.WebSiteContent));
            }
        });
        ll_weChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipAndShow(getResources().getString(R.string.WeChatContent));
            }
        });
        ll_sharedApp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyProperty.isPublished()) {
                    DialogUtil.getInstance().showSharedDialog(wr, getResources().getDrawable(R.mipmap.app_qrcode), getResources().getString(R.string.publishedDownloadUrl));
                } else {
                    DialogUtil.getInstance().showSharedDialog(wr, getResources().getDrawable(R.mipmap.app_qrcode_test), getResources().getString(R.string.testDownloadUrl));
                }
//                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                        + getResources().getResourcePackageName(R.mipmap.app_qrcode) + "/"
//                        + getResources().getResourceTypeName(R.mipmap.app_qrcode) + "/"
//                        + getResources().getResourceEntryName(R.mipmap.app_qrcode));
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                shareIntent.setType("image/*");
//                startActivity(Intent.createChooser(shareIntent, "分享APP"));
            }
        });
        ll_phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipAndShow(getResources().getString(R.string.ContactPhoneContent));
            }
        });
        ll_qr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipAndShow(getResources().getString(R.string.QRContent));
            }
        });
        final String accessToken = MySharedPreferences.getInstance().getAccessToken(this);
        ll_qa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("item", MyProperty.getCurrentValue(getResources().getString(R.string.QAUrl, accessToken)));
                bundle.putString("qa", getResources().getString(R.string.QA));
                readyGo(TvContentActivity.class, bundle);
            }
        });
    }

    private void init() {
        publish = findViewById(R.id.publish_layout);
        website = findViewById(R.id.website_layout);
        wechat = findViewById(R.id.wechat_layout);
        sharedApp = findViewById(R.id.sharedApp_layout);
        phone = findViewById(R.id.phone_layout);
        workTime = findViewById(R.id.workTime_layout);
        qr = findViewById(R.id.qr_layout);
        qa = findViewById(R.id.qa_layout);
        ll_website = (LinearLayout) website.findViewById(R.id.item_line);
        ll_weChat = (LinearLayout) wechat.findViewById(R.id.item_line);
        ll_sharedApp = (LinearLayout) sharedApp.findViewById(R.id.item_line);
        ll_phone = (LinearLayout) phone.findViewById(R.id.item_line);
        ll_qr = (LinearLayout) qr.findViewById(R.id.item_line);
        ll_qa = (LinearLayout) qa.findViewById(R.id.item_line);

        View[] views = {publish, website, wechat, sharedApp, phone, workTime, qr, qa};
        String[] titles = {getResources().getString(R.string.VersionDesc), getResources().getString(R.string.WebSite), getResources().getString(R.string.WeChat), getResources().getString(R.string.SharedApp), getResources().getString(R.string.ContactPhone), getResources().getString(R.string.WorkTime), getResources().getString(R.string.QR), getResources().getString(R.string.QA)};
        String[] details = {getResources().getString(R.string.VersionDescContent), getResources().getString(R.string.WebSiteContent), getResources().getString(R.string.WeChatContent), getResources().getString(R.string.QAContent), getResources().getString(R.string.ContactPhoneContent), getResources().getString(R.string.WorkTimeContent), getResources().getString(R.string.QRContent), getResources().getString(R.string.QAContent)};

        for (int i = 0; i < views.length; i++) {
            itemTitle = (TextView) views[i].findViewById(R.id.item_title);
            itemDetail = (TextView) views[i].findViewById(R.id.item_detail);
            itemTitle.setText(titles[i]);
            itemDetail.setText(details[i]);
            if (getResources().getString(R.string.QAContent).equals(details[i])) {
                itemTitle.setTextColor(getResources().getColor(R.color.app_blue));
                itemDetail.setTextColor(getResources().getColor(R.color.app_blue));
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_more_aboutus;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_aboutus;
    }

    private void setClipAndShow(String str) {
        if (str.contains(" ")) {
            String[] arr = str.split(" ");
            ClipUtil.CopyData(MoreAboutUsActivity.this, arr[1]);
        } else {
            ClipUtil.CopyData(MoreAboutUsActivity.this, str);
        }
        ToastUtil.show(MoreAboutUsActivity.this, getResources().getString(R.string.copyFinished));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
