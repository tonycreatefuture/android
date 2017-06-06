package com.zhongdasoft.svwtrainnet.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zhongdasoft.svwtrainnet.greendao.entity.UserSearchHistory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_SEARCH_HISTORY".
*/
public class UserSearchHistoryDao extends AbstractDao<UserSearchHistory, Long> {

    public static final String TABLENAME = "USER_SEARCH_HISTORY";

    /**
     * Properties of entity UserSearchHistory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property UseWhere = new Property(2, String.class, "useWhere", false, "USE_WHERE");
        public final static Property Text = new Property(3, String.class, "text", false, "TEXT");
        public final static Property IsValid = new Property(4, Boolean.class, "isValid", false, "IS_VALID");
    }


    public UserSearchHistoryDao(DaoConfig config) {
        super(config);
    }
    
    public UserSearchHistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_SEARCH_HISTORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_NAME\" TEXT NOT NULL ," + // 1: userName
                "\"USE_WHERE\" TEXT NOT NULL ," + // 2: useWhere
                "\"TEXT\" TEXT NOT NULL ," + // 3: text
                "\"IS_VALID\" INTEGER);"); // 4: isValid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_SEARCH_HISTORY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserSearchHistory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserName());
        stmt.bindString(3, entity.getUseWhere());
        stmt.bindString(4, entity.getText());
 
        Boolean isValid = entity.getIsValid();
        if (isValid != null) {
            stmt.bindLong(5, isValid ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserSearchHistory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserName());
        stmt.bindString(3, entity.getUseWhere());
        stmt.bindString(4, entity.getText());
 
        Boolean isValid = entity.getIsValid();
        if (isValid != null) {
            stmt.bindLong(5, isValid ? 1L: 0L);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserSearchHistory readEntity(Cursor cursor, int offset) {
        UserSearchHistory entity = new UserSearchHistory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // userName
            cursor.getString(offset + 2), // useWhere
            cursor.getString(offset + 3), // text
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0 // isValid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserSearchHistory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.getString(offset + 1));
        entity.setUseWhere(cursor.getString(offset + 2));
        entity.setText(cursor.getString(offset + 3));
        entity.setIsValid(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserSearchHistory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserSearchHistory entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserSearchHistory entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}