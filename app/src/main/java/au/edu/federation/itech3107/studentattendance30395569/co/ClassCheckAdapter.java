package au.edu.federation.itech3107.studentattendance30395569.co;


import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import au.edu.federation.itech3107.studentattendance30395569.util.bc;
import au.edu.federation.itech3107.studentattendance30395569.R;
import au.edu.federation.itech3107.studentattendance30395569.sjk.ClassBean;
import au.edu.federation.itech3107.studentattendance30395569.util.StringUtil;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  神兽保佑
 * 　　　　┃　　　┃  代码无bug
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 * @author
 * @date 2023/10/19
 * @description
 */

public class ClassCheckAdapter extends BaseQuickAdapter<ClassBean, BaseViewHolder> {

    public ClassCheckAdapter(int layoutResId, @Nullable List<ClassBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ClassBean item) {
        helper.setText(R.id.tv_item,item.getName());
        CheckBox view = helper.getView(R.id.cb);
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!bc.select_class.contains(item.getId()+",")) {
                        bc.select_class = bc.select_class + item.getId() + ",";
                        Log.e("hao", "选中: "+ bc.select_class);
                    }
                }else {
                    if (bc.select_class.contains(item.getId()+",")) {
                        String[] split = bc.select_class.split(",");
                        String newDes = "";
                        for (String i : split){
                            if (!StringUtil.isEmpty(i)){
                                int i1 = Integer.parseInt(i);
                                if (item.getId() != i1){
                                    newDes = newDes + i1+",";
                                }
                            }
                        }
                        bc.select_class = newDes;
                        Log.e("hao", "不选中: "+ bc.select_class);
                    }
                }
            }
        });
        view.setChecked(false);
        String selectClass = bc.select_class;
        if (!StringUtil.isEmpty(selectClass)) {
            String[] split = selectClass.split(",");
            for (String s : split) {
                if (!StringUtil.isEmpty(s)) {
                    if ((s+",").equals(item.getId()+",")) {
                        view.setChecked(true);
                    }
                }
            }
        }
    }
}