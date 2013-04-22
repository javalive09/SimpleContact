
package com.peter.simplecontact.access.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.peter.simplecontact.domain.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于DBOpenHelper的db控制类，简化了对数据库的操作。
 * 使用包装类contentValues进行数据库操作.contentValues是一个map。其中key对应db数据库的列名。
 * 
 * @author Peter
 */
public class OtherPersonService {
    private DBOpenHelper dbOpenHelper;

    public OtherPersonService(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    /**
     * 添加记录
     * 
     * @param person
     */
    public void save(Person person) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", person.getName());
        values.put("phone", person.getPhone());
        db.insert("person", null, values);// 如果values为null，则向数据库中（插入第二个参数名字的列的值为字符串
                                          // NULL），因为数据库不允许插入空
    }

    /**
     * 删除记录,以id为基准
     * 
     * @param id 记录ID
     */
    public void delete(Integer id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("person", "id=?", new String[] {
                id.toString()
        });
    }

    /**
     * 更新记录，以id为基准
     * 
     * @param person
     */
    public void update(Person person) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", person.getName());
        values.put("phone", person.getPhone());
        db.update("person", values, "personid=?", new String[] {
                person.getId() + ""
        });
    }

    /**
     * 查询记录，以id为基准
     * 
     * @param id 记录ID
     * @return
     */
    public Person find(Integer id) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("person", null, "personid=?", new String[] {
                id.toString()
        }, null, null, null);

        if (cursor.moveToFirst()) {//cursor不会为null，所以不用判断。底层总会返回一个cursor。
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            return new Person(id, name, phone);
        }
        cursor.close();
        return null;
    }

    /**
     * 分页获取记录
     * 
     * @param offset 跳过前面多少条记录
     * @param maxResult 每页获取多少条记录
     * @return
     */
    public List<Person> getScrollData(int offset, int maxResult) {
        List<Person> persons = new ArrayList<Person>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("person", null, null, null, null, null, "name asc", offset
                + "," + maxResult);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            persons.add(new Person(id, name, phone));
        }
        cursor.close();
        return persons;
    }

    /**
     * 获取记录总数
     * 
     * @return
     */
    public long getCount() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("person", new String[] {
                "count(*)"
        }, null, null, null, null, null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        return result;
    }

}
