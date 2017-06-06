package com.zhongdasoft.svwtrainnet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.PhoneInfo;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;

import java.util.ArrayList;
import java.util.HashMap;

public class TraineeListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private BaseActivity mActivity;
    private ArrayList<HashMap<String, Object>> mData;
    private HashMap<String, Object> dispatchMap;
    private TextView innerPlanDispatchNum;
    private int planNumber = 0;
    private int approveNumber = 0;

    public TraineeListViewAdapter(BaseActivity activity,
                                  ArrayList<HashMap<String, Object>> mData) {
        this.mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.mData = mData;
    }

    public ArrayList<HashMap<String, Object>> getData() {
        return this.mData;
    }

    public void setData(ArrayList<HashMap<String, Object>> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.trainnet_item_traineelist,
                    null);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.subTitle);
            holder.subTitleDesc = (TextView) convertView.findViewById(R.id.subTitleDesc);
            holder.img_noData = (ImageView) convertView.findViewById(R.id.img_noData);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setCb(position, "cb", holder.cb);
        setText(position, "title", holder.title);
        setText(position, "subTitle", holder.subTitle);
        setText(position, "subTitleDesc", holder.subTitleDesc);
        setImg(position, "img_noData", holder.img_noData);
        return convertView;
    }

    private void setText(int position, String key, TextView tv) {
        if (mData.get(position).containsKey(key)) {
            String info = "";
            if (mData.get(position).get(key) != null) {
                info = mData.get(position).get(key).toString();
            }
            tv.setText(HtmlUtil.fromHtml(info));
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void setImg(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
            Integer drawableId = (Integer) mData.get(position).get(key);
            img.setImageResource(drawableId);
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.GONE);
        }
    }

    private void setCb(final int position, String key, CheckBox cb) {
        if (mData.get(position).containsKey(key)) {
            cb.setVisibility(View.VISIBLE);
            String value = mData.get(position).get(key).toString();
            if ("1".equals(value) || "2".equals(value)) {
                cb.setEnabled(true);
                if ("1".equals(value)) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
            } else {
                cb.setEnabled(false);
                cb.setChecked(false);
            }
            if (!mData.get(0).containsKey("readOnly")) {
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox myCb = (CheckBox) v;
                        String planId = mData.get(position).get("planId").toString();
                        String staffId = mData.get(position).get("StaffId").toString();
                        String idcard = mData.get(position).get("Idcard").toString();
                        View listView = (View) v.getParent();
                        RelativeLayout rlParent = (RelativeLayout) listView.getParent().getParent();
                        innerPlanDispatchNum = (TextView) rlParent.findViewById(R.id.innerPlanDispatchNum);
                        innerPlanDispatchNum.setTag(position);
                        if (0 == planNumber) {
                            approveNumber = Integer.parseInt(mData.get(position).get("approveNumber").toString());
                            planNumber = Integer.parseInt(mData.get(position).get("planNumber").toString());
                        }
                        if ((myCb.isChecked() && approveNumber == planNumber) || (!myCb.isChecked() && approveNumber == 0)) {
                            if (myCb.isChecked() && approveNumber == planNumber) {
                                myCb.setChecked(false);
                                ToastUtil.show(mActivity, mActivity.getResources().getString(R.string.errPlanNumMore));
                            } else {
                                myCb.setChecked(true);
                                ToastUtil.show(mActivity, mActivity.getResources().getString(R.string.errPlanNumZero));
                            }
                            return;
                        }
                        runThread(planId, staffId, idcard, myCb.isChecked());
                    }
                });
            } else {
                cb.setEnabled(false);
            }
        } else {
            cb.setVisibility(View.GONE);
        }
    }

    private void runThread(final String planId, final String staffId, final String idCard, final boolean isDispatch) {
        Waiting.show(mActivity, mActivity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String trainee = PhoneInfo.getInstance().getTraineeXML(staffId, idCard);
                if (isDispatch) {
                    dispatchMap = TrainNetWebService.getInstance().InternalRegisterTrainee(mActivity, planId, trainee);
                } else {
                    dispatchMap = TrainNetWebService.getInstance().InternalCancelRegisterTrainee(mActivity, planId, trainee);
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        submitResult(isDispatch);
                    }
                });
            }
        }).start();
    }

    private void submitResult(boolean isDispatch) {
        String returnCode = dispatchMap.get(mActivity.getResources().getString(R.string.ReturnCode)).toString();
        if ("0".equals(returnCode)) {
            if (isDispatch) {
                innerPlanDispatchNum.setText(String.format("%s/%s", ++approveNumber, planNumber));
            } else {
                innerPlanDispatchNum.setText(String.format("%s/%s", --approveNumber, planNumber));
            }
            int position = Integer.parseInt(innerPlanDispatchNum.getTag().toString());
            mData.get(position).put("cb", isDispatch ? "1" : "2");
            for (HashMap<String, Object> map : mData) {
                map.put("approveNumber", approveNumber);
            }
//            getCache().remove(CacheKey.InnerPlanDispatchRefresh + planId);
        } else {
            String message = dispatchMap.get(mActivity.getResources().getString(R.string.Message)).toString();
            ToastUtil.show(mActivity, message);
        }
    }

    public final class ViewHolder {
        public CheckBox cb;
        public TextView title;
        public TextView subTitle;
        public TextView subTitleDesc;
        public ImageView img_noData;
    }
}