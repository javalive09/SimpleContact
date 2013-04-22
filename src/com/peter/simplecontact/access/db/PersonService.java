
package com.peter.simplecontact.access.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.peter.simplecontact.domain.Person;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于DBOpenHelper的db控制类，简化了对数据库的操作。 使用sql语句进行db操作
 * 
 * @author Peter
 */
public class PersonService {
    private DBOpenHelper dbOpenHelper;

    public PersonService(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    /**
     * 添加记录
     * 
     * @param person
     */
    public void save(Person person) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("insert into person(name, phone) values (?,?)", new Object[] {
                person.getName(), person.getPhone()
        });
    }

    /**
     * 删除记录
     * 
     * @param id 记录ID
     */
    public void delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from person where id=?", new Object[] {
                id
        });
    }

    /**
     * 以主键为基准。如果不存在，则执行insert。如果存在并且数据有变，则执行update操作 sqlite内部按此调用insert和update操作
     * 
     * @param person
     */
    public void replace(Person person) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("replace into person (id, name, phone) values (?,?,?)", new Object[] {
                person.getId(), person.getName(), person.getPhone()
        });
    }

    /**
     * 更新记录
     * 
     * @param person
     */
    public void update(Person person) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update person set name=?,phone=? where id=?", new Object[] {
                person.getName(), person.getPhone(), person.getId()
        });
    }

    /**
     * 查询记录
     * 
     * @param id 记录ID
     * @return
     */
    public Person find(Integer id) {
        Person person = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from person where id=?", new String[] {
                id.toString()
        });
        if (cursor.moveToFirst()) {//cursor永远不会为null，查询时会new出对象。
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            person = new Person(id, name, phone);
        }
        cursor.close();
        return person;
    }

    /**
     * 获取全部记录
     * 
     * @return
     */
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<Person>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from person order by name asc ", null);
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
     * 分页获取记录
     * 
     * @param offset 跳过前面多少条记录
     * @param maxResult 每页获取多少条记录
     * @return
     */
    public List<Person> getScrollData(int offset, int maxResult) {
        List<Person> persons = new ArrayList<Person>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from person order by name asc limit ?,?",
                new String[] {
                        String.valueOf(offset), String.valueOf(maxResult)
                });
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
     * 分页获取记录
     * 
     * @param offset 跳过前面多少条记录
     * @param maxResult 每页获取多少条记录
     * @return
     */
    public Cursor getCursorScrollData(int offset, int maxResult) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from person order by name asc limit ?,?",
                new String[] {
                        String.valueOf(offset), String.valueOf(maxResult)
                });
        return cursor;
    }

    /**
     * 获取记录总数
     * 
     * @return
     */
    public long getCount() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from person ", null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        return result;
    }

}
