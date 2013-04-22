
package com.peter.simplecontact.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peter.simplecontact.R;
import com.peter.simplecontact.SimpleContactApplication;
import com.peter.simplecontact.domain.Person;

/**
 * 姣忎竴椤筕iew鐨勫叿浣撲俊鎭〉闈�
 * 
 * @author Peter
 */
public class ItemView extends Activity implements OnClickListener {

    private EditText name, number;
    private Button save, back;
    private static final int NEEDREFRESH = 1;
    private int id = -1;// 鐢ㄤ簬鍒ゆ柇鏄惁鏈塸erson瀵硅薄浼犺繃鏉�

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("item thread="+Thread.currentThread().getId());
        setContentView(R.layout.iteminformation);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);

        save = (Button) findViewById(R.id.save);
        back = (Button) findViewById(R.id.back);

        save.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 鍒濆鍖栨暟鎹細view涓殑鏁版嵁銆乮d鐨勫�
     */
    private void initData() {
        Intent intent = getIntent();
        id = -1;// 鎭㈠id
        if (intent != null) {
            Person person = intent.getParcelableExtra("person");
            if (person != null) {
                name.setText(person.getName());
                number.setText(person.getPhone());
                id = person.getId();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                saveOrUpdate();
                break;

            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 鏂板鎴栨洿鏂版搷浣滐細鍖呮嫭瀵逛繚瀛樺唴瀹逛负绌虹殑杩囨护
     */
    private void saveOrUpdate() {
        final String saveName = name.getText().toString();
        final String saveNumber = number.getText().toString();

        if (saveName.trim().length() <= 0 || saveNumber.trim().length() <= 0) {
            return;
        }

        final ProgressDialog progressDialog = ProgressDialog.show(ItemView.this,
                getString(R.string.dialog_title),
                getString(R.string.dialog_message), true);
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (id == -1) {// 鏂板
                    SimpleContactApplication.getDBController().save(
                            new Person(saveName, saveNumber));
                } else {// 鏇存柊
                    SimpleContactApplication.getDBController().update(
                            new Person(id, saveName, saveNumber));
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (id == -1) {
                            Toast.makeText(ItemView.this,
                                    saveName + getString(R.string.add_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ItemView.this,
                                    saveName + getString(R.string.modify_success),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        setResult(NEEDREFRESH);
                        finish();
                    }
                });
            }
        }).start();

    }

}
