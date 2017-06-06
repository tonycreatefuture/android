package com.zhongdasoft.svwtrainnet.util;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.InnerCourse;
import com.zhongdasoft.svwtrainnet.adapter.MyGridViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.module.exam.TestActivity;
import com.zhongdasoft.svwtrainnet.module.exam.TestCenterActivity;
import com.zhongdasoft.svwtrainnet.module.more.MoreAboutUsActivity;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.widget.DragGridView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DialogUtil {
    private static DialogUtil instance;
    private MyDismiss myDismiss;

    private DialogUtil() {
    }

    public synchronized static DialogUtil getInstance() {
        if (null == instance) {
            instance = new DialogUtil();
        }
        return instance;
    }

    public void showScanDialog(final WeakReference<? extends BaseActivity> wr, String title,
                               String content) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.continuePreview();
            }
        });
        alertDialog.show();
        setWindow(alertDialog, title, content, null);
    }

    private void setWindow(final AlertDialog alertDialog, String title, String content, ArrayList<View.OnClickListener> listener) {
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_simple);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_subTitle = (TextView) window.findViewById(R.id.tv_subTitle);
        tv_subTitle.setText(content);
        TextView tv_iKnow = (TextView) window.findViewById(R.id.tv_iKnow);
        if (null == listener) {
            tv_iKnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        } else {
            tv_iKnow.setVisibility(View.GONE);
            if (listener.size() == 2) {
                TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
                tv_ok.setVisibility(View.VISIBLE);
                tv_ok.setTag(alertDialog);
                tv_ok.setOnClickListener(listener.get(0));
                TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
                tv_cancel.setVisibility(View.VISIBLE);
                tv_cancel.setTag(alertDialog);
                tv_cancel.setOnClickListener(listener.get(1));
            } else if (listener.size() == 1) {
                tv_iKnow.setVisibility(View.VISIBLE);
                tv_iKnow.setTag(alertDialog);
                tv_iKnow.setText(R.string.OK);
                tv_iKnow.setOnClickListener(listener.get(0));
            } else {
                TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
                tv_ok.setVisibility(View.VISIBLE);
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        }
    }

    public void showScanDialog(final WeakReference<? extends BaseActivity> wr, final String title,
                               String content, Drawable image, final String url, String okBtnText) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.continuePreview();
            }
        });
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog);

        ImageView iv_image = (ImageView) window.findViewById(R.id.iv_image);
        if (null != image) {
            iv_image.setVisibility(View.VISIBLE);
            iv_image.setImageDrawable(image);
        } else {
            iv_image.setVisibility(View.GONE);
        }

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_subTitle = (TextView) window.findViewById(R.id.tv_subTitle);
        tv_subTitle.setText(content);
        TextView tv_buttonText = (TextView) window.findViewById(R.id.tv_buttonText);
        tv_buttonText.setText(okBtnText);
        RelativeLayout rl_event = (RelativeLayout) window.findViewById(R.id.rl_event);

        rl_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!StringUtil.isNullOrEmpty(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("scan", title);
                    bundle.putString("item", url);
                    activity.readyGo(TvContentActivity.class, bundle);
                }
            }
        });
    }

    public void showSharedDialog(final WeakReference<? extends BaseActivity> wr, Drawable image, final String url) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.continuePreview();
            }
        });
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_shared);

        ImageView iv_image = (ImageView) window.findViewById(R.id.iv_image);
        iv_image.setImageDrawable(image);

        ImageView imgSharedLink = (ImageView) window.findViewById(R.id.imgSharedLink);
        imgSharedLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipUtil.CopyData(activity, url);
                ToastUtil.show(activity, activity.getResources().getString(R.string.copyFinished));
            }
        });
    }

    public void showTestDialog(final WeakReference<? extends BaseActivity> wr, final UserPaper up) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_exam);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(up.getTestName());

        TextView tv_questionNum = (TextView) window.findViewById(R.id.tv_questionNum);
        Long questionNum = DaoQuery.getInstance().getQuestionCount(up.getDbName());
        tv_questionNum.setText("" + questionNum);
        TextView tv_scoreNum = (TextView) window.findViewById(R.id.tv_scoreNum);
        tv_scoreNum.setText(up.getPaperScore() + "");
        TextView tv_timeNum = (TextView) window.findViewById(R.id.tv_timeNum);
        tv_timeNum.setText(up.getDuration() + "");
        TextView tv_test = (TextView) window.findViewById(R.id.tv_test);
        tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Bundle bundle = new Bundle();
                String realBeginTime = MySharedPreferences.getInstance().getCurrentTime(activity);
                bundle.putString("dbName", up.getDbName());
                bundle.putString("paperId", up.getPaperId());
                bundle.putString("examineeId", up.getExamineeId());
                bundle.putString("testName", up.getTestName());
                bundle.putBoolean("showScore", up.getShowScore());
                bundle.putString("realBeginTime", realBeginTime);
                activity.readyGoThenKill(TestActivity.class, bundle);
            }
        });
    }

    public void showScoreDialog(final WeakReference<? extends BaseActivity> wr, String userInfo, String dbName) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isChanged", true);
                if (!activity.getClass().getName().equals(ActivityKey.Exam)) {
                    activity.readyGoThenKill(TestCenterActivity.class, bundle);
                } else {
                    activity.readyGo(TestCenterActivity.class, bundle);
                }
            }
        });
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_score);

        Float score = DaoQuery.getInstance().getUserQuestionScore(dbName);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(userInfo + "考试已完成 得分：" + score);

        String scoreInfo = DaoQuery.getInstance().getUserQuestionScoreInfo(dbName);
        JsonParser parse = new JsonParser();
        JsonArray ja = parse.parse(scoreInfo).getAsJsonArray();

        int[] tvAll = {R.id.tv_singleAll, R.id.tv_multiAll, R.id.tv_judgeAll};
        int[] tvRight = {R.id.tv_singleRightNum, R.id.tv_multiRightNum, R.id.tv_judgeRightNum};
        int[] tvWrong = {R.id.tv_singleWrongNum, R.id.tv_multiWrongNum, R.id.tv_judgeWrongNum};
        int[] tvScore = {R.id.tv_singleScoreInfo, R.id.tv_multiScoreInfo, R.id.tv_judgeScoreInfo};
        TextView tv_All;
        TextView tv_Right;
        TextView tv_Wrong;
        TextView tv_Score;
        for (int i = 0; i < tvAll.length; i++) {
            tv_All = (TextView) window.findViewById(tvAll[i]);
            tv_All.setText(ja.get(i).getAsJsonObject().get("all").getAsString());
            tv_Right = (TextView) window.findViewById(tvRight[i]);
            tv_Right.setText(ja.get(i).getAsJsonObject().get("right").getAsString());
            tv_Wrong = (TextView) window.findViewById(tvWrong[i]);
            tv_Wrong.setText(ja.get(i).getAsJsonObject().get("wrong").getAsString());
            tv_Score = (TextView) window.findViewById(tvScore[i]);
            tv_Score.setText("分数 " + ja.get(i).getAsJsonObject().get("score").getAsString());
        }
        TextView tv_testReturn = (TextView) window.findViewById(R.id.tv_testReturn);
        tv_testReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void showDialog(final WeakReference<? extends BaseActivity> wr, String title,
                           String content, View.OnClickListener ok, View.OnClickListener cancel) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        alertDialog.show();
        ArrayList<View.OnClickListener> listener = new ArrayList<>();
        if (null != ok) {
            listener.add(ok);
        }
        if (null != cancel) {
            listener.add(cancel);
        }
        if (listener.size() == 0) {
            listener = null;
        }
        setWindow(alertDialog, title, content, listener);
    }

    public void showImageDialog(final WeakReference<? extends BaseActivity> wr, final Bitmap bm, final ImageDismiss imageDismiss) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != bm) {
                    bm.recycle();
                }
            }
        });
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_imagepre);

        ImageView tv_image = (ImageView) window.findViewById(R.id.iv_image);
        tv_image.setImageBitmap(bm);
        tv_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != imageDismiss) {
                    imageDismiss.handleDismiss();
                }
                alertDialog.dismiss();
            }
        });
    }

    public void showListDialog(final WeakReference<? extends BaseActivity> wr, String title,
                               final String[] items, MyDismiss dismiss) {
        this.myDismiss = dismiss;
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDismiss.handleDismiss(which, null);
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        alertDialog.show();
    }

    public void showInputDialog(final WeakReference<? extends BaseActivity> wr, String title, String value, int type, MyDismiss dismiss) {
        this.myDismiss = dismiss;
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder = builder.setTitle(title);
        if (0 == type) {
            final EditText input = new EditText(activity);
            input.setFocusable(true);
            input.setText(value);
            builder.setView(input).setPositiveButton(activity.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = myDismiss.handleDismiss(-1, input.getText().toString());
                    if (null == str) {
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(activity, str);
                    }
//                    dialog.dismiss();
//                    Field field;
//                    if (null == str) {
//                        try {
//                            field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
//                            field.setAccessible(true);
//                            field.set(dialog, true);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
//                            field.setAccessible(true);
//                            field.set(dialog, false);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                        ToastUtil.show(activity, str);
//                    }
                }
            });
        } else if (1 == type) {
            final CheckBox cb = new CheckBox(activity);
            cb.setFocusable(true);
            if ("false".equals(value.toLowerCase())) {
                cb.setChecked(false);
            } else {
                cb.setChecked(true);
            }
            builder.setView(cb).setPositiveButton(activity.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = myDismiss.handleDismiss(-1, cb.isChecked() ? "true" : "false");
                    if (null == str) {
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(activity, str);
                    }
                }
            });
        } else {

        }
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        alertDialog.show();
    }

    public void showCourseDialog(final WeakReference<? extends BaseActivity> wr, final String title, final CourseDismiss courseDismiss) {
        final BaseActivity activity = wr.get();
        if (null == wr || null == activity || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.create();
        //alertDialog.setCancelable(false);

        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.trainnet_dialog_course);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_subTitle = (TextView) window.findViewById(R.id.tv_subTitle);
        tv_subTitle.setHint(title);
        ListView lv = (ListView) window.findViewById(R.id.listview_course);
        final InnerCourse innerCourse = new InnerCourse(activity, lv, tv_subTitle, alertDialog);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != innerCourse) {
                    courseDismiss.handleDismiss(innerCourse.getCourseNo(), innerCourse.getCourseName());
                }
            }
        });
    }

    public interface ImageDismiss {
        void handleDismiss();
    }

    public interface MyDismiss {
        String handleDismiss(int which, String str);
    }

    public interface CourseDismiss {
        void handleDismiss(String courseNo, String courseName);
    }
}
