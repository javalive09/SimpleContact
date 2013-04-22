package com.peter.simplecontact.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

public class PersonProviderTest extends AndroidTestCase {
    
    /**
     * 插入数据
     */
    public void testInsert() throws Exception{
        Uri uri = Uri.parse("content://com.peter.providers.personprovider/person");
        ContentResolver contentResolver = getContext().getContentResolver();
        
        ContentValues values = new ContentValues();
        values.put("name", "javaLive09");
        values.put("phone", "11111111111");
        
        contentResolver.insert(uri, values);
    }
    
    /**
     * 删除数据
     */
    public void testDelete() throws Exception{
        Uri uri = Uri.parse("content://com.peter.providers.personprovider/person/1");
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.delete(uri, null, null);
    }
    
    /**
     * 更新数据
     */
    public void testUpdate() throws Exception{
        Uri uri = Uri.parse("content://com.peter.providers.personprovider/person/10");
        ContentResolver contentResolver = getContext().getContentResolver();
        
        ContentValues values = new ContentValues();
        values.put("name", "luckypeter");
        
        contentResolver.update(uri, values, null, null);
    }
    
    /**
     * 查询数据
     */
    public void testQuery() throws Exception{
        Uri uri = Uri.parse("content://com.peter.providers.personprovider/person");
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, "id asc");
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            System.out.println("name = "+ name);
        }
    }
}
