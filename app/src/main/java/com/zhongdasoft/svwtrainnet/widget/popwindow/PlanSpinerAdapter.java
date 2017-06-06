package com.zhongdasoft.svwtrainnet.widget.popwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PlanSpinerAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mObjects = new ArrayList<>();
    private LayoutInflater mInflater;
    private final int lineNum = 4;
    private final int colNum = 3;
    private final int yearMonthRow = 2;
    // private int mSelectItem = 0;

    public PlanSpinerAdapter(Context context) {
        init(context);
        mContext = context;
    }

    public void refreshData(List<T> objects, int selIndex) {
        mObjects = objects;
        if (selIndex < 0) {
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()) {
            selIndex = mObjects.size() - 1;
        }

        // mSelectItem = selIndex;
    }

    private void init(Context context) {
        // mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spiner_item_layout_plan, null);
            viewHolder = new ViewHolder();
            int[] ids = {R.id.innerPlanEvent1, R.id.innerPlanEvent2, R.id.innerPlanEvent3, R.id.innerPlanEvent4};
            viewHolder.innerPlanEvent = new TextView[ids.length];
            for (int i = 1; i < viewHolder.innerPlanEvent.length + 1; i++) {
                viewHolder.innerPlanEvent[i - 1] = (TextView) convertView.findViewById(ids[i - 1]);
                viewHolder.innerPlanEvent[i - 1].setTag(pos / 2 + "," + (i - 1));
            }
            viewHolder.innerPlanLineDesc = (TextView) convertView
                    .findViewById(R.id.innerPlanLineDesc);
            viewHolder.innerPlanLine = convertView
                    .findViewById(R.id.innerPlanLine);
            viewHolder.innerPlanLeft = (ImageView) convertView
                    .findViewById(R.id.innerPlanLeft);
            viewHolder.innerPlanRight = (ImageView) convertView
                    .findViewById(R.id.innerPlanRight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TextView[] tv = viewHolder.innerPlanEvent;
        Object item = getItem(pos);
        String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery", mContext);
        if (StringUtil.isNullOrEmpty(innerPlanQuery) || innerPlanQuery.length() != colNum * 2) {
            innerPlanQuery = mContext.getResources().getString(R.string.innerPlanQuery);
            MySharedPreferences.getInstance().setStoreString("InnerPlanQuery", innerPlanQuery, mContext);
        }
        String[] plans = innerPlanQuery.split(",");
        if (pos == lineNum + 1) {
            viewHolder.innerPlanEvent[lineNum - 1].setVisibility(View.VISIBLE);
            viewHolder.innerPlanEvent[lineNum - 1].setText(item.toString());
            viewHolder.innerPlanEvent[lineNum - 1].setTextColor(mContext.getResources().getColor(R.color.app_blue));
            for (int i = 0; i < colNum; i++) {
                viewHolder.innerPlanEvent[i].setVisibility(View.GONE);
            }
            viewHolder.innerPlanLineDesc.setVisibility(View.INVISIBLE);
            viewHolder.innerPlanLine.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.innerPlanEvent[lineNum - 1].setVisibility(View.GONE);
            if (pos % 2 == 0) {
                String[] items = item.toString().split(",");
                for (int i = 0; i < colNum; i++) {
                    viewHolder.innerPlanEvent[i].setVisibility(View.VISIBLE);
                    viewHolder.innerPlanEvent[i].setText(items[i]);
                    viewHolder.innerPlanEvent[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] lineCol = v.getTag().toString().split(",");
                            int line = Integer.parseInt(lineCol[0]);
                            String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery", mContext);
                            StringBuilder sbStr = new StringBuilder(innerPlanQuery);
                            sbStr.deleteCharAt(2 * line);
                            sbStr.insert(2 * line, lineCol[1]);
                            MySharedPreferences.getInstance().setStoreString("InnerPlanQuery", sbStr.toString(), mContext);
                            int pos = Integer.parseInt(lineCol[1]);
                            setBackground(tv, pos);
                            if (line == yearMonthRow) {
                                TextView tv = (TextView) v;
                                MySharedPreferences.getInstance().setStoreString("InnerPlanQuerySelectedYM", tv.getText().toString(), mContext);
                            }
                        }
                    });
                }
                int position = Integer.parseInt(plans[pos / 2]);
                setBackground(viewHolder.innerPlanEvent, position);
                viewHolder.innerPlanLineDesc.setVisibility(View.INVISIBLE);
                viewHolder.innerPlanLine.setVisibility(View.INVISIBLE);
            } else {
                for (int i = 0; i < colNum; i++) {
                    viewHolder.innerPlanEvent[i].setVisibility(View.GONE);
                }
                viewHolder.innerPlanLineDesc.setVisibility(View.VISIBLE);
                viewHolder.innerPlanLine.setVisibility(View.VISIBLE);
                viewHolder.innerPlanLineDesc.setText(item.toString());
            }
        }
        if (pos == lineNum) {
            viewHolder.innerPlanLeft.setVisibility(View.VISIBLE);
            viewHolder.innerPlanRight.setVisibility(View.VISIBLE);
            final TextView[] tvs = viewHolder.innerPlanEvent;
            viewHolder.innerPlanLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < colNum; i++) {
                        tvs[i].setText(getYearMonth(tvs[i].getText().toString(), true));
                        setSelectedYearMonth(i, tvs[i]);
                    }
                }
            });
            viewHolder.innerPlanRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < colNum; i++) {
                        tvs[i].setText(getYearMonth(tvs[i].getText().toString(), false));
                        setSelectedYearMonth(i, tvs[i]);
                    }
                }
            });
        } else {
            viewHolder.innerPlanLeft.setVisibility(View.INVISIBLE);
            viewHolder.innerPlanRight.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private void setSelectedYearMonth(int i, TextView tv) {
        String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery", mContext);
        int colSelected = Integer.parseInt(innerPlanQuery.substring(2 * yearMonthRow, 2 * yearMonthRow + 1));
        if (i == colSelected) {
            MySharedPreferences.getInstance().setStoreString("InnerPlanQuerySelectedYM", tv.getText().toString(), mContext);
        }
    }

    private String getYearMonth(String timeString, boolean isLeft) {
        String year = timeString.substring(0, 4);
        String month = timeString.substring(5, timeString.length());
        int iYear = Integer.parseInt(year);
        int iMonth = Integer.parseInt(month);
        if (isLeft) {
            if (iMonth > 1 && iMonth <= 12) {
                return String.format("%s-%s", iYear, getFix(iMonth - 1));
            } else {
                return String.format("%s-%s", iYear - 1, 12);
            }
        } else {
            if (iMonth >= 1 && iMonth < 12) {
                return String.format("%s-%s", iYear, getFix(iMonth + 1));
            } else {
                return String.format("%s-%s", iYear + 1, 1);
            }
        }
    }

    private String getFix(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }

    private void setBackground(TextView[] tv, int pos) {
        for (int i = 0; i < colNum; i++) {
            tv[i].setBackground(mContext.getResources().getDrawable(R.drawable.trainnet_inner_border_u));
        }
        tv[pos].setBackground(mContext.getResources().getDrawable(R.drawable.trainnet_inner_border_s));
    }

    public interface IOnItemSelectListener {
        void onItemClick(int pos);
    }

    public static class ViewHolder {
        public TextView[] innerPlanEvent;
        public TextView innerPlanLineDesc;
        public View innerPlanLine;
        public ImageView innerPlanLeft;
        public ImageView innerPlanRight;
    }

}
