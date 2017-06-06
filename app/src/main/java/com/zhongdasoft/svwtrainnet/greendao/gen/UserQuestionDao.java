package com.zhongdasoft.svwtrainnet.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestion;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_QUESTION".
*/
public class UserQuestionDao extends AbstractDao<UserQuestion, Long> {

    public static final String TABLENAME = "USER_QUESTION";

    /**
     * Properties of entity UserQuestion.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UqId = new Property(0, Long.class, "uqId", true, "_id");
        public final static Property DbName = new Property(1, String.class, "dbName", false, "DB_NAME");
        public final static Property PaperID = new Property(2, String.class, "paperID", false, "PAPER_ID");
        public final static Property QID = new Property(3, String.class, "qID", false, "Q_ID");
        public final static Property QOrder = new Property(4, String.class, "qOrder", false, "Q_ORDER");
        public final static Property EssayMark = new Property(5, String.class, "essayMark", false, "ESSAY_MARK");
        public final static Property CourseID = new Property(6, String.class, "courseID", false, "COURSE_ID");
        public final static Property ChapterID = new Property(7, String.class, "chapterID", false, "CHAPTER_ID");
        public final static Property Level = new Property(8, Integer.class, "level", false, "LEVEL");
        public final static Property QType = new Property(9, Integer.class, "qType", false, "Q_TYPE");
        public final static Property Content = new Property(10, String.class, "content", false, "CONTENT");
        public final static Property AID = new Property(11, String.class, "aID", false, "A_ID");
        public final static Property AnswerContent = new Property(12, String.class, "answerContent", false, "ANSWER_CONTENT");
        public final static Property AContent = new Property(13, String.class, "aContent", false, "A_CONTENT");
        public final static Property ReferMark = new Property(14, String.class, "referMark", false, "REFER_MARK");
        public final static Property CheckSum = new Property(15, Integer.class, "checkSum", false, "CHECK_SUM");
    }

    private DaoSession daoSession;


    public UserQuestionDao(DaoConfig config) {
        super(config);
    }
    
    public UserQuestionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_QUESTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: uqId
                "\"DB_NAME\" TEXT NOT NULL ," + // 1: dbName
                "\"PAPER_ID\" TEXT NOT NULL ," + // 2: paperID
                "\"Q_ID\" TEXT NOT NULL ," + // 3: qID
                "\"Q_ORDER\" TEXT NOT NULL ," + // 4: qOrder
                "\"ESSAY_MARK\" TEXT NOT NULL ," + // 5: essayMark
                "\"COURSE_ID\" TEXT NOT NULL ," + // 6: courseID
                "\"CHAPTER_ID\" TEXT," + // 7: chapterID
                "\"LEVEL\" INTEGER NOT NULL ," + // 8: level
                "\"Q_TYPE\" INTEGER NOT NULL ," + // 9: qType
                "\"CONTENT\" TEXT NOT NULL ," + // 10: content
                "\"A_ID\" TEXT NOT NULL ," + // 11: aID
                "\"ANSWER_CONTENT\" TEXT NOT NULL ," + // 12: answerContent
                "\"A_CONTENT\" TEXT," + // 13: aContent
                "\"REFER_MARK\" TEXT," + // 14: referMark
                "\"CHECK_SUM\" INTEGER);"); // 15: checkSum
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_QUESTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserQuestion entity) {
        stmt.clearBindings();
 
        Long uqId = entity.getUqId();
        if (uqId != null) {
            stmt.bindLong(1, uqId);
        }
        stmt.bindString(2, entity.getDbName());
        stmt.bindString(3, entity.getPaperID());
        stmt.bindString(4, entity.getQID());
        stmt.bindString(5, entity.getQOrder());
        stmt.bindString(6, entity.getEssayMark());
        stmt.bindString(7, entity.getCourseID());
 
        String chapterID = entity.getChapterID();
        if (chapterID != null) {
            stmt.bindString(8, chapterID);
        }
        stmt.bindLong(9, entity.getLevel());
        stmt.bindLong(10, entity.getQType());
        stmt.bindString(11, entity.getContent());
        stmt.bindString(12, entity.getAID());
        stmt.bindString(13, entity.getAnswerContent());
 
        String aContent = entity.getAContent();
        if (aContent != null) {
            stmt.bindString(14, aContent);
        }
 
        String referMark = entity.getReferMark();
        if (referMark != null) {
            stmt.bindString(15, referMark);
        }
 
        Integer checkSum = entity.getCheckSum();
        if (checkSum != null) {
            stmt.bindLong(16, checkSum);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserQuestion entity) {
        stmt.clearBindings();
 
        Long uqId = entity.getUqId();
        if (uqId != null) {
            stmt.bindLong(1, uqId);
        }
        stmt.bindString(2, entity.getDbName());
        stmt.bindString(3, entity.getPaperID());
        stmt.bindString(4, entity.getQID());
        stmt.bindString(5, entity.getQOrder());
        stmt.bindString(6, entity.getEssayMark());
        stmt.bindString(7, entity.getCourseID());
 
        String chapterID = entity.getChapterID();
        if (chapterID != null) {
            stmt.bindString(8, chapterID);
        }
        stmt.bindLong(9, entity.getLevel());
        stmt.bindLong(10, entity.getQType());
        stmt.bindString(11, entity.getContent());
        stmt.bindString(12, entity.getAID());
        stmt.bindString(13, entity.getAnswerContent());
 
        String aContent = entity.getAContent();
        if (aContent != null) {
            stmt.bindString(14, aContent);
        }
 
        String referMark = entity.getReferMark();
        if (referMark != null) {
            stmt.bindString(15, referMark);
        }
 
        Integer checkSum = entity.getCheckSum();
        if (checkSum != null) {
            stmt.bindLong(16, checkSum);
        }
    }

    @Override
    protected final void attachEntity(UserQuestion entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserQuestion readEntity(Cursor cursor, int offset) {
        UserQuestion entity = new UserQuestion( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // uqId
            cursor.getString(offset + 1), // dbName
            cursor.getString(offset + 2), // paperID
            cursor.getString(offset + 3), // qID
            cursor.getString(offset + 4), // qOrder
            cursor.getString(offset + 5), // essayMark
            cursor.getString(offset + 6), // courseID
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // chapterID
            cursor.getInt(offset + 8), // level
            cursor.getInt(offset + 9), // qType
            cursor.getString(offset + 10), // content
            cursor.getString(offset + 11), // aID
            cursor.getString(offset + 12), // answerContent
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // aContent
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // referMark
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15) // checkSum
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserQuestion entity, int offset) {
        entity.setUqId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDbName(cursor.getString(offset + 1));
        entity.setPaperID(cursor.getString(offset + 2));
        entity.setQID(cursor.getString(offset + 3));
        entity.setQOrder(cursor.getString(offset + 4));
        entity.setEssayMark(cursor.getString(offset + 5));
        entity.setCourseID(cursor.getString(offset + 6));
        entity.setChapterID(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLevel(cursor.getInt(offset + 8));
        entity.setQType(cursor.getInt(offset + 9));
        entity.setContent(cursor.getString(offset + 10));
        entity.setAID(cursor.getString(offset + 11));
        entity.setAnswerContent(cursor.getString(offset + 12));
        entity.setAContent(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setReferMark(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setCheckSum(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserQuestion entity, long rowId) {
        entity.setUqId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserQuestion entity) {
        if(entity != null) {
            return entity.getUqId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserQuestion entity) {
        return entity.getUqId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}