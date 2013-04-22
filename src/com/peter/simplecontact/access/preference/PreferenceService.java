
package com.peter.simplecontact.access.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

/**
 * xml形式的配置文件的服务类
 * 
 * @author Peter
 */
public class PreferenceService {
    private Context context;

    public PreferenceService(Context context) {
        this.context = context;
    }

    /**
     * 保存参数 Context.MODE_PRIVATE： 私有属性
     * Context.MODE_APPEND = Context.MODE_PRIVATE(私有属性) + append 
     * Context.MODE_WORLD_READABLE：其他应用可读属性
     * Context.MODE_WORLD_WRITEABLE：其他应用可写属性
     * 
     * @param name 姓名
     * @param age 年龄
     */
    public void save(String name, int age) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("person",// 名字不能包含后缀，框架默认用xml
                Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putInt("age", age);
        editor.commit();// 提交才能生成XML
    }

    /**
     * 获取配置参数
     * 
     * @return
     */
    public Map<String, String> getPreferences() {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("person",
                Context.MODE_PRIVATE);
        params.put("name", sharedPreferences.getString("name", ""));
        params.put("age", sharedPreferences.getInt("age", 0) + "");
        return params;
    }
}
