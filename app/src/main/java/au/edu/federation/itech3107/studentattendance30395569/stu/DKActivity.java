package au.edu.federation.itech3107.studentattendance30395569.stu;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.util.bc;
import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.co.CheckAdapter;
import au.edu.federation.itech3107.studentattendance30395569.databinding.ActivityCheckBinding;
import au.edu.federation.itech3107.studentattendance30395569.sjk.ClassBean;
import au.edu.federation.itech3107.studentattendance30395569.sjk.CourseV2;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserDataBase;
import au.edu.federation.itech3107.studentattendance30395569.util.StringUtil;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DKActivity extends AppCompatActivity {
    private ActivityCheckBinding root;
    private CheckAdapter ad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityCheckBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        initView();
    }

    private void initView() {
        root.rv.setLayoutManager(new LinearLayoutManager(this));
        root.rv.setNestedScrollingEnabled(false);

        ad = new CheckAdapter(R.layout.item_check, new ArrayList<>(),  DKActivity.this);
        root.rv.setAdapter(ad);

        CourseV2 bean = (CourseV2) getIntent().getSerializableExtra("bean");
        if (bean != null) {
            UserDataBase.getInstance(this).getCourseDao().getCourseById(bean.getCouId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MaybeObserver<CourseV2>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(CourseV2 list) {
                            //Query result
                            if (!StringUtil.isEmpty(list.getJoinClassId())) {
                                bc.select_class = list.getJoinClassId();
                            }
                            if (!StringUtil.isEmpty(list.getCheckInStudentIds())) {
                                bc.select_student = list.getCheckInStudentIds();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        }

        root.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bean.setJoinClassId(bc.select_class);
                bean.setCheckInStudentIds(bc.select_student);
                UserDataBase.getInstance(DKActivity.this).getCourseDao().update(bean);
                Toast.makeText(DKActivity.this, "Success", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        UserDataBase.getInstance(this).getClassDao().getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<ClassBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<ClassBean> list) {
                        //Query result
                        if (list.size() != 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (bc.select_class.contains(list.get(i).getId() + ",")) {
                                    ad.addData(list.get(i));
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
