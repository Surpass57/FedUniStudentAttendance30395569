package au.edu.federation.itech3107.studentattendance30395569.co;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.stu.SYFragment;
import au.edu.federation.itech3107.studentattendance30395569.stu.BJFragment;
import au.edu.federation.itech3107.studentattendance30395569.util.TabViewPagerAdapter;

public class SYActivity extends AppCompatActivity {


    private List<AppCompatDialogFragment> f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        f = new ArrayList<>();
        f.add(new SYFragment());
        f.add(new BJFragment());
        ViewPager vp = findViewById(R.id.vp);
        TabLayout tab = findViewById(R.id.tab);
        String[] mTitle = new String[]{getResources().getString(R.string.course), getResources().getString(R.string.student)};
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager(), f, mTitle);
        vp.setAdapter(adapter);
        tab.setupWithViewPager(vp);
        tab.getTabAt(0).setText(mTitle[0]).setIcon(R.mipmap.ic_tab_co);
        tab.getTabAt(1).setText(mTitle[1]).setIcon(R.mipmap.ic_tab_class);

        int type = getIntent().getIntExtra("type", 0);
        vp.setCurrentItem(type);
    }
}