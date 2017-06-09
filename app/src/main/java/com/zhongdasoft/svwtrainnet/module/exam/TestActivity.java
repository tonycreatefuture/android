package com.zhongdasoft.svwtrainnet.module.exam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.R.drawable;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.TestKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestion;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestionOption;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;
import com.zhongdasoft.svwtrainnet.widget.BorderTextView;
import com.zhongdasoft.svwtrainnet.widget.MyScrollView;
import com.zhongdasoft.svwtrainnet.widget.timer.ExamTimer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends BaseActivity implements OnGestureListener {
    private Button btnProgressQuery;
    private Button btnSubmitPaper;
    private TextView tvPrevious;
    private TextView tvNext;
    private TextView tvPaperTitle;
    private TextView tvPaperFinished;
    private TextView tv;
    private RadioGroup rg;
    private RadioButton rb;
    private CheckBox cb;
    private String testName;
    private String dbName;
    private ViewFlipper viewFlipper = null;
    private GestureDetector gestureDetector = null;
    private List<UserQuestion> userQuestionList;
    private Context context;
    private WeakReference<? extends BaseActivity> wr;
    private GridLayout glayout;
    private String realBeginTime;
    private List<MyScrollView> layoutList;
    private ExamTimer mc;
    private String errorMsg = null;
    private String currentTime;
    private float currentX = 0;
    private LinearLayout.LayoutParams lp;
    private Long time = -1L;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //禁止截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        context = this;
        wr = new WeakReference<>(this);
        Intent i = getIntent();
        dbName = i.getStringExtra("dbName");
        testName = i.getStringExtra("testName");
        realBeginTime = i.getStringExtra("realBeginTime");

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        // 生成GestureDetector对象，用于检测手势事件
        gestureDetector = new GestureDetector(this);

        tvPrevious = (TextView) findViewById(R.id.previous);
        tvPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(TestKey.PaperPrevious);
            }
        });

        tvNext = (TextView) findViewById(R.id.next);
        tvNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(TestKey.PaperNext);
            }
        });

        tvPaperTitle = (TextView) findViewById(R.id.paperTitle);
        tvPaperTitle.setText(testName);

        tvPaperFinished = (TextView) findViewById(R.id.paperFinished);

        float textSize = Scale.getTextSize(TestActivity.this);
        tvPaperTitle.setTextSize(textSize);

        btnProgressQuery = (Button) findViewById(R.id.progressQuery);
        btnProgressQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(TestKey.PaperProgress);
            }
        });
        btnSubmitPaper = (Button) findViewById(R.id.submitPaper);
        btnSubmitPaper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(TestKey.PaperSubmit);
            }
        });

        tvPrevious.setTextSize(textSize);
        tvNext.setTextSize(textSize);
        tv = (TextView) findViewById(R.id.countdown);

        btnProgressQuery.setTextSize(textSize);
        btnSubmitPaper.setTextSize(textSize);

        Waiting.show(this, context.getResources().getString(R.string.LoadingTestContent));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutList = getLayOutList();
                handler.sendEmptyMessage(TestKey.PaperLoad);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.nettest_test;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TestKey.PaperNext:
                    next();
                    break;
                case TestKey.PaperPrevious:
                    previous();
                    break;
                case TestKey.PaperProgress:
                    progressQuery();
                    break;
                case TestKey.PaperSubmit:
                    submitPaper();
                    break;
                case TestKey.PaperLoad:
                    loadPaper();
                    break;
            }
            return false;
        }
    });

    private void loadPaper() {
        if (errorMsg != null) {
            Waiting.dismiss();
            ToastUtil.show(TestActivity.this, errorMsg);
            return;
        }
        for (MyScrollView layout : layoutList) {
            viewFlipper.addView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }
        // 设置选中的题目
        String selectedQuestion = MySharedPreferences.getInstance().getString(
                "selectedQuestion" + dbName);
        int iSelectedQuestion = (selectedQuestion == null || ""
                .equals(selectedQuestion)) ? 0 : Integer
                .parseInt(selectedQuestion);
        viewFlipper.setDisplayedChild(iSelectedQuestion);
        setPreviousNextIcon(iSelectedQuestion);

        tv = (TextView) findViewById(R.id.countdown);
        float textSize = Scale.getTextSize(TestActivity.this);
        tv.setTextSize(textSize);
        if (realBeginTime == null) {
            currentTime = MySharedPreferences.getInstance().getCurrentTime();
            realBeginTime = currentTime;
        }
        UserPaper up = DaoQuery.getInstance().findUserPaperByDB(dbName);
        up.setRealBeginTime(realBeginTime);
        CRUD.getInstance().UpdateUserPaper(up);
        setCountDownTime(1);
        Waiting.dismiss();
    }

    private void submitPaper() {
        String text;
        int iCount = userQuestionList.size() - getCompleteQuestions();
        if (iCount > 0) {
            text = "您还有" + iCount + "道题没做，确认交卷？";
        } else {
            text = "您已完成考试，确认交卷？";
        }
        DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), text, new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = (AlertDialog) v.getTag();
                alertDialog.dismiss();
                // 这里需要完成更新本地库对应试卷的状态（已交卷）
                if (mc != null) {
                    mc.cancel();
                }
                UserPaper up = DaoQuery.getInstance().findUserPaperByDB(dbName);
                WebserviceUtil.getInstance().submitExam(wr, up);
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = (AlertDialog) v.getTag();
                alertDialog.dismiss();
            }
        });
    }

    private void initProgressQuery() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.nettest_progressquery,
                null);
        float textSize = Scale.getTextSize(TestActivity.this);

        TextView tv = (TextView) layout
                .findViewById(R.id.progressTextView1);
        tv.setText(getCompleteQuestions() + "/" + userQuestionList.size());
        // tv.setTextSize(textSize);

        glayout = (GridLayout) layout
                .findViewById(R.id.progressGridLayout1);
        int colNum = glayout.getColumnCount();
        int rowNum = userQuestionList.size() / colNum + userQuestionList.size() % colNum == 0 ? 0 : 1;
        glayout.setRowCount(rowNum);
        glayout.setVerticalScrollBarEnabled(true);
        glayout.setUseDefaultMargins(true);

        int pQuery = Scale.getRectangle(TestActivity.this);
        BorderTextView btv;
        for (int i = 0; i < userQuestionList.size(); ++i) {
            btv = new BorderTextView(context);
            btv.setText((i + 1) + "");
            if (i + 1 >= 100) {
                btv.setTextSize(textSize - 5);
            } else {
                btv.setTextSize(textSize);
            }
            btv.setTextColor(Color.rgb(102, 102, 102));
            btv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            btv.setTag(i);
            btv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = Integer.parseInt(v.getTag().toString());
                    setBackgroundColor(i);
                    viewFlipper.setDisplayedChild(i);
                    setPreviousNextIcon(i);
                }
            });
            glayout.addView(btv, pQuery, pQuery);
        }
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void progressQuery() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            return;
        }
        if (null == popupWindow) {
            initProgressQuery();
        }
        popupWindow.showAsDropDown(btnProgressQuery);
        setBackgroundColor(viewFlipper.getDisplayedChild());
    }

    // 答题监听
    View.OnTouchListener answerListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int i = Integer.parseInt(v.getTag().toString());
            int j = i % 100;
            i = i / 100;
            int qType = userQuestionList.get(i).getQType();
            int iStatus = 0;
            boolean isLongClick = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentX = event.getX();
                    time = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(currentX - event.getX()) > 50) {
                        iStatus = 1;
                    } else {
                        iStatus = 2;
                    }
                    if (System.currentTimeMillis() - time > 200L) {
                        isLongClick = true;
                    }
                    break;
            }
            UserQuestion userQuestion = userQuestionList.get(i);
            if (0 == iStatus) {
            } else if (1 == iStatus) {
            } else {
                int checkNum = userQuestion.getCheckSum();
                String aContent = StringUtil.isNullOrEmpty(userQuestion.getAContent()) ? "" : userQuestion.getAContent();
                CheckBox cb;
                RadioButton rb;
                String newContent = null;
                if (TestKey.MultiQuestionType == qType) {
                    cb = (CheckBox) v;
                    Log.d("---longclick---", "cb.isDirty():" + cb.isDirty());
                    if (!isLongClick) {
                        cb.setChecked(!cb.isChecked());
                        if (cb.isChecked() && !aContent.contains(j + ",,")) {
                            userQuestion.setCheckSum(checkNum + 1);
                            newContent = insertIntoContent(aContent, j);
                            userQuestion.setAContent(newContent);
                        } else if (!cb.isChecked() && aContent.contains(j + ",,")) {
                            userQuestion.setCheckSum(checkNum - 1);
                            userQuestion.setAContent(aContent.replace(j + ",,", ""));
                        }
                    } else {
                        if (!cb.isChecked() && !aContent.contains(j + ",,")) {
                            userQuestion.setCheckSum(checkNum + 1);
                            newContent = insertIntoContent(aContent, j);
                            userQuestion.setAContent(newContent);
                        } else if (cb.isChecked() && aContent.contains(j + ",,")) {
                            userQuestion.setCheckSum(checkNum - 1);
                            userQuestion.setAContent(aContent.replace(j + ",,", ""));
                        }
                    }
                } else {
                    rb = (RadioButton) v;
                    rb.setChecked(true);
                    if (rb.isChecked()) {
                        userQuestion.setCheckSum(1);
                        userQuestion.setAContent(j + "");
                    } else {
                        userQuestion.setCheckSum(0);
                        userQuestion.setAContent("");
                    }
                }
                CRUD.getInstance().UpdateUserQuestion(userQuestion);
            }
            return false;
        }

        private String insertIntoContent(String aContent, int j) {
            if (StringUtil.isNullOrEmpty(aContent)) {
                return j + ",,";
            }
            String[] array = aContent.split(",,");
            int a[] = new int[array.length + 1];
            a[array.length] = j;
            int i = 0;
            for (String str : array) {
                if (!StringUtil.isNullOrEmpty(str)) {
                    a[i] = Integer.parseInt(str);
                    ++i;
                }
            }
            Arrays.sort(a);
            StringBuilder sb = new StringBuilder();
            for (int iNum : a) {
                sb.append(iNum).append(",,");
            }
            return sb.toString();
        }
    };

    /**
     * 加载考试试卷
     *
     * @return
     */
    private List<MyScrollView> getLayOutList() {
        userQuestionList = DaoQuery.getInstance().getUserQuestionList(dbName);
        MyScrollView sv;
        LinearLayout ll;
        float textSize = Scale.getTextSize(TestActivity.this);
        List<MyScrollView> layoutList = new ArrayList<>();
        tvPaperFinished.setText("(1/" + userQuestionList.size() + ")");
        String optionStr;
        for (int i = 0; i < userQuestionList.size(); ++i) {
            sv = new MyScrollView(this);
            sv.setGestureDetector(gestureDetector);
            ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            tv = new TextView(this);
            tv.setText((i + 1) + "." + HtmlUtil.deleteHtml(userQuestionList.get(i).getContent()));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(textSize);
            tv.setGravity(Gravity.LEFT);
            if (TestKey.SingleQuestionType == userQuestionList.get(i).getQType()) {
                tv.setBackgroundResource(drawable.nettest_qtype_single);
                rg = new RadioGroup(this);
                rg.setOrientation(LinearLayout.VERTICAL);
                rg.setContentDescription("单选题");
                List<UserQuestionOption> optionList = DaoQuery.getInstance().getUserQuestionOptionList(userQuestionList.get(i).getUqId());
                for (int j = 0; j < optionList.size(); ++j) {
                    rb = new RadioButton(this);
                    optionStr = HtmlUtil.fromHtml(optionList.get(j).getContent()).toString();
                    if (StringUtil.isNullOrEmpty(optionStr)) {
                        optionStr = optionList.get(j).getContent();
                    }
                    rb.setText(optionStr);
                    rb.setTextSize(textSize);
                    rb.setTextColor(Color.BLACK);
                    // rb.setButtonDrawable(android.R.color.transparent);
                    // rb.setBackgroundResource(R.drawable.radio);
                    rb.setTag(i * 100 + (j + 1));
                    rb.setOnTouchListener(answerListener);
                    rg.addView(rb, getLp(5));
                    if (!StringUtil.isNullOrEmpty(userQuestionList.get(i).getAContent())
                            && (j + 1) == Integer.parseInt(userQuestionList.get(i).getAContent())) {
                        rg.check(rb.getId());
                    }
                }
                ll.addView(tv, getLp(0));
                ll.addView(rg, getLp(20));
            } else if (TestKey.MultiQuestionType == userQuestionList.get(i).getQType()) {
                tv.setBackgroundResource(drawable.nettest_qtype_mutiple);
                ll.addView(tv, getLp(0));
                List<UserQuestionOption> optionList = DaoQuery.getInstance().getUserQuestionOptionList(userQuestionList.get(i).getUqId());
                for (int j = 0; j < optionList.size(); ++j) {
                    cb = new CheckBox(this);
                    if (!StringUtil.isNullOrEmpty(userQuestionList.get(i).getAContent())) {
                        if (userQuestionList.get(i).getAContent().contains((j + 1) + ",,")) {
                            cb.setChecked(true);
                        }
                    }
                    optionStr = HtmlUtil.fromHtml(optionList.get(j).getContent()).toString();
                    if (StringUtil.isNullOrEmpty(optionStr)) {
                        optionStr = optionList.get(j).getContent();
                    }
                    cb.setText(optionStr);
                    cb.setTextSize(textSize);
                    cb.setTextColor(Color.BLACK);
                    // cb.setButtonDrawable(android.R.color.transparent);
                    // cb.setBackgroundResource(R.drawable.checkbox);
                    cb.setTag(i * 100 + (j + 1));
                    cb.setOnTouchListener(answerListener);
                    if (j == 0) {
                        ll.addView(cb, getLp(25));
                    } else {
                        ll.addView(cb, getLp(5));
                    }
                }
            } else if (TestKey.JudgeQuestionType == userQuestionList.get(i).getQType()) {
                tv.setBackgroundResource(drawable.nettest_qtype_judgement);
                rg = new RadioGroup(this);
                rg.setOrientation(LinearLayout.VERTICAL);
                rg.setContentDescription("判断题");
                for (int j = 1; j >= 0; --j) {
                    rb = new RadioButton(this);
                    rb.setTag(i * 100 + j);
                    rb.setText(j == 1 ? "是" : "否");
                    rb.setTextSize(textSize);
                    rb.setTextColor(Color.BLACK);
                    // rb.setButtonDrawable(android.R.color.transparent);
                    // rb.setBackgroundResource(R.drawable.radio);
                    rb.setOnTouchListener(answerListener);
                    rg.addView(rb, getLp(5));
                    if (!StringUtil.isNullOrEmpty(userQuestionList.get(i).getAContent())
                            && j == Integer.parseInt(userQuestionList.get(i).getAContent())) {
                        rg.check(rb.getId());
                    }
                }
                ll.addView(tv, getLp(0));
                ll.addView(rg, getLp(20));
            } else {

            }
            sv.addView(ll);
            layoutList.add(sv);
        }
        return layoutList;
    }

    private LinearLayout.LayoutParams getLp(int topMargin) {
        if (null == lp) {
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        lp.topMargin = topMargin;
        return lp;
    }

    private void setPreviousNextIcon(int i) {
        if (i == 0) {
            next();
            previous();
        } else if (i == viewFlipper.getChildCount() - 1) {
            previous();
            next();
        } else {
            previous();
            next();
        }
    }

    private void setCountDownTime(long value) {
        if (mc != null) {
            mc.cancel();
        }
        UserPaper userPaper = DaoQuery.getInstance().getUserPaperByDB(dbName);
        mc = new ExamTimer(userPaper.getLeftTime(), value, wr, tv, dbName);
        mc.start();
    }

    private void setBackgroundColor(int iSelected) {
        BorderTextView btv;
        for (int i = 0; i < glayout.getChildCount(); ++i) {
            if (!StringUtil.isNullOrEmpty(glayout.getChildAt(i).getTag().toString())) {
                btv = (BorderTextView) glayout.getChildAt(i);
                if (userQuestionList.get(i).getCheckSum() > 0) {
                    btv.setBackgroundColor(Color.rgb(227, 227, 227));
                } else {
                    btv.setBackgroundColor(Color.WHITE);
                }
                btv.setTextColor(Color.rgb(102, 102, 102));
            }
        }
        btv = (BorderTextView) glayout.getChildAt(iSelected);
        btv.setTextColor(Color.rgb(2, 54, 126));
    }

    private int getCompleteQuestions() {
        int iCompleteQuestions = 0;
        for (int i = 0; i < userQuestionList.size(); ++i) {
            if (userQuestionList.get(i).getCheckSum() > 0) {
                ++iCompleteQuestions;
            }
        }
        return iCompleteQuestions;
    }

    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        // 对手指滑动的距离进行了计算，如果滑动距离大于120像素，就做切换动作，否则不做任何切换动作。
        // 从左向右滑动
        if (arg0.getX() - arg1.getX() > 120) {
            return next();
        }// 从右向左滑动
        else if (arg0.getX() - arg1.getX() < -120) {
            return previous();
        }
        return true;
    }

    private boolean next() {
        if (this.viewFlipper.getDisplayedChild() >= userQuestionList.size() - 2) {
            tvNext.setBackgroundResource(drawable.nettest_icon_next2);
            tvNext.setEnabled(false);
        } else {
            tvNext.setBackgroundResource(drawable.nettest_icon_next1);
            tvNext.setEnabled(true);
        }
        tvPrevious.setBackgroundResource(drawable.nettest_icon_previous1);
        tvPrevious.setEnabled(true);
        if (this.viewFlipper.getDisplayedChild() == userQuestionList.size() - 1) {
            saveSelectedQuestion(userQuestionList.size() - 1);
            this.viewFlipper.stopFlipping();
            return false;
        } else {
            setPage(this.viewFlipper.getDisplayedChild() + 2, true);
            return true;
        }
    }

    private boolean previous() {
        if (this.viewFlipper.getDisplayedChild() <= 1) {
            tvPrevious.setBackgroundResource(drawable.nettest_icon_previous2);
            tvPrevious.setEnabled(false);
        } else {
            tvPrevious.setBackgroundResource(drawable.nettest_icon_previous1);
            tvPrevious.setEnabled(true);
        }
        tvNext.setBackgroundResource(drawable.nettest_icon_next1);
        tvNext.setEnabled(true);
        if (this.viewFlipper.getDisplayedChild() == 0) {
            saveSelectedQuestion(0);
            this.viewFlipper.stopFlipping();
            return false;
        } else {
            setPage(this.viewFlipper.getDisplayedChild(), false);
            return true;
        }
    }

    private void setPage(int pos, boolean isLeft) {
        tvPaperFinished.setText("(" + pos + "/" + userQuestionList.size() + ")");
        saveSelectedQuestion(pos - 1);
        if (isLeft) {
            this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_in));
            this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_out));
            this.viewFlipper.showNext();
        } else {
            this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_right_in));
            this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_right_out));
            this.viewFlipper.showPrevious();
        }
    }

    private void saveSelectedQuestion(int pos) {
        // 保存当前选中题目
        MySharedPreferences.getInstance().setStoreString("selectedQuestion" + dbName, pos
                + "");
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.viewFlipper.getChildAt(this.viewFlipper.getDisplayedChild()).setEnabled(false);
        this.gestureDetector.onTouchEvent(ev);//在这里先处理下你的手势左右滑动事件
        this.viewFlipper.getChildAt(this.viewFlipper.getDisplayedChild()).setEnabled(true);
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onDestroy() {
        Waiting.dismiss();
        if (null != popupWindow) {
            popupWindow.dismiss();
        }
        super.onDestroy();
    }
}
