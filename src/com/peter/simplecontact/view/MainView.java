
package com.peter.simplecontact.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.peter.simplecontact.R;
import com.peter.simplecontact.SimpleContactApplication;
import com.peter.simplecontact.access.db.PersonService;
import com.peter.simplecontact.domain.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 主显示界面 ：信息列表
 * 
 * @author Peter
 */
public class MainView extends Activity implements OnClickListener,
        OnLongClickListener, ListView.OnScrollListener , OnTouchListener{

    private static final String TAG = MainView.class.getSimpleName();
    private static final int ITEMVIEW = 1;
    private static final int NEEDREFRESH = 1;
    private ListView mInformationList;
    private LinearLayout mFirstWordOverLayView;
    private TextView mFirstWordTextView;
    private WindowManager mWindowManager;
    private PersonAdapter mAdapter;
    private Button mAdd , mEdit;
    private Animation mSlideRightOut;
    private Animation mSlideLeftIn;
    private boolean mShowAnima = false;
    private boolean mShowIcion = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.out.println("Main thread="+Thread.currentThread().getId());
        mFirstWordOverLayView = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.wordhintview,
                null);
        mFirstWordTextView = (TextView) mFirstWordOverLayView.findViewById(R.id.hint);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFirstWordOverLayView, lp);

        mInformationList = (ListView) findViewById(R.id.list);
        List<Person> persons = SimpleContactApplication.getDBController().findAll();
        mAdd = (Button) findViewById(R.id.add);
        mEdit = (Button) findViewById(R.id.edit);//增加动画效果
        mAdd.setOnClickListener(this);
        mEdit.setOnClickListener(this);

        mAdapter = new PersonAdapter((Person[]) persons.toArray(new Person[persons
                .size()]), R.layout.listviewitem);
        mInformationList.setAdapter(mAdapter);
        mInformationList.setOnScrollListener(this);
        mInformationList.setOnTouchListener(this);
        
        //增加动画
        mSlideRightOut = AnimationUtils.loadAnimation(MainView.this, R.anim.push_right_out);
        mSlideLeftIn = AnimationUtils.loadAnimation(MainView.this, R.anim.push_left_in);
    }

    /**
     * 用反射回调FrameWork方法获取 FastScroller 的state值
     * 
     * @param list
     * @return
     */
    private Object invokeFrameWorkPrivateMethod(Object list) {

        Object result = null;// 获得返回值
        try {
            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);// 压制java访问控制检查
            Class<?> clazzClass = f.getType();// 获取FastScroller Class
            Object mFastScroller = f.get(list);// 获取mFastScroller 实例对象
            Method method = clazzClass.getDeclaredMethod("getState", new Class[] {});// 调用无参函数
            result = method.invoke(mFastScroller, new Object[] {});// 获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** 更改指定ListView的快速滚动条图标。 */
    // private void changeFastScrollerDrawable(ListView list) {
    // try {
    // Field f = AbsListView.class.getDeclaredField("mFastScroller");
    // f.setAccessible(true);
    // Object obj = f.get(list);
    // f = f.getType().getDeclaredField("mThumbDrawable");
    // f.setAccessible(true);
    // Drawable drawable = (Drawable) f.get(obj);
    // drawable = getResources().getDrawable(R.drawable.ic_launcher);
    // f.set(obj, drawable);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }

    /**
     * 判断是否需要显示OverLay 首字母提示
     * 
     * @param list
     * @return
     */
    private boolean needShowOverLay(Object list) {
    	Object result = invokeFrameWorkPrivateMethod(list);
    	if(result != null) {
	        int i = (Integer)result;
	        if (i == 3) {// STATE_DRAGGING = 3;
	            return true;
	        }
    	}
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);

        if (needShowOverLay(mInformationList)) {
            mFirstWordOverLayView.setVisibility(View.VISIBLE);
        } else {
            mFirstWordOverLayView.setVisibility(View.INVISIBLE);
        }

        return b;
    }

    @Override
    public boolean onLongClick(final View v) {
        v.setBackgroundColor(Color.BLUE);// 设置选中条目背景色
        final Person person = (Person)v.getTag();

        Log.i(TAG, "1 id=" + Thread.currentThread().getId());

        final AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                .setTitle(person.getName() + " ( id = " + person.getId() + " ) ")
                .setItems(
                        new CharSequence[] {
                                MainView.this.getString(R.string.delete),
                                MainView.this.getString(R.string.modify)
                        }, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface idialog, int which) {
                                switch (which) {
                                    case 0:// 删除操作
                                        deletePerson(person);
                                        break;

                                    case 1:// 修改操作
                                        Intent intent = new Intent(MainView.this, ItemView.class);
                                        intent.putExtra("person", person);
                                        startActivityForResult(intent, ITEMVIEW);
                                        break;
                                    default:
                                        break;
                                }

                            }

                        }).create();
        dialog.show();
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                v.setBackgroundResource(R.drawable.item_bg);// 恢复背景色，绑定的是对话框
            }
        });
        return true;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.add) {
            Intent intent = new Intent(MainView.this, ItemView.class);
            startActivityForResult(intent, ITEMVIEW);
            return;
        }else if(v.getId() == R.id.edit){
            mShowAnima = true;//滚动listview时屏蔽动画效果，按键后才有动画效果
            mShowIcion = !mShowIcion;
            mAdapter.notifyDataSetChanged();
            return;
        }

        v.setBackgroundColor(Color.BLUE);// 设置选中条目背景色
        final Person person = (Person) v.getTag();

        final AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                .setTitle(person.getName() + " ( " + person.getPhone() + " ) ")
                .setItems(
                        new CharSequence[] {
                                MainView.this.getString(R.string.call),
                                MainView.this.getString(R.string.sms)
                        }, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface idialog, int which) {
                                switch (which) {
                                    case 0:// 拨打电话操作
                                        call(person);
                                        break;

                                    case 1:// 发送短信操作

                                        break;
                                    default:
                                        break;
                                }

                            }

                        }).create();
        dialog.show();
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (v != null) {
                    v.setBackgroundResource(R.drawable.item_bg);// 恢复背景色
                }
            }
        });
    }

    /**
     * 打电话的功能
     * 
     * @param person
     */
    private void call(Person person) {
        String phoneNum = person.getPhone();
        Uri uri = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        // Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mShowAnima = false;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (mFirstWordOverLayView.getVisibility() == View.VISIBLE) {
            int center = firstVisibleItem + visibleItemCount / 2;
            Person currentPerson = (Person) mAdapter.getItem(center);
            String currentWord = currentPerson.getName().charAt(0) + "";

            mFirstWordTextView.setText(currentWord);
        }
    }

    private void deletePerson(final Person person) {
        final ProgressDialog progressDialog = ProgressDialog.show(MainView.this,
                getString(R.string.dialog_title),
                getString(R.string.dialog_message), true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleContactApplication.getDBController().delete(person.getId());

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainView.this,
                                person.getName() + getString(R.string.delete_success),
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        mAdapter.updateData();
                    }
                });

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ITEMVIEW:
                if (resultCode == NEEDREFRESH) {
                    mAdapter.updateData();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        getWindowManager().removeView(mFirstWordOverLayView);
        super.onDestroy();
    }

    /**
     * 绑定 SQLITE中的数据 和 ListView 中的item 提供update数据功能
     * 
     * @author Peter
     */
    private class PersonAdapter extends BaseAdapter {
        private Person[] persons;// 用数组可以显著提高绘图效率
        private int resource;
        private LayoutInflater inflater;

        public PersonAdapter(Person[] persons, int resource) {
            this.persons = persons;
            this.resource = resource;
            // inflater = (LayoutInflater) //用系统服务的方法获取
            // context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater = LayoutInflater.from(MainView.this);
        }

        @Override
        public int getCount() {
            return persons.length;
        }

        @Override
        public Object getItem(int position) {
            return persons != null ? persons[position] : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        	Log.i(TAG, "getView 被调用 ！！！");
        	
            // 获取数据
            Person person = (Person) getItem(position);

            // 获取view
            ViewCache cache = null;
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);

                LinearLayout item = (LinearLayout) convertView.findViewById(R.id.item);
                TextView firstword = (TextView) convertView.findViewById(R.id.firstword);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView phone = (TextView) convertView.findViewById(R.id.phone);
                ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

                cache = new ViewCache();
                cache.firtword = firstword;
                cache.name = name;
                cache.phone = phone;
                cache.item = item;
                cache.icon = icon;

                convertView.setTag(cache);
                
                System.out.println(position +"-----new view:"+convertView+"visi:"+(icon.getVisibility()==View.VISIBLE));
            } else {
                cache = (ViewCache) convertView.getTag();
                System.out.println(position +"-----old view:"+convertView+"visi:"+(cache.icon.getVisibility()==View.VISIBLE));
            }

            // 绑定数据
            cache.name.setText(person.getName());
            cache.phone.setText(person.getPhone());
            cache.item.setTag(person);

            // 过滤首字母
            char currentWord = person.getName().charAt(0);
            char previousWord = (position - 1) >= 0 ? ((Person) getItem(position - 1)).getName()
                    .charAt(0) : ' ';
            if (currentWord != previousWord) {
                cache.firtword.setVisibility(View.VISIBLE);
                cache.firtword.setText(currentWord + "");
                previousWord = currentWord;
            } else {
                cache.firtword.setVisibility(View.GONE);
            }

            // 绑定事件
            cache.firtword.setOnClickListener(null);
            cache.item.setOnClickListener(MainView.this);
            cache.item.setOnLongClickListener(MainView.this);

            //防止有的item（最下面的露出一点点的item）并没有new出来，导致显示不正确
            if(mShowIcion){
                cache.icon.setVisibility(View.VISIBLE);
                if(mShowAnima){//显示动画效果
                    cache.icon.startAnimation(mSlideLeftIn); 
                }
            }else{
                cache.icon.setVisibility(View.INVISIBLE);
                if(mShowAnima){//显示动画效果
                    cache.icon.startAnimation(mSlideRightOut);
                }
            }
            
            return convertView;
        }

        public void updateData() {
            PersonService service = SimpleContactApplication.getDBController();
            int newCount = (int) service.getCount();
            persons = service.getScrollData(0, newCount).toArray(new Person[newCount]);
            notifyDataSetChanged();
        }
    }
    

	/**
     * 每一项view 的缓冲
     * 
     * @author Peter
     */
    private class ViewCache {
        TextView firtword;
        TextView name;
        TextView phone;
        ImageView icon;
        LinearLayout item;
    }


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

    // 以下是用CursorAdapter作为适配器的代码

    // private OnLongClickListener itemLongTouchListener = new
    // OnLongClickListener() {
    //
    // @Override
    // public boolean onLongClick(View v) {
    // ViewHolder holder = (ViewHolder) v.getTag();
    // int position = holder.position;
    // System.out.println("long click position="+position);
    // return true;
    // }
    // };
    //
    // private OnClickListener itemShortTouchListener = new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // ViewHolder holder = (ViewHolder) v.getTag();
    // int position = holder.position;
    // System.out.println("short click position="+position);
    // }
    // };
    //
    // private class informationAdapter extends CursorAdapter{
    //
    // private LayoutInflater layoutInflater;
    // private char lastWord;
    //
    // public informationAdapter(Context context, Cursor c) {
    // super(context, c);
    // layoutInflater = LayoutInflater.from(context);
    // }
    //
    // @Override
    // public View newView(Context context, Cursor cursor, ViewGroup parent) {
    // return null;
    // }
    //
    // @Override
    // public void bindView(View view, Context context, Cursor cursor) {
    //
    // }
    //
    // public View getView(int position, View convertView, ViewGroup parent){
    //
    // ViewHolder holder = null;
    //
    // if( convertView == null){
    // holder = new ViewHolder();
    // convertView = layoutInflater.inflate(R.layout.listviewitem, null);
    // LinearLayout item = (LinearLayout) convertView.findViewById(R.id.item);
    //
    // holder.firstWord = (TextView) convertView.findViewById(R.id.firstword);
    // holder.name = (TextView) convertView.findViewById(R.id.name);
    // holder.phone = (TextView) convertView.findViewById(R.id.phone);
    // holder.item = item;
    // holder.item.setTag(holder);
    //
    // convertView.setTag(holder);
    // }else{
    // holder = (ViewHolder) convertView.getTag();
    // }
    //
    // Cursor mCursor = (Cursor) getItem(position);
    //
    // String name = mCursor.getString(mCursor.getColumnIndex("name"));
    // String phone = mCursor.getString(mCursor.getColumnIndex("phone"));
    //
    // char currentWord = name.charAt(0);
    // if(currentWord != lastWord){
    // lastWord = currentWord;
    // holder.firstWord.setText(currentWord+"");
    // }else{
    // holder.firstWord.setVisibility(View.GONE);
    // }
    //
    // holder.name.setText(name);
    // holder.phone.setText(phone);
    // holder.position = position;
    //
    // holder.item.setOnClickListener(itemShortTouchListener);
    // holder.item.setOnLongClickListener(itemLongTouchListener);
    //
    // return convertView;
    //
    // }
    //
    // public void changeCursor(Cursor c) {
    // super.changeCursor(c);
    // }
    // }
    //
    // private static class ViewHolder{
    // public TextView firstWord;
    // public TextView name;
    // public TextView phone;
    // public LinearLayout item;
    // public int position;
    // }

}
