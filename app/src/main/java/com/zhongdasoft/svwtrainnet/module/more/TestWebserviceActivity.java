package com.zhongdasoft.svwtrainnet.module.more;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.UploadFileActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.ClipUtil;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class TestWebserviceActivity extends BaseActivity {
    private final String packageName = "com.zhongdasoft.svwtrainnet.daomanager.model.";
    private final String StringType = "String";
    private final String IntType = "int";
    private final String BooleanType = "boolean";
    private final String ByteArrayType = "byte[]";
    private BaseActivity activity;
    private Method method;
    private ArrayList<Object> objList;
    private Method[] methods;
    private ListView lv;
    private ArrayList<HashMap<String, Object>> listItem;
    private BaseListViewAdapter mAdapter;
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<String> methodList;
    private ArrayList<ArrayList<String>> methodParamList;
    private ArrayList<String> paramList;
    private HashMap<String, Object> resultMap;
    private int inputLength = 0;
    private ArrayList<String> resultList;
    private int deep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        wr = new WeakReference<>(this);
        methods = TrainNetWebService.getInstance().getClass().getDeclaredMethods();

        methodList = new ArrayList<>();
        methodParamList = new ArrayList<>();
        ArrayList<String> methodParamSubList;
        for (Method m : methods) {
            if ("void".equals(m.getReturnType().getName())) {
                continue;
            }
            methodList.add(m.getName());
            methodParamSubList = new ArrayList<>();
            for (Class c : m.getParameterTypes()) {
                methodParamSubList.add(c.getSimpleName());
            }
            methodParamList.add(methodParamSubList);
        }


//        LinearLayout ll = (LinearLayout)findViewById(R.id.test);
//        ll.addView(mReactRootView);
        setListView();
//        lv = (ListView) activity.findViewById(R.id.listview_testwebservice);
//        lv.setVisibility(View.GONE);

//        SoftVersion.getActivityInfo(this);
//        UserPaper up = new UserPaper();
//        up.setStatus(0);
//        CRUD.getInstance().UpdateUserPaper(up);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_test_webservice;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_testwebservice;
    }

    private void setData() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        ListViewUtil.setListItem(listItem, "请选择接口名称", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, "点击此处获取接口名称", ListViewUtil.PaperInfo);
    }

    private void setListView() {
        setData();
        mAdapter = new BaseListViewAdapter(activity, listItem);
        lv = (ListView) activity.findViewById(R.id.listview_testwebservice);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.get(arg2).containsKey("paperInfo")) {
                    final int pos = arg2;
                    if (pos == 1) {
                        DialogUtil.getInstance().showListDialog(wr, "请选择接口名称", methodList.toArray(new String[]{}), new DialogUtil.MyDismiss() {
                            @Override
                            public String handleDismiss(int which, String str) {
                                listItem.get(pos).put("paperInfo", methodList.get(which));
                                addListItem(which);
                                mAdapter.notifyDataSetChanged();
                                return null;
                            }
                        });
                    } else {
                        final String value = listItem.get(arg2).get("paperInfo").toString();
                        if (value.endsWith("=context") || value.endsWith("=accesstoken") || value.endsWith("=device") || value.endsWith("=deviceId") || value.endsWith("=network") || value.endsWith("=Android")) {
                            return;
                        }
                        String strValue;
                        if (value.indexOf("=") == value.length() - 1) {
                            strValue = "";
                        } else {
                            strValue = value.split("=")[1];
                        }
                        int type = 0;
                        if (value.contains("(" + BooleanType)) {
                            type = 1;
                        }
                        DialogUtil.getInstance().showInputDialog(wr, "请输入参数值", strValue, type, new DialogUtil.MyDismiss() {
                            @Override
                            public String handleDismiss(int which, String str) {
                                listItem.get(pos).put("paperInfo", value.split("=")[0] + "=" + str);
                                mAdapter.notifyDataSetChanged();
                                return null;
                            }
                        });
                    }
                } else if (listItem.get(arg2).containsKey("paperEvent")) {
                    String interfaceContent = listItem.get(1).get("paperInfo").toString();
                    if (StringUtil.isNullOrEmpty(interfaceContent)) {
                        ToastUtil.show(TestWebserviceActivity.this, "请输入接口名称");
                        return;
                    }
                    method = null;
                    for (Method m : methods) {
                        if (interfaceContent.equalsIgnoreCase(m.getName())) {
                            method = m;
                            break;
                        }
                    }
                    if (null == method) {
                        ToastUtil.show(TestWebserviceActivity.this, "不存在该接口名称");
                        return;
                    }
                    objList = new ArrayList<>();
                    String str;
                    String value;
                    int iStart, iEnd;
                    for (int i = 0; i < listItem.size(); i++) {
                        HashMap<String, Object> map = listItem.get(i);
                        for (String key : map.keySet()) {
                            str = map.get(key).toString();
                            if (!str.contains("=")) {
                                continue;
                            }
                            String[] keyValue = str.split("=");
                            if (keyValue.length < 2) {
                                value = "";
                            } else {
                                value = str.split("=")[1];
                            }
                            switch (value) {
                                case "context":
                                    objList.add(activity);
                                    break;
                                default:
                                    //此处添加其他参数
                                    if (str.contains("(String") || str.contains("(int") || str.contains("(boolean")) {
                                        objList.add(value);
                                    } else if (str.contains("(byte[]")) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("item", "jpg,gif,png,bmp,doc,docx,xls,xlsx,ppt,pptx,zip,rar");
                                        readyGoForResult(UploadFileActivity.class, 0x101, bundle);
                                    } else {
                                        iStart = i;
                                        int pos = str.indexOf("参");
                                        iEnd = getEnd(i, pos);
                                        i = iEnd;
                                        JSONObject json = new JSONObject();
                                        generateJsonData(iStart, iEnd, json);
                                        parseJson(json.toString());
                                    }
                                    break;
                            }
                        }
                    }
                    Waiting.show(activity, getResources().getString(R.string.Loading));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Object o;
                            resultList = new ArrayList<>();
                            try {
                                o = method.invoke(TrainNetWebService.getInstance(), objList.toArray());
                                if (method.getReturnType().getSimpleName().equals("ArrayList")) {
                                    ArrayList<HashMap<String, Object>> value = (ArrayList<HashMap<String, Object>>) o;
                                    for (HashMap<String, Object> map : value) {
                                        for (String key : map.keySet()) {
                                            resultList.add(key + ":" + map.get(key).toString());
                                        }
                                    }
                                } else if (method.getReturnType().getSimpleName().equals("HashMap")) {
                                    HashMap<String, Object> map = (HashMap<String, Object>) o;
                                    for (String key : map.keySet()) {
                                        resultList.add(key + ":" + map.get(key).toString());
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                resultList.add("错误信息:" + ex.getMessage());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int len = listItem.size();
                                    for (int i = 0; i < len - inputLength; i++) {
                                        listItem.remove(listItem.size() - 1);
                                    }
                                    for (String str : resultList) {
                                        ListViewUtil.setListItem(listItem, str, ListViewUtil.PaperSubTitle);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    Waiting.dismiss();
                                }
                            });
                        }
                    }).start();
                } else if (arg2 >= inputLength) {
                    String values = listItem.get(arg2).get("paperSubTitle").toString();
                    String[] value = values.split(":");
                    ClipUtil.CopyData(activity, value[1]);
                    ToastUtil.show(activity, getResources().getString(R.string.copyFinished));
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String modelName = jsonObject.names().get(0).toString();
            switch (modelName) {
                case "Exam":
                    objList.add("");
                    break;
                case "Evaluation":
                    objList.add("");
                    break;
                case "Plan":
                    objList.add("");
                    break;
                case "Trainee":
                    objList.add("");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            objList.add("");
        }
    }

    private int getEnd(int i, int pos) {
        int iStart, iEnd = 0;
        iStart = i;
        String str;
        boolean isEnd = false;
        for (int j = iStart + 1; j < listItem.size() && !isEnd; j++) {
            for (String key1 : listItem.get(j).keySet()) {
                if (!"paperInfo".equals(key1) && !"paperSubTitle".equals(key1)) {
                    continue;
                }
                str = listItem.get(j).get(key1).toString();
                if (str.indexOf("参") > pos) {
                    continue;
                }
                isEnd = true;
                iEnd = j - 1;
                break;
            }
        }
        return iEnd;
    }

    private void generateJsonData(int iStart, int iEnd, JSONObject json) {
        JSONArray answerList = new JSONArray();
        JSONObject answer = new JSONObject();

        //分析生成model
        String model = listItem.get(iStart).get("paperSubTitle").toString();
        try {
            if (model.contains(" ")) {
                String[] models = model.split("=")[1].split(" ");
                model = models[1];
            } else {
                model = model.split("=")[1];
            }
            json.put(model, answerList);
            answerList.put(answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String field;
        int iNewEnd;
        for (int i = iStart + 1; i <= iEnd; i++) {
            if (listItem.get(i).containsKey("paperInfo") || listItem.get(i).containsKey("paperSubTitle")) {
                if (listItem.get(i).containsKey("paperInfo")) {
                    field = listItem.get(i).get("paperInfo").toString();
                    if (!field.contains(" ")) {
                        return;
                    }
                    String[] strArr = field.split(" ");
                    String fieldName = strArr[1].substring(0, strArr[1].indexOf(")"));
                    String[] fields = field.split("=");
                    String fieldValue;
                    if (fields.length < 2) {
                        fieldValue = "";
                    } else {
                        fieldValue = field.split("=")[1];
                    }
                    try {
                        answer.put(fieldName, fieldValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    field = listItem.get(i).get("paperSubTitle").toString();
                    int pos = field.indexOf("参");
                    iNewEnd = getEnd(i, pos);
                    generateJsonData(i, getEnd(i, pos), answer);
                    i = iNewEnd;
                }
            }
        }
    }

    private void addListItem(int which) {
        String methodName = listItem.get(1).get("paperInfo").toString();
        if (listItem.size() > 2) {
            listItem.clear();
            ListViewUtil.setListItem(listItem, "请选择接口名称", ListViewUtil.PaperSubTitle);
            ListViewUtil.setListItem(listItem, methodName, ListViewUtil.PaperInfo);
            deep = 0;
        }
        ListViewUtil.setListItem(listItem, "接口参数", ListViewUtil.PaperSubTitle);
        paramList = methodParamList.get(which);
        String strType;
        ListViewUtil.setListItem(listItem, "参数1=context", ListViewUtil.PaperSubTitle);
        for (int i = 1; i < paramList.size(); i++) {
            strType = paramList.get(i);
            switch (strType) {
                case StringType:
                    ListViewUtil.setListItem(listItem, String.format("参数%s(%s)=", i + 1, strType), ListViewUtil.PaperInfo);
                    break;
                case IntType:
                    ListViewUtil.setListItem(listItem, String.format("参数%s(%s)=0", i + 1, strType), ListViewUtil.PaperInfo);
                    break;
                case BooleanType:
                    ListViewUtil.setListItem(listItem, String.format("参数%s(%s)=false", i + 1, strType), ListViewUtil.PaperInfo);
                    break;
                case ByteArrayType:
                    ListViewUtil.setListItem(listItem, String.format("参数%s(%s)=", i + 1, strType), ListViewUtil.PaperInfo);
                    break;
                default:
                    setSubModel(strType, i + 1, deep);
                    break;
            }
        }
        ListViewUtil.setListItem(listItem, "测试接口", ListViewUtil.PaperEvent);
        inputLength = listItem.size();
    }

    private void setSubModel(String strType, int pos, int deep) {
        String fieldName = null;
        if (strType.contains(",")) {
            String[] type = strType.split(",");
            strType = type[0];
            fieldName = type[1];
        }
        Class clazz = null;
        try {
            clazz = Class.forName(packageName + strType);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        TreeMap<String, String> tm = new TreeMap<>();
        boolean isSubTitle = false;
        String fieldType;
        if (null != clazz) {
            deep++;
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                fieldType = f.getType().getSimpleName();
                if (fieldType.startsWith(StringType) || fieldType.startsWith(BooleanType) || fieldType.startsWith(IntType)) {
                    tm.put(f.getName(), fieldType);
                    isSubTitle = true;
                    continue;
                }
                if (f.getType().getSimpleName().startsWith("KvmComplexList")) {
                    Type fc = f.getGenericType();
                    // 如果不为空并且是泛型参数的类型
                    if (fc != null && fc instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) fc;
                        Type[] types = pt.getActualTypeArguments();
                        if (types != null && types.length > 0) {
                            Class<?>[] classes = new Class<?>[types.length];
                            for (int k = 0; k < classes.length; k++) {
                                tm.put(f.getName(), ((Class<?>) types[k]).getSimpleName() + "," + f.getName());
                            }
                        }
                    }
                } else {
                    tm.put(f.getName(), fieldType);
                    isSubTitle = true;
                }
            }
            //此处要根据内部的类型进行处理
            if (isSubTitle) {
                if (null != fieldName) {
                    ListViewUtil.setListItem(listItem, String.format("%s参数%s=%s %s", getSpace(deep - 1), pos, strType, fieldName), ListViewUtil.PaperSubTitle);
                } else {
                    ListViewUtil.setListItem(listItem, String.format("%s参数%s=%s", getSpace(deep - 1), pos, strType), ListViewUtil.PaperSubTitle);
                }
            } else {
                ListViewUtil.setListItem(listItem, String.format("%s参数%s=%s", getSpace(deep - 1), pos, clazz.getSimpleName()), ListViewUtil.PaperSubTitle);
            }
            for (String key : tm.keySet()) {
                if (tm.get(key).startsWith(StringType) || tm.get(key).startsWith(BooleanType) || tm.get(key).startsWith(IntType)) {
                    if (tm.get(key).startsWith(StringType)) {
                        ListViewUtil.setListItem(listItem, String.format("%s参数%s(%s %s)=", getSpace(deep), pos, tm.get(key), key), ListViewUtil.PaperInfo);
                    } else if (tm.get(key).startsWith(BooleanType)) {
                        ListViewUtil.setListItem(listItem, String.format("%s参数%s(%s %s)=false", getSpace(deep), pos, tm.get(key), key), ListViewUtil.PaperInfo);
                    } else {
                        ListViewUtil.setListItem(listItem, String.format("%s参数%s(%s %s)=0", getSpace(deep), pos, tm.get(key), key), ListViewUtil.PaperInfo);
                    }
                } else {
                    setSubModel(tm.get(key), pos, deep);
                }
            }
            return;
        }
    }

    private String getSpace(int deep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x101) {
            byte[] content = data.getByteArrayExtra("item");
            String filePath = data.getStringExtra("filePath");
            if (null != content) {
                objList.add(content);
            } else {
                ToastUtil.show(TestWebserviceActivity.this, "上传文件失败");
            }
        }
    }
}
