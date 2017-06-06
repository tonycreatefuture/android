package com.zhongdasoft.svwtrainnet.module.traininner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.CameraUtil;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.FileUtil;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.PermissionUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


public class InnerPlanSummaryActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private View include;
    private EditText innerPlanSummary;
    private TextView innerPlanSummaryDesc;
    private LinearLayout ll_upload;
    private TextView innerPlanSummaryOk;
    private TextView innerPlanSummaryCancel;
    private TextView innerPlanUpload;
    private HashMap<String, Object> resultMap;
    private String planId;
    private String planStatus;
    private Intent intent;
    private ArrayList<HashMap<String, Object>> summaryList;
    private ArrayList<HashMap<String, Object>> listItem;
    private String summary;
    private boolean isReadOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        TextView tvTitle = (TextView) findViewById(R.id.trainnet_title);

        int[] includes = {R.id.include_planSummary, R.id.include_traineePhoto};
        int[] texts = {R.string.PlanSummary, R.string.PlanUpload};
        for (int i = 0; i < includes.length; i++) {
            include = findViewById(includes[i]);
            TextView tv = (TextView) include.findViewById(R.id.innerPlanLineDesc);
            tv.setText(texts[i]);
        }

        summary = "";

        planId = getIntent().getStringExtra("item");
        innerPlanSummary = (EditText) findViewById(R.id.innerPlanSummary);
        innerPlanSummaryDesc = (TextView) findViewById(R.id.innerPlanSummaryDesc);
        innerPlanSummaryDesc.setText(getResources().getString(R.string.summaryWords, "0"));
        innerPlanSummary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                innerPlanSummaryDesc.setText(getResources().getString(R.string.summaryWords, s.length() + ""));
            }
        });
        ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        innerPlanUpload = (TextView) findViewById(R.id.innerPlanUpload);
        innerPlanSummaryOk = (TextView) findViewById(R.id.innerPlanSummaryOk);
        innerPlanSummaryCancel = (TextView) findViewById(R.id.innerPlanSummaryCancel);

        innerPlanSummaryOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerPlanSummary.length() < 10) {
                    ToastUtil.show(InnerPlanSummaryActivity.this, "请输入最少10个字符");
                    return;
                }
                runThread();
            }
        });

        innerPlanSummaryCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                innerPlanSummary.setText(null == innerPlanSummary.getTag() ? "" : innerPlanSummary.getTag().toString());
                innerPlanSummaryDesc.setText(getResources().getString(R.string.summaryWords, innerPlanSummary.getText().toString().length() + ""));
                setDefaultAttachment();
            }
        });
        planStatus = getIntent().getStringExtra("planStatus");
        if ("2".equals(planStatus) || "3".equals(planStatus)) {
            isReadOnly = true;
            tvTitle.setText(R.string.title_innerplan_viewsummary);
        }

        final String[] items = {"拍照", "手机相册"};//, "上传文件"};
        innerPlanUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showListDialog(wr, "请选择", items, new DialogUtil.MyDismiss() {
                    @Override
                    public String handleDismiss(int which, String str) {
                        switch (which) {
                            case 0:
                                CameraUtil.getInstance().openCamera(InnerPlanSummaryActivity.this);
                                break;
                            case 1:
                                Crop.pickImage(InnerPlanSummaryActivity.this);
//                                CameraUtil.openPhoneCamera(InnerPlanSummaryActivity.this);
                                break;
//                            case 2:
//                                openPhoneDir();
//                                break;
                        }
                        return null;
                    }
                });
            }
        });

        listItem = new ArrayList<>();
        downloadData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_innerplan_summary;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_innerplan_summary;
    }

    private void downloadData() {
        listItem.clear();
        Waiting.show(this, getResources().getString(R.string.LoadingSummary));
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String InnerPlanSummaryRefresh = getCache().getAsString(CacheKey.InnerPlanSummaryRefresh + planId);
//                if (!StringUtil.isNullOrEmpty(InnerPlanSummaryRefresh)) {
//                    listItem = getGson().fromJson(InnerPlanSummaryRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
//                    }.getType());
//                    if (listItem.size() > 0) {
//                        summary = listItem.get(0).get("summary").toString();
//                    }
//                } else {
                summaryList = TrainNetWebService.getInstance().InternalSummary(InnerPlanSummaryActivity.this, planId);
                HashMap<String, Object> hashMap;
                for (HashMap<String, Object> map : summaryList) {
                    if ("Result".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                        summary = (null == map.get("Summary") ? "" : map.get("Summary").toString());
                    }
                    if ("ApiInternalSummaryAnnex".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                        hashMap = new HashMap<>();
                        String id = map.get("Id").toString();
                        String filePath = MyProperty.getCurrentValue(getResources().getString(R.string.SummariesUrl, map.get("PhysicalName").toString()));
                        String showName = map.get("FileName").toString();
                        hashMap.put("id", id);
                        hashMap.put("filePath", filePath);
                        hashMap.put("showName", showName);
                        listItem.add(hashMap);
                    }
                }
                if (listItem.size() > 0) {
                    listItem.get(0).put("summary", summary);
                }
                final int downloadLen = listItem.size();
                if (downloadLen > 0) {
                    PermissionUtil.getInstance().applyPermission(InnerPlanSummaryActivity.this, new PermissionUtil.PermissionHandler() {
                        @Override
                        public void handlePermission(boolean granted) {
                            if (!granted) {
                                Waiting.dismiss();
                                ToastUtil.show(InnerPlanSummaryActivity.this, getResources().getString(R.string.sdCardTips));
                                return;
                            }
                            int i = 1;
                            for (HashMap<String, Object> map : listItem) {
                                final int k = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Waiting.setText(getResources().getString(R.string.LoadingFiles, String.format("(%s/%s)", k, downloadLen)));
                                    }
                                });
                                String newFilePath = FileUtil.getInstance().saveFile(map.get("filePath").toString(), map.get("showName").toString(), CameraUtil.getInstance().getCameraFilePath(), InnerPlanSummaryActivity.this);
                                map.put("filePath", newFilePath);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(InnerPlanSummaryActivity.this, getResources().getString(R.string.FilesFinished, String.format("(%s/%s)", k, downloadLen)));
                                    }
                                });
                                i++;
                            }
                        }
                    });

                }
