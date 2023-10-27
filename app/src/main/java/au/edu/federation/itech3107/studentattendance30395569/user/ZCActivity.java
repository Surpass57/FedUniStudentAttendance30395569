package au.edu.federation.itech3107.studentattendance30395569.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.databinding.ActivityRegisterBinding;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserBean;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserDao;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserDataBase;
import au.edu.federation.itech3107.studentattendance30395569.util.StringUtil;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ZCActivity extends AppCompatActivity {

    private int au = 1;
    private ActivityRegisterBinding rooy;
    private UserDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rooy = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(rooy.getRoot());
        dao = UserDataBase.getInstance(this).getUserDao();
        initView();
    }

    private void initView() {
        //register
        rooy.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verify that the input box is empty
                if (StringUtil.isEmpty(rooy.etNick.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.nick) + getString(R.string.notNull), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isEmpty(rooy.etAccount.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.account) + getString(R.string.notNull), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isEmpty(rooy.etPassword.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.password) + getString(R.string.notNull), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isEmpty(rooy.etPassword2.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.password) + getString(R.string.notNull), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!rooy.etPassword.getText().toString().equals(rooy.etPassword2.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.password_not_again), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isEmpty(rooy.etMobile.getText().toString())) {
                    Toast.makeText(ZCActivity.this, getString(R.string.mobile) + getString(R.string.notNull), Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check whether the account is the same
                dao.getUserByName(rooy.etAccount.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MaybeObserver<List<UserBean>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<UserBean> list) {
                                //Query result
                                if (list != null && list.size() != 0) {
                                    //If the account is repeated, prompt is displayed
                                    Toast.makeText(ZCActivity.this, getString(R.string.accountNotice), Toast.LENGTH_SHORT).show();
                                } else {
                                    //save data
                                    insertData();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("hao", "TaskDetailActivity onError(): " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                //No result found
                                insertData();
                            }
                        });
            }
        });



    }

    private void insertData() {
        //Database insert data
        UserBean userBean = new UserBean();
        userBean.setName(rooy.etAccount.getText().toString());
        userBean.setPwd(rooy.etPassword.getText().toString());
        userBean.setNick(rooy.etNick.getText().toString());
        userBean.setMobile(rooy.etMobile.getText().toString());
        userBean.setAuthon(au);
        dao.insert(userBean);
        Toast.makeText(ZCActivity.this, getString(R.string.registerNotice), Toast.LENGTH_SHORT).show();
        finish();
    }

}