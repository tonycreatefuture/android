package com.zhongdasoft.svwtrainnet.greendao.gen;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestion;

import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestionOption;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_QUESTION_OPTION".
*/
public class UserQuestionOptionDao extends AbstractDao<UserQuestionOption, Long> {

    public static final String TABLENAME = "USER_QUESTION_OPTION";

    /**
     * Properties of entity UserQuestionOption.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UqoId = new Property(0, Long.class, "uqoId", true, "_id");
        public final static Property UqId = new Property(1, Long.class, "uqId", false, "UQ_ID");
        public final static Property QID = new Property(2, String.class, "qID", false, "Q_ID");
        public final static Property OptionID = new Property(3, String.class, "optionID", false, "OPTION_ID");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
    }

    private DaoSession daoSession;

    private Query<UserQuestionOption> userQuestion_QOptionsQuery;

    public UserQuestionOptionDao(DaoConfig config) {
        super(config);
    }
    
    public UserQuestionOptionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_QUESTION_OPTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: uqoId
                "\"UQ_ID\" INTEGER NOT NULL ," + // 1: uqId
                "\"Q_ID\" TEXT NOT NULL ," + // 2: qID
                "\"OPTION_ID\" TEXT NOT NULL ," + // 3: optionID
                "\"CONTENT\" TEXT NOT NULL );"); // 4: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_QUESTION_OPTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserQuestionOption entity) {
        stmt.clearBindings();
 
        Long uqoId = entity.getUqoId();
        if (uqoId != null) {
            stmt.bindLong(1, uqoId);
        }
        stmt.bindLong(2, entity.getUqId());
        stmt.bindString(3, entity.getQID());
        stmt.bindString(4, entity.getOptionID());
        stmt.bindString(5, entity.getContent());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserQuestionOption entity) {
        stmt.clearBindings();
 
        Long uqoId = entity.getUqoId();
        if (uqoId != null) {
            stmt.bindLong(1, uqoId);
        }
        stmt.bindLong(2, entity.getUqId());
        stmt.bindString(3, entity.getQID());
        stmt.bindString(4, entity.getOptionID());
        stmt.bindString(5, entity.getContent());
    }

    @Override
    protected final void attachEntity(UserQuestionOption entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserQuestionOption readEntity(Cursor cursor, int offset) {
        UserQuestionOption entity = new UserQuestionOption( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // uqoId
            cursor.getLong(offset + 1), // uqId
            cursor.getString(offset + 2), // qID
            cursor.getString(offset + 3), // optionID
            cursor.getString(offset + 4) // content
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserQuestionOption entity, int offset) {
        entity.setUqoId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUqId(cursor.getLong(offset + 1));
        entity.setQID(cursor.getString(offset + 2));
        entity.setOptionID(cursor.getString(offset + 3));
        entity.setContent(cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserQuestionOption entity, long rowId) {
        entity.setUqoId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserQuestionOption entity) {
        if(entity != null) {
            return entity.getUqoId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserQuestionOption entity) {
        return entity.getUqoId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "qOptions" to-many relationship of UserQuestion. */
    public List<UserQuestionOption> _queryUserQuestion_QOptions(Long uqId) {
        synchronized (this) {
            if (userQuestion_QOptionsQuery == null) {
                QueryBuilder<UserQuestionOption> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.UqId.eq(null));
                userQuestion_QOptionsQuery = queryBuilder.build();
            }
        }
        Query<UserQuestionOption> query = userQuestion_QOptionsQuery.forCurrentThread();
        query.setParameter(0, uqId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserQuestionDao().getAllColumns());
            builder.append(" FROM USER_QUESTION_OPTION T");
            builder.append(" LEFT JOIN USER_QUESTION T0 ON T.\"UQ_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected UserQuestionOption loadCurrentDeep(Cursor cursor, boolean lock) {
        UserQuestionOption entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        UserQuestion uQuestion = loadCurrentOther(daoSession.getUserQuestionDao(), cursor, offset);
         if(uQuestion != null) {
            entity.setUQuestion(uQuestion);
        }

        return entity;    
    }

    public UserQuestionOption loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<UserQuestionOption> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<UserQuestionOption> list = new ArrayList<UserQuestionOption>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<UserQuestionOption> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<UserQuestionOption> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