//                    getCache().put(CacheKey.InnerPlanSummaryRefresh + planId, getGson().toJson(listItem));
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //小结赋值
                        innerPlanSummary.setText(summary);
                        innerPlanSummary.setTag(summary);
                        innerPlanSummaryDesc.setText(getResources().getString(R.string.summaryWords, summary.length() + ""));
                        setDefaultAttachment();
                        if (isReadOnly) {
                            innerPlanSummaryOk.setEnabled(false);
                            innerPlanSummaryCancel.setEnabled(false);
                            innerPlanSummary.setEnabled(false);
                            innerPlanUpload.setEnabled(false);
                            innerPlanSummaryOk.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_d));
                            innerPlanSummaryCancel.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_d));
                            innerPlanSummaryOk.setTextColor(getResources().getColor(R.color.dark_gray));
                            innerPlanSummaryCancel.setTextColor(getResources().getColor(R.color.dark_gray));
                            innerPlanUpload.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_d));
                            innerPlanSummary.setBackground(getResources().getDrawable(R.color.gray_white));
                            FrameLayout fl;
                            ImageView deleteMark;
                            for (int i = 0; i < ll_upload.getChildCount(); i++) {
                                fl = (FrameLayout) ll_upload.getChildAt(i);
                                deleteMark = (ImageView) fl.getChildAt(1);
                                deleteMark.setVisibility(View.GONE);
                            }
                        }
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    private void setDefaultAttachment() {
        ll_upload.removeAllViews();
        for (HashMap<String, Object> map : listItem) {
            CameraUtil.getInstance().saveImageFile(map.get("filePath").toString(), map.get("showName").toString(), ll_upload, wr);
        }
    }

    private void runThread() {
        Waiting.show(this, getResources().getString(R.string.LoadingSummaryUpload));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> addList = getAddList();
                ArrayList<String> delList = getDelList();
                if ((null == innerPlanSummary.getTag() && !StringUtil.isNullOrEmpty(innerPlanSummary.getText().toString()))
                        || !innerPlanSummary.getTag().toString().equals(innerPlanSummary.getText().toString())) {
                    resultMap = TrainNetWebService.getInstance().InternalUpdateSummary(InnerPlanSummaryActivity.this, planId, innerPlanSummary.getText().toString());
                } else {
                    if (addList.size() == 0 && delList.size() == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(InnerPlanSummaryActivity.this, getResources().getString(R.string.noChanged));
                                Waiting.dismiss();
                            }
                        });
                        return;
                    }
                }
                int i = 1;
                final int addLen = addList.size();
                for (String add : addList) {
                    final int k = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Waiting.setText(getResources().getString(R.string.LoadingCompressFiles, String.format("(%s/%s)", k, addLen)));
                        }
                    });
                    String[] myAdd = add.split(",");
                    final String fileName = myAdd[0];
                    String fileType = myAdd[2];
                    byte[] content;
                    if ("0".equals(fileType)) {
                        content = FileUtil.getInstance().getContentFromFile(myAdd[1]);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeFile(myAdd[1]);
                        content = CameraUtil.getInstance().getCompressBitmap(InnerPlanSummaryActivity.this, bitmap);
                    }
                    String contentStr = Base64.encodeToString(content, Base64.DEFAULT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Waiting.setText(getResources().getString(R.string.LoadingUploadFiles, String.format("(%s/%s)", k, addLen)));
                        }
                    });
                    resultMap = TrainNetWebService.getInstance().InternalUploadFile(InnerPlanSummaryActivity.this, planId, fileName, contentStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(InnerPlanSummaryActivity.this, getResources().getString(R.string.UploadFilesFinished, String.format("(%s/%s)", k, addLen)));
                        }
                    });
                    i++;
                }
                i = 1;
                final int delLen = delList.size();
                for (String del : delList) {
                    final int k = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Waiting.setText(getResources().getString(R.string.LoadingDeleteFiles, String.format("(%s/%s)", k, delLen)));
                        }
                    });
                    String[] myDel = del.split(",");
                    int delId = Integer.parseInt(myDel[0]);
                    final String fileName = myDel[1];
                    resultMap = TrainNetWebService.getInstance().InternalDeleteFile(InnerPlanSummaryActivity.this, delId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(InnerPlanSummaryActivity.this, getResources().getString(R.string.DeleteFilesFinished, String.format("(%s/%s)", k, delLen)));
                        }
                    });
                    i++;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        String returnCode = resultMap.get(getResources().getString(R.string.ReturnCode)).toString();
                        if ("0".equals(returnCode)) {
//                            getCache().remove(CacheKey.InnerPlanSummaryRefresh + planId);
                        }
                        String message = resultMap.get(getResources().getString(R.string.Message)).toString();
                        DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = (AlertDialog) v.getTag();
                                alertDialog.dismiss();
                                downloadData();
                            }
                        }, null);
                    }
                });
            }
        }).start();
    }

    private ArrayList<String> getDelList() {
        ArrayList<String> delList = new ArrayList<>();
        ArrayList<String> fileNameList = new ArrayList<>();
        String fileName;
        for (int i = 0; i < ll_upload.getChildCount(); i++) {
            fileName = ll_upload.getChildAt(i).findViewById(R.id.upload_item_image).getTag(R.string.fileName).toString();
            fileNameList.add(fileName);
        }
        for (HashMap<String, Object> map : listItem) {
            if (!fileNameList.contains(map.get("showName").toString())) {
                delList.add(map.get("id").toString() + "," + map.get("showName").toString());
            }
        }
        return delList;
    }

    private ArrayList<String> getAddList() {
        ArrayList<String> addList = new ArrayList<>();
        ArrayList<String> fileNameList = new ArrayList<>();
        ArrayList<String> filePathList = new ArrayList<>();
        ArrayList<String> fileTypeList = new ArrayList<>();
        String fileName;
        String filePath;
        String fileType;
        View v;
        for (int i = 0; i < ll_upload.getChildCount(); i++) {
            v = ll_upload.getChildAt(i).findViewById(R.id.upload_item_image);
            fileName = v.getTag(R.string.fileName).toString();
            filePath = v.getTag(R.string.filePath).toString();
            fileType = v.getTag(R.string.fileType).toString();
            fileNameList.add(fileName);
            filePathList.add(filePath);
            fileTypeList.add(fileType);
        }
        for (HashMap<String, Object> map : listItem) {
            if (fileNameList.contains(map.get("showName").toString())) {
                int index = fileNameList.indexOf(map.get("showName").toString());
                fileNameList.remove(index);
                filePathList.remove(index);
                fileTypeList.remove(index);
            }
        }
        for (int i = 0; i < fileNameList.size(); i++) {
            addList.add(fileNameList.get(i) + "," + filePathList.get(i) + "," + fileTypeList.get(i));
        }
        return addList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CameraUtil.getInstance().activityResult(requestCode, resultCode, data, ll_upload, wr);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
