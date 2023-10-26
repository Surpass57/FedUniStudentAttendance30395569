package au.edu.federation.itech3107.studentattendance30395569.stu;

import static au.edu.federation.itech3107.studentattendance30395569.util.ScreenUtils.dp2px;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.util.AppUtils;
import au.edu.federation.itech3107.studentattendance30395569.util.bc;
import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.co.TJKCActivity;
import au.edu.federation.itech3107.studentattendance30395569.user.SelectWeekAdapter;
import au.edu.federation.itech3107.studentattendance30395569.sjk.CourseV2;
import au.edu.federation.itech3107.studentattendance30395569.sjk.UserDataBase;
import au.edu.federation.itech3107.studentattendance30395569.util.DialogHelper;
import au.edu.federation.itech3107.studentattendance30395569.util.DialogListener;
import au.edu.federation.itech3107.studentattendance30395569.util.Preferences;
import au.edu.federation.itech3107.studentattendance30395569.util.ScreenUtils;
import au.edu.federation.itech3107.studentattendance30395569.util.TimeUtils;
import au.edu.federation.itech3107.studentattendance30395569.util.Utils;
import au.edu.federation.itech3107.studentattendance30395569.view.CourseView;
import au.edu.federation.itech3107.studentattendance30395569.view.ShowDetailDialog;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class KCActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView week;
    private int count;
    private String month;
    private ShowDetailDialog tk;
    private CourseView ckView;
    private LinearLayout gp;
    private LinearLayout nodeGp;
    private int wS = 12;
    private int nS = 11;
    private int Nw = 28;
    private TextView mTv;
    private RecyclerView weekRv;
    private int weekHight;
    private boolean showD = false;
    private LinearLayout mLayoutCourse;
    private int mIntentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        gp = findViewById(R.id.layout_week_group);
        nodeGp = findViewById(R.id.layout_node_group);
        mLayoutCourse = findViewById(R.id.layout_course);
        mIntentId = getIntent().getIntExtra("id", 0);
        month = getIntent().getStringExtra("date");
        ScreenUtils.init(this);
        Preferences.init(this);
        initToolbar();
        initWeek();
        initCourseView();
        initWeekNodeGroup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void initWeek() {
        initWeekTitle();
        initSelectWeek();
    }

    private void initSelectWeek() {
        weekRv = findViewById(R.id.recycler_view_select_week);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weekRv.getLayoutParams();
        params.topMargin = -dp2px(45);
        weekRv.setLayoutParams(params);

        weekRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.HORIZONTAL, false));
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            strings.add( i + "week");
        }
        SelectWeekAdapter selectWeekAdapter = new SelectWeekAdapter(R.layout.adapter_select_week, strings);
        weekRv.setAdapter(selectWeekAdapter);
        weekRv.scrollToPosition(AppUtils.getCurrentWeek(this)-1);

        weekRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                weekHight = bottom - top;
            }
        });

        selectWeekAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                count = position + 1;

                AppUtils.PreferencesCurrentWeek(KCActivity.this, count);
                ckView.setCurrentIndex(count);
                ckView.resetView();
                week.setText(count + "week");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animSelectWeek(false);
                        AppUtils.updateWidget(getApplicationContext());
                    }
                }, 150);
            }
        });
    }


    private void animSelectWeek(boolean show) {
        showD = show;

        int start = 0, end = 0;
        if (show) {
            start = -weekHight;
        } else {
            end = -weekHight;
        }

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weekRv.getLayoutParams();
                params.topMargin = (int) animation.getAnimatedValue();
                weekRv.setLayoutParams(params);
            }
        });
        animator.start();
    }


    private void initWeekTitle() {
        week = findViewById(R.id.tv_toolbar_subtitle);
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animSelectWeek(!showD);
            }
        });
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText(getString(R.string.class_schedule));
    }

    private void initWeekNodeGroup() {
        nodeGp.removeAllViews();
        gp.removeAllViews();

        for (int i = -1; i < 7; i++) {
            TextView textView = new TextView(getApplicationContext());
            textView.setGravity(Gravity.CENTER);

            textView.setWidth(0);
            textView.setTextColor(getResources().getColor(R.color.primary_text));
            LinearLayout.LayoutParams params;

            if (i == -1) {
                params = new LinearLayout.LayoutParams(
                        Utils.dip2px(getApplicationContext(), Nw),
                        ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setTextSize(nS);
                textView.setText(month + "\n月");

                mTv = textView;
            } else {
                params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                textView.setTextSize(wS);
                textView.setText(bc.WEEK_SINGLE[i]);
            }

            gp.addView(textView, params);
        }

        int nodeItemHeight = Utils.dip2px(getApplicationContext(), 55);
        for (int i = 1; i <= 16; i++) {
            TextView textView = new TextView(getApplicationContext());
            textView.setTextSize(nS);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.GRAY);
            textView.setText(String.valueOf(i));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, nodeItemHeight);
            nodeGp.addView(textView, params);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initCourseView() {
        ckView = findViewById(R.id.course_view_v2);
        ckView.setCourseItemRadius(3)
                .setTextTBMargin(dp2px(1), dp2px(1));

        ckView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("touch");
                return false;
            }
        });
        initCourseViewEvent();
    }

    /**
     * courseVIew event
     */
    private void initCourseViewEvent() {
        ckView.setOnItemClickListener(new CourseView.OnItemClickListener() {
            @Override
            public void onClick(List<CourseAncestor> course, View itemView) {
                tk = new ShowDetailDialog();
                tk.show(KCActivity.this, (CourseV2) course.get(0), new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        tk = null;
                    }
                });
            }

            @Override
            public void onLongClick(List<CourseAncestor> courses, View itemView) {
                final CourseV2 course = (CourseV2) courses.get(0);
                DialogHelper dialogHelper = new DialogHelper();
                dialogHelper.showNormalDialog(KCActivity.this, getString(R.string.confirm_to_delete),
                        "Class 【" + course.getCouName() + "】" + bc.WEEK[course.getCouWeek()]
                                + "" + course.getCouStartNode() + " ", new DialogListener() {
                            @Override
                            public void onPositive(DialogInterface dialog, int which) {
                                super.onPositive(dialog, which);
                                deleteCancelSnackBar(course);
                            }
                        });
            }

            public void onAdd(CourseAncestor course, View addView) {
                Intent intent = new Intent(KCActivity.this, TJKCActivity.class);
                intent.putExtra(bc.INTENT_ADD_COURSE_ANCESTOR, course);
                intent.putExtra(bc.INTENT_ADD, true);
                startActivity(intent);
            }

        });
    }

    /**
     * Undo delete prompt
     */
    private void deleteCancelSnackBar(final CourseV2 course) {
        course.setDisplayable(false);
        ckView.resetView();
        Snackbar.make(mTv, "Delete Success！☆\\(￣▽￣)/", Snackbar.LENGTH_LONG).setAction("撤销",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                    case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                    case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        //delete
                        UserDataBase.getInstance(KCActivity.this).getCourseDao().delete(course);
                        ckView.resetView();
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        //cancel
                        course.setDisplayable(true);
                        ckView.resetView();
                        break;
                }
            }
        }).show();
    }

    private void updateView() {
        updateCoursePreference();
    }

    @SuppressLint("SetTextI18n")
    public void updateCoursePreference() {
        updateCurrentWeek();
        mTv.setText(month + "\nmonth");

        //get id
//        long currentCsNameId = Preferences.getLong(
//                getString(R.string.app_preference_current_cs_name_id), 0L);

//        mPresenter.updateCourseViewData(currentCsNameId);
        //Re-query data
        UserDataBase.getInstance(KCActivity.this).getCourseDao().getAllUsers(mIntentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<CourseV2>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<CourseV2> list) {
                        //Query result
                        if (list.size() != 0) {
                            ckView.clear();
                            for (CourseV2 course : list) {
                                if (course.getCouColor() == null || course.getCouColor() == -1) {
                                    course.setCouColor(Utils.getRandomColor());
                                }
                                course.init();
                                ckView.addCourse(course);
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

    @SuppressLint("SetTextI18n")
    private void updateCurrentWeek() {
        count = AppUtils.getCurrentWeek(this);
        week.setText( count + "week");
        ckView.setCurrentIndex(count);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (tk != null) {
                    tk.dismiss();
                    Log.e("hao", "KCActivity onKeyDown()");
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (tk != null) tk.dismiss();
        return super.onTouchEvent(event);
    }


}
