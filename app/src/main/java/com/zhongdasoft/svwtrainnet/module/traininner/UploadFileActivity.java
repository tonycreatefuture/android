package com.zhongdasoft.svwtrainnet.module.traininner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadFileActivity extends BaseActivity {
    private ListView lv;
    private WeakReference<? extends BaseActivity> wr;
    private String[] fileExtends;
    private String rootPath;
    private int upload_other_request_code;
    private HashMap<String, Object> fileMap;
    private ArrayList<HashMap<String, Object>> listItem;
    private String uploadFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        TextView leftBtn = (TextView) findViewById(R.id.trainnet_button_left);
        leftBtn.setVisibility(View.INVISIBLE);

        upload_other_request_code = Integer.parseInt(getResources().getString(R.string.UPLOAD_OTHER_REQUEST_CODE));

        uploadFiles = getIntent().getStringExtra("uploadFiles");
        String fileExtend = getIntent().getStringExtra("item");
        if (!StringUtil.isNullOrEmpty(fileExtend)) {
            fileExtends = fileExtend.split(",");
        } else {
            fileExtends = new String[1];
            fileExtends[0] = "";
        }
        rootPath = Environment.getExternalStorageDirectory().getPath();//获取根目录
        if (StringUtil.isNullOrEmpty(rootPath)) {
            setReturn(null);
            return;
        } else {
            rootPath += getResources().getString(R.string.downloadPath);
        }
        listItem = new ArrayList<>();
        getFileDir(rootPath);

        BaseListViewAdapter mAdapter = new BaseListViewAdapter(this,
                listItem);
        lv = (ListView) findViewById(R.id.listview_uploadfile);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                long fileSize = Long.parseLong(listItem.get(arg2).get("fileSize").toString());
                if (fileSize > Long.parseLong(getResources().getString(R.string.uploadFileSize))) {
                    ToastUtil.show(UploadFileActivity.this, getResources().getString(R.string.errFileSize));
                } else if (!StringUtil.isNullOrEmpty(uploadFiles) && uploadFiles.indexOf(listItem.get(arg2).get("fileName").toString() + ",") >= 0) {
                    ToastUtil.show(UploadFileActivity.this, getResources().getString(R.string.errFileExist));
                } else {
                    String filePath = listItem.get(arg2).get("filePath").toString();
                    setReturn(filePath);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_train_uploadfile;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_uploadfile;
    }

    private void getFileDir(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File fileTemp : files) {
            if (fileTemp.isFile() && fileTemp.canRead()) {
                String fileExtendName = getFileExtendName(fileTemp.getName());
                for (String extend : fileExtends) {
                    if (StringUtil.isNullOrEmpty(extend) || fileExtendName.toLowerCase().equals(extend)) {
                        fileMap = new HashMap<>();
                        fileMap.put("info", "文件名：" + fileTemp.getName());
                        fileMap.put("info1", "路径：" + fileTemp.getPath());
                        fileMap.put("info2", "文件大小：" + getShowSize(fileTemp.length()));
                        fileMap.put("fileName", fileTemp.getName());
                        fileMap.put("filePath", fileTemp.getPath());
                        fileMap.put("fileSize", fileTemp.length());
                        fileMap.put("noRight", "");
                        listItem.add(fileMap);
                        break;
                    }
                }
            } else {
            }
        }
        return;
    }

    private String getShowSize(long length) {
        if (length < 1024) {
            return length + "";
        } else if (length >= 1024 && length < 1024 * 1024) {
            return length / 1024 + "k";
        } else if (length >= 1024 * 1024 && length < 1024 * 1024 * 1024) {
            return length / 1024 / 1024 + "m";
        } else {
            return length / 1024 / 1024 / 1024 + "g";
        }
    }

    private String getFileExtendName(String name) {
        int pos = name.lastIndexOf(".");
        if (pos < 0) {
            return "";
        }
        return name.substring(pos + 1, name.length());
    }

    private void setReturn(String filePath) {
        Intent mIntent = new Intent();
        mIntent.putExtra("item", filePath);
        // 设置结果，并进行传送
        this.setResult(upload_other_request_code, mIntent);
        finish();
    }
}
