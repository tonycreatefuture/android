package com.zhongdasoft.svwtrainnet.network;

import android.util.Log;

import com.zhongdasoft.svwtrainnet.util.DateUtil;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/10/31 16:12
 * 修改人：Administrator
 * 修改时间：2016/10/31 16:12
 * 修改备注：
 */

public class ServiceObject {
    private final int timeout = 30 * 1000;
    private static ServiceObject instance;

    private ServiceObject() {
    }

    /**
     * 单例模式
     */
    public synchronized static ServiceObject getInstance() {
        if (null == instance) {
            instance = new ServiceObject();
        }
        return instance;
    }

    public ArrayList<HashMap<String, Object>> invoke(HashMap<String, String> mParams) throws Exception {
        String serviceUrl = mParams.get("serviceUrl").toString();
        String methodName = mParams.get("methodName").toString();
        String recurField = null != mParams.get("recurField") ? mParams.get("recurField").toString() : "";
        String listKey = null == mParams.get("listKey") ? "" : mParams.get("listKey").toString();
        mParams.remove("serviceUrl");
        mParams.remove("methodName");
        mParams.remove("recurField");
        mParams.remove("listKey");
        Log.d(String.format("---Invoke %s Begin---", methodName), DateUtil.getSystemDateTime());
        String soapRequestData = buildRequestData(methodName, mParams);
        byte[] data = soapRequestData.getBytes();
        URL url = new URL(serviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(timeout);
        conn.setDoInput(true);                  //是否有入参
        conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        ArrayList<HashMap<String, Object>> listResult;
        if (conn.getResponseCode() == 200) {
            String responseStr = inputStream2String(conn.getInputStream());
//            Log.d("---Response---", responseStr);
//            String xmlPrefix = String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><%sResponse xmlns=\"http://svwtrainnet.csvw.com\">", methodName);
//            String xmlPostfix = String.format("</%sResponse></soap:Body></soap:Envelope>", methodName);
            String jsonStr = JsonUtil.xml2JSON(responseStr);
            Log.d("---Response---", jsonStr);
            listResult = parserXMLPULL(responseStr, recurField);
            for (HashMap<String, Object> map : listResult) {
                map.remove("ParentNodeEnd");
            }
            Log.d(String.format("---Invoke %s End---", methodName), DateUtil.getSystemDateTime());
            return listResult;
        }
        return null;
    }

    private String buildRequestData(String mName, HashMap<String, String> mParams) {
        String nameSpace = MyProperty.loadConfig().getProperty("NAMESPACE");
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapRequestData
                .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapRequestData.append("<soap:Body>");
        soapRequestData.append("<" + mName + " xmlns=\"" + nameSpace
                + "\">");
        String value;
        for (String name : mParams.keySet()) {
            value = replaceSpecialChar(mParams.get(name));
            soapRequestData.append("<" + name + ">" + value
                    + "</" + name + ">");
        }
        soapRequestData.append("</" + mName + ">");
        soapRequestData.append("</soap:Body>");
        soapRequestData.append("</soap:Envelope>");

        return soapRequestData.toString();
    }

    private String replaceSpecialChar(String s) {
        if (s.contains("&")) {
            s = s.replace("&", "&amp;");
        }
//        if (s.contains("<")) {
//            s = s.replace("<", "&lt;");
//        }
//        if (s.contains(">")) {
//            s = s.replace(">", "&gt;");
//        }
        if (s.contains("&amp;amp;")) {
            s = s.replace("&amp;amp;", "&amp;");
        }
        return s;
    }

    private ArrayList<HashMap<String, Object>> parserXMLPULL(String xml, String recurField) {
        String fieldName = "";
        String fieldKey = "";
        String parentId = "";
        if (!StringUtil.isNullOrEmpty(recurField)) {
            String[] fields = recurField.split(",");
            fieldName = fields[0];
            fieldKey = fields[1];
        }
        ArrayList<HashMap<String, Object>> listResult = null;
        HashMap<String, Object> mapResult = null;
//     XmlPullParserException
//     XmlSerializer
//     方式一：
//     XmlPullParser parser = Xml.newPullParser();
//     方法二：
        XmlPullParserFactory xpf;
        XmlPullParser parser;
        try {
            xpf = XmlPullParserFactory.newInstance();
            parser = xpf.newPullParser();
//          将XML文件以流的形式加入，并设置XML文件的编码方式
//          parser.setInput(InputStrean inputStream, String inputEncoding)
//          parser.setInput(Reader reader)
//            parser.setInput(is, "utf-8");
            parser.setInput(new StringReader(xml));
//          此时文档刚初始化，所以解析的位置在文档的开头
            int type = parser.getEventType();//此时返回０，也就是在START_DOCUMENT
//          返回类型START_DOCUMENT,END_DOCUMENT,START_TAG,END_TAG,TEXT
            int index = -1;
            boolean isText;
            while (type != XmlPullParser.END_DOCUMENT) {
                isText = true;
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        //做一些初始化工作
                        listResult = new ArrayList<>();
                        index = -1;
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (!isNormal(name)) {
                            break;
                        }
                        type = parser.next();//让解析器指向name属性的值
                        if (type == XmlPullParser.END_TAG) {
                            break;
                        }
                        if (type == XmlPullParser.TEXT) {
                            listResult.get(index).put(name, parser.getText());
                            isText = true;
                        } else {
                            if (fieldName.equalsIgnoreCase(name)) {
                                parentId = listResult.get(listResult.size() - 1).get(fieldKey).toString();
                            }
                            mapResult = new HashMap<>();
                            listResult.add(mapResult);
                            listResult.get(listResult.size() - 1).put("ParentNode", name);
                            index = listResult.size() - 1;
                            isText = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String value = parser.getName();
                        if (!isNormal(value)) {
                            break;
                        }
                        if (fieldName.equalsIgnoreCase(value)) {
                            parentId = "";
                        }
                        if (!StringUtil.isNullOrEmpty(parentId)) {
                            listResult.get(index).put("parentId", parentId);
                        }
                        int pos = returnUnfinished(listResult, value);
                        if (-1 != pos) {
                            index = pos;
                        }
                        break;
                }
                if (isText) {
                    type = parser.next();//当前解析位置结束，指向下一个位置
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listResult;
    }

    private int returnUnfinished(ArrayList<HashMap<String, Object>> listResult, String value) {
        int pos = -1;
        for (int i = listResult.size() - 1; i >= 0; i--) {
            if (value.equalsIgnoreCase(listResult.get(i).get("ParentNode").toString())
                    && !listResult.get(i).containsKey("ParentNodeEnd")) {
                listResult.get(i).put("ParentNodeEnd", "");
                pos = i;
                break;
            }
        }
        for (int j = pos - 1; j >= 0; j--) {
            if (listResult.get(j).containsKey("ParentNodeEnd")) {
                continue;
            }
            return j;
        }
        return -1;
    }

    private boolean isNormal(String value) {
        if ("soap:Envelope".equalsIgnoreCase(value) || "soap:Body".equalsIgnoreCase(value)) {
            return false;
        }
        return true;
    }

    private String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


}
