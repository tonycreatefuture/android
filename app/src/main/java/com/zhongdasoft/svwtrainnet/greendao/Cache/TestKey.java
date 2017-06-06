package com.zhongdasoft.svwtrainnet.greendao.Cache;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/10 9:41
 * 修改人：Administrator
 * 修改时间：2016/7/10 9:41
 * 修改备注：
 */
public class TestKey {
    /* 提前进入考场 时间（秒） */
    public static final int ENTER_TIME = 30;
    /* 考试状态 */
    public static final int TEST_PREPARED = 0;
    public static final int TEST_RUNNING = 1;
    public static final int TEST_FINISHED = 2;
    public static final int TEST_SUBMIT = 3;

    public static final int SingleQuestionType = 8;
    public static final int MultiQuestionType = 9;
    public static final int JudgeQuestionType = 12;

    public static final int PaperLoad = 101;
    public static final int PaperSubmit = 102;
    public static final int PaperProgress = 103;
    public static final int PaperNext = 104;
    public static final int PaperPrevious = 105;
}
