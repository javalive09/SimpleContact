
package com.peter.simplecontact.access.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.peter.simplecontact.access.db.DBOpenHelper;

/**
 * 基于DBOpenHelper的db控制类。 对外提供数据共享。好处是统一了数据访问的方式 。 Uri 代表要操作的数据。(每次操作前，要判断Uri)
 * 包含两部分：
 *  1.要操作的ContentProvider 2.对其中的什么数据进行操作
 * content://com.peter.providers.personprovider/person/10 即 ：scheme +
 * 主机名/authority + 路径
 * 
 * 使用provider时需要知道  uri，数据库db中各个列的名字
 * 
 * @author Peter
 */
public class PersonProvider extends ContentProvider {
    private DBOpenHelper dbOpenHelper;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);// 用于判断Uri匹配的类，UriMatcher.NO_MATCH是不匹配码
    private static final int PERSONS = 1;
    private static final int PERSON = 2;

    static {
        MATCHER.addURI("com.peter.providers.personprovider", "person", PERSONS);// authority,path,matchcode
        MATCHER.addURI("com.peter.providers.personprovider", "person/#", PERSON);// authority,path,matchcode
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DBOpenHelper(this.getContext());
        return true;
    }

    /**
     * 查询数据
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case PERSONS:
                return db.query("person", projection, selection, selectionArgs, null , null , sortOrder);
                
            case PERSON:
                long rowId = ContentUris.parseId(uri);//获取uri中的最后一位，即id
                String where = "id=" + rowId;
                if(selection != null && !selection.trim().equals("")){
                    where +=" and " + selection;//拼接where条件
                }
                return db.query("person", projection, where, selectionArgs, null , null , sortOrder);
            default:
                throw new IllegalArgumentException("this is unKnown Uri:" + uri);// 抛出参数非法异常
        }
    }
    
    /**
     * 获取类型
     */
    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case PERSONS:
                return "vnd.android.cursor.dir/person";//集合类型

            case PERSON:
                return "vnd.android.cursor.item/person";//单条记录

            default:
                throw new IllegalArgumentException("this is unKnown Uri:" + uri);// 抛出参数非法异常
        }
    }

    /**
     * 插入数据
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case PERSONS:
                long rowid = db.insert("person", "name", values);// 主键值
                Uri insertUri = ContentUris.withAppendedId(uri, rowid);// 拼接Uri
                return insertUri;

            default:
                throw new IllegalArgumentException("this is unKnown Uri:" + uri);// 抛出参数非法异常
        }
    }

    /**
     * 删除数据
     * return 返回影响数据的个数
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int num = 0;
        switch (MATCHER.match(uri)) {
            case PERSONS:
                num = db.delete("person", selection, selectionArgs);
                break;
            case PERSON:
                long rowId = ContentUris.parseId(uri);//获取uri中的最后一位，即id
                String where = "id=" + rowId;
                if(selection != null && !selection.trim().equals("")){
                    where +=" and " + selection;//拼接where条件
                }
                num = db.delete("person", where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("this is unKnown Uri:" + uri);// 抛出参数非法异常
        }
        return num;
    }

    /**
     * 更新数据
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int num = 0;
        switch (MATCHER.match(uri)) {
            case PERSONS:
                num = db.update("person", values, selection, selectionArgs);
                break;
            case PERSON:
                long rowId = ContentUris.parseId(uri);//获取uri中的最后一位，即id
                String where = "id=" + rowId;
                if(selection != null && !selection.trim().equals("")){
                    where +=" and " + selection;//拼接where条件
                }
                num = db.update("person", values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("this is unKnown Uri:" + uri);// 抛出参数非法异常
        }
        return num;
    }

}
