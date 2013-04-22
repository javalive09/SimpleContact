
package com.peter.simplecontact.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.peter.simplecontact.SimpleContactApplication;
import com.peter.simplecontact.access.db.DBOpenHelper;
import com.peter.simplecontact.access.db.PersonService;
import com.peter.simplecontact.domain.Person;

import java.util.List;

/**
 * 单元测试类
 * 
 * @author Peter
 */
public class PersonServiceTest extends AndroidTestCase {
    private static final String TAG = "PersonServiceTest";

    /**
     * 每一个方法执行前都会执行这个方法。可以将初始化的操作，通用操作放到这里
     */
    @Override
    protected void setUp() throws Exception {

    }

    /**
     * 测试DB的建立
     */
    public void testCreateDB() throws Exception {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext());
        dbOpenHelper.getWritableDatabase();
    }

    /**
     * 测试DB公用接口
     */
    public void testGetInstance() throws Exception {
        SimpleContactApplication.getDBController().getCount();
    }

    /**
     * 最好生成抛出 Exception，由框架捕获，然后通知结果。
     * 
     * @throws Exception
     */
    public void testSave() throws Exception {
        PersonService personService = new PersonService(getContext());
        for (int i = 1; i < 11; i++) {
            personService.save(new Person("张" + i, "000-" + i));
        }

        for (int i = 1; i < 11; i++) {
            personService.save(new Person("王" + i, "000-" + i));
        }
    }

    /**
     * 测试删除
     */
    public void testDelete() throws Exception {
        PersonService personService = new PersonService(getContext());

        personService.delete(78);

    }

    /**
     * 测试更新
     */
    public void testUpdate() throws Exception {
        PersonService personService = new PersonService(getContext());
        personService.update(new Person(1, "peter", "1212"));
    }

    /**
     * 测试单个查询
     */
    public void testFind() throws Exception {
        PersonService personService = new PersonService(getContext());
        Person person = personService.find(1);
        Log.i(TAG, person.toString());
    }

    /**
     * 测试获取总数
     */
    public void testCountTotal() throws Exception {
        PersonService personService = new PersonService(getContext());
        long count = personService.getCount();
        Log.i(TAG, count + "");
    }

    /**
     * 测试分页
     */
    public void testScrollData() throws Exception {
        PersonService personService = new PersonService(getContext());
        List<Person> persons = personService.getScrollData(0, (int) personService.getCount());
        for (Person person : persons) {
            Log.i(TAG, person.toString());
        }
    }

    /**
     * 测试查询所有
     */
    public void testFindAll() throws Exception {
        PersonService personService = new PersonService(getContext());
        List<Person> list = personService.findAll();
        for (Person person : list) {
            Log.i(TAG, person.toString());
        }
    }

    /**
     * 测试replace
     */
    public void testReplace() throws Exception {
        PersonService personService = new PersonService(getContext());
        personService.replace(new Person(81, "王大力", "1234567890122"));
    }
    
    /**
     * 暴力增加数据
     */
    public void testRandomSave(){
        PersonService personService = new PersonService(getContext());
        
        for(int i = 0 ; i < 100; i++){
            StringBuffer name = new StringBuffer();
            for(int j = 0; j < 5; j++){
                char word =  (char)(Math.random()*26+'a');
                name.append(word);
            }
            personService.save(new Person(name.toString(), "123456789"));
        }
        
    }

}
