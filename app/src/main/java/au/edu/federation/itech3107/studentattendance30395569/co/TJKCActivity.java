package au.edu.federation.itech3107.studentattendance30395569.co;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.util.AppUtils;
import au.edu.federation.itech3107.studentattendance30395569.util.bc;
import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.stu.CourseAncestor;
import au.edu.federation.itech3107.studentattendance30395569.sjk.ClassBean;
import au.edu.federation.itech3107.studentattendance30395569.sjk.CourseV2;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserDataBase;
import au.edu.federation.itech3107.studentattendance30395569.stu.DKActivity;
import au.edu.federation.itech3107.studentattendance30395569.util.Preferences;
import au.edu.federation.itech3107.studentattendance30395569.util.ScreenUtils;
import au.edu.federation.itech3107.studentattendance30395569.util.StringUtil;
import au.edu.federation.itech3107.studentattendance30395569.view.EditTextLayout;
import au.edu.federation.itech3107.studentattendance30395569.view.PopupWindowDialog;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//Add Course
public class TJKCActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean aN = true;

    private CourseAncestor co;
    private CourseV2 courseD;

    private ImageView addLo;
    private LinearLayout loCon;
    private ImageView sub;
    private EditTextLayout name;
    private EditTextLayout tea;
    private LinearLayout ll_class;
    private RecyclerView rv;
    private Button clickAdd;
    private ClassCheckAdapter mAdap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        handleIntent();
        initView();

        initListener();
    }

    private void initView() {
        name = findViewById(R.id.etl_name);
        tea = findViewById(R.id.etl_teacher);

        addLo = findViewById(R.id.iv_add_location);
        loCon = findViewById(R.id.layout_location_container);
        sub = findViewById(R.id.iv_submit);
        ll_class = findViewById(R.id.ll_class);
        clickAdd = findViewById(R.id.btn_add);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(false);
        mAdap = new ClassCheckAdapter(R.layout.item_check_course, new ArrayList<>());
        rv.setAdapter(mAdap);
        sub.setImageResource(R.drawable.ic_done_black_24dp);
        addLocation(false);

        clickAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, SYActivity.class).putExtra("type", 1));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserDataBase.getInstance(this).getClassDao().getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<ClassBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<ClassBean> list) {
                        // Query the result
                        if (list.size() != 0) {
                            mAdap.setNewData(list);
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

    private void initListener() {
        addLo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addLocation(true);
                startActivity(new Intent(TJKCActivity.this, DKActivity.class)
                        .putExtra("bean", courseD));
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        co = (CourseAncestor) intent.getSerializableExtra(bc.INTENT_ADD_COURSE_ANCESTOR);
        if (co != null) {
            aN = true;
        } else {
            courseD = (CourseV2) intent.getSerializableExtra(bc.INTENT_EDIT_COURSE);
            if (courseD != null) {
                aN = false; //is edit mode
                courseD.init();//Clicking from the desktop must have been initialized from other locations not necessarily
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void submit() {
        //name
        String name = this.name.getText().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(TJKCActivity.this, "Not Null", Toast.LENGTH_SHORT).show();
            return;
        }

        //teacher
        String teacher = tea.getText().trim();
        //group

        long couCgId = Preferences.getLong(getString(R.string.app_preference_current_cs_name_id), 0);
        int childCount = loCon.getChildCount();
        boolean hasLocation = false;
        for (int i = 0; i < childCount; i++) {
            View locationItem = loCon.getChildAt(i);
            Object obj = locationItem.getTag();

            if (obj != null) {
                hasLocation = true;
                CourseV2 courseV2 = (CourseV2) obj;
                courseV2.setCouName(name);
                courseV2.setCouTeacher(teacher);
                courseV2.setGroupId(bc.selectId);

                if (aN || courseV2.getCouId() == 0) {
                    courseV2.setCouCgId(couCgId);
                    courseV2.setJoinClassId(bc.select_class);
                    courseV2.init();
                    //Insert course
                    UserDataBase.getInstance(this).getCourseDao().insert(courseV2);
                } else {
                    courseV2.setJoinClassId(bc.select_class);
                    courseV2.init();
                    //update course
                    UserDataBase.getInstance(this).getCourseDao().update(courseV2);
                }

            }
        }
        if (!hasLocation) {
            Toast.makeText(TJKCActivity.this, "Time Is Null", Toast.LENGTH_SHORT).show();
        }

        if (aN) {
            Toast.makeText(TJKCActivity.this, "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TJKCActivity.this, "Success", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void addLocation(boolean closeable) {
        final LinearLayout locationItem = (LinearLayout) View.inflate(this,
                R.layout.layout_location_item, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ScreenUtils.dp2px(8);

        if (closeable) {
            locationItem.findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loCon.removeView(locationItem);
                }
            });

            initEmptyLocation(locationItem);

        } else {// Create a default class time and place
            locationItem.findViewById(R.id.iv_clear).setVisibility(View.INVISIBLE);

            if (co != null) {
                // Screen click here

                CourseV2 defaultCourse = new CourseV2().setCouOnlyIdR(AppUtils.createUUID())
                        .setCouAllWeekR(bc.DEFAULT_ALL_WEEK)
                        .setCouWeekR(co.getRow())
                        .setCouStartNodeR(co.getCol())
                        .setCouNodeCountR(co.getRowNum())
                        .init();

                initNodeInfo(locationItem, defaultCourse);
            } else if (courseD != null) {

                initNodeInfo(locationItem, courseD);

                name.setText(courseD.getCouName());
                tea.setText(courseD.getCouTeacher());
                bc.select_class = "";
                String joinClassId = courseD.getJoinClassId();
                if (!StringUtil.isEmpty(joinClassId)) {
                    bc.select_class = joinClassId;
                }
            } else {

                initEmptyLocation(locationItem);
                addLo.setVisibility(View.GONE);
            }
        }

        locationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLocationItem(locationItem);
            }
        });

        loCon.addView(locationItem, params);
    }

    private void initEmptyLocation(LinearLayout locationItem) {
        CourseV2 defaultCourse = new CourseV2().setCouOnlyIdR(AppUtils.createUUID())
                .setCouAllWeekR(bc.DEFAULT_ALL_WEEK)
                .setCouAllWeekR(1 + "")
                .setCouStartNodeR(1)
                .setCouNodeCountR(1);
        initNodeInfo(locationItem, defaultCourse);
    }

    private void initNodeInfo(LinearLayout locationItem, CourseV2 courseV2) {
        TextView tvText = locationItem.findViewById(R.id.tv_text);
        String builder = bc.WEEK_SINGLE[courseV2.getCouWeek() - 1] + "Week " +
                " Section" + courseV2.getCouStartNode() + "-" +
                (courseV2.getCouStartNode() + courseV2.getCouNodeCount() - 1);
        tvText.setText(builder);

        locationItem.setTag(courseV2);
    }

    private void clickLocationItem(final LinearLayout locationItem) {
        PopupWindowDialog dialog = new PopupWindowDialog();

        CourseV2 courseV2 = null;
        Object obj = locationItem.getTag();
        // has tag data
        if (obj != null && obj instanceof CourseV2) {
            courseV2 = (CourseV2) obj;
        } else {
            throw new RuntimeException("Course data tag not be found");
        }

        dialog.showSelectTimeDialog(this, courseV2, new PopupWindowDialog.SelectTimeCallback() {
            @Override
            public void onSelected(CourseV2 course) {
                StringBuilder builder = new StringBuilder();
                builder.append("周").append(bc.WEEK_SINGLE[course.getCouWeek() - 1])
                        .append(" 第").append(course.getCouStartNode()).append("-")
                        .append(course.getCouStartNode() + course.getCouNodeCount() - 1).append("节");
                if (!TextUtils.isEmpty(course.getCouLocation())) {
                    builder.append("【").append(course.getCouLocation()).append("】");
                }

                ((TextView) locationItem.findViewById(R.id.tv_text))
                        .setText(builder.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
