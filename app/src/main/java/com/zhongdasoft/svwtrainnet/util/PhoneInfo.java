package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PhoneInfo {

    private static PhoneInfo instance;

    private PhoneInfo() {
    }

    /**
     * 单例模式
     */
    public synchronized static PhoneInfo getInstance() {
        if (null == instance) {
            instance = new PhoneInfo();
        }
        return instance;
    }

    /* 设备ID */
    public String getDeviceId(Context context) {
        String deviceId;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(android.content.Context.TELEPHONY_SERVICE);

        try {
            if (tm.getDeviceId() == null) {
                deviceId = android.os.Build.SERIAL;
            } else {
                deviceId = tm.getDeviceId();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
            deviceId = android.os.Build.SERIAL;
        }
        return deviceId;
    }

    /* 生产商 */
    public String getManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /* 型号 */
    public String getModel() {
        return android.os.Build.MODEL;
    }

    /* OS版本 */
    public String getOSVersion() {
        return "SDK:Android " + android.os.Build.VERSION.RELEASE;
    }

    /* 网络类型 */
    public String getNetworkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(android.content.Context.TELEPHONY_SERVICE);

        return tm.getNetworkOperatorName();
    }

    /* 服务商 */
    public String getCarrier(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(android.content.Context.TELEPHONY_SERVICE);

        return tm.getSimOperatorName();
    }

    /* 手机号码 */
    public String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(android.content.Context.TELEPHONY_SERVICE);
        try {
            return tm.getLine1Number();
        } catch (SecurityException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址。
     *
     * @return
     * @author SHANHY
     */
    public String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String getNetworkXML(Context context) {
        StringBuilder sbXML = new StringBuilder();
        String value;
        sbXML.append("<Type>");
        if (NetManager.isWifiConnected(context)) {
            value = "WLAN";
        } else {
            value = "Cellular";
        }
        sbXML.append(value).append("</Type>");
        sbXML.append("<Carrier>").append(getCarrier(context)).append("</Carrier>");
        sbXML.append("<PhoneNumber>").append(getPhoneNumber(context)).append("</PhoneNumber>");
        sbXML.append("<IPAddress>").append(getPsdnIp()).append("</IPAddress>");
        return sbXML.toString();
    }

    public String getDeviceXML(Context context) {
        StringBuilder sbXML = new StringBuilder();
        sbXML.append("<DeviceId>").append(getDeviceId(context)).append("</DeviceId>");
        sbXML.append("<Platform>").append("Android").append("</Platform>");
        sbXML.append("<Manufacturer>").append(getManufacturer()).append("</Manufacturer>");
        sbXML.append("<Model>").append(getModel()).append("</Model>");
        sbXML.append("<OSVersion>").append(getOSVersion()).append("</OSVersion>");

        return sbXML.toString();
    }

    public String getExamXML(String... param) {
        String examineeId = param[0];
        String paperId = param[1];
        String startTime = param[2];
        String endTime = param[3];
        String grade = param[4];
        String answers = param[5];
        StringBuilder sbXML = new StringBuilder();
        sbXML.append("<ExamineeId>").append(examineeId).append("</ExamineeId>");
        sbXML.append("<PaperId>").append(paperId).append("</PaperId>");
        sbXML.append("<StartTime>").append(startTime).append("</StartTime>");
        sbXML.append("<FinishTime>").append(endTime).append("</FinishTime>");
        sbXML.append("<Score>").append(grade).append("</Score>");
        sbXML.append("<Answers>").append(getExamAnswersXML(answers)).append("</Answers>");
        return sbXML.toString();
    }

    private String getExamAnswersXML(String json) {
        StringBuilder sbXML = new StringBuilder();
        JsonParser parse = new JsonParser();
        JsonArray ja = parse.parse(json).getAsJsonArray();
        for (int i = 0; i < ja.size(); i++) {
            sbXML.append("<ApiExamQuestionAnswer>");
            sbXML.append("<QuestionId>").append(ja.get(i).getAsJsonObject().get("qId").getAsString()).append("</QuestionId>");
            sbXML.append("<Answer>").append(ja.get(i).getAsJsonObject().get("answer").getAsString()).append("</Answer>");
            sbXML.append("</ApiExamQuestionAnswer>");
        }
        return sbXML.toString();
    }

    public String getEvaluationAnswersXML(String json) {
        StringBuilder sbXML = new StringBuilder();
        JsonParser parse = new JsonParser();
        JsonArray ja = parse.parse(json).getAsJsonArray();
        for (int i = 0; i < ja.size(); i++) {
            sbXML.append("<ApiEvaluationAnswer>");
            sbXML.append("<Id>").append(ja.get(i).getAsJsonObject().get("Id").getAsString()).append("</Id>");
            sbXML.append("<Answer>").append(ja.get(i).getAsJsonObject().get("Answer").getAsString()).append("</Answer>");
            sbXML.append("<Type>").append(ja.get(i).getAsJsonObject().get("Type").getAsString()).append("</Type>");
            sbXML.append("</ApiEvaluationAnswer>");
        }
        return sbXML.toString();
    }

    public String getTraineeXML(String... param) {
        StringBuilder sbXML = new StringBuilder();
        String StaffId = "";
        String Idcard = "";
        String Id = "";
        String Grade = "0";
        String CertificateNo = "";
        String Attendance = "";
        String Assessment = "";
        if (param.length == 1) {
            Idcard = param[0];
        } else if (param.length == 2) {
            StaffId = param[0];
            Idcard = param[1];
        } else if (param.length == 5) {
            Id = param[0];
            Grade = param[1];
            CertificateNo = param[2];
            Attendance = param[3];
            Assessment = param[4];
        } else {
            return "";
        }
        sbXML.append("<Id>").append(Id).append("</Id>");
        sbXML.append("<StaffId>").append(StaffId).append("</StaffId>");
        sbXML.append("<Idcard>").append(Idcard).append("</Idcard>");
        sbXML.append("<Grade>").append(Grade).append("</Grade>");
        sbXML.append("<Attendance>").append(Attendance).append("</Attendance>");
        sbXML.append("<Assessment>").append(Assessment).append("</Assessment>");
        sbXML.append("<CertificateNo>").append(CertificateNo).append("</CertificateNo>");
        return sbXML.toString();
    }


    public String getPlanXML(String... param) {
        StringBuilder sbXML = new StringBuilder();
        String Id = param[0];
        String DealerNo = param[1];
        String Type = param[2];
        String CourseNo = param[3];
        String CourseName = param[4];
        String StartDate = param[5];
        String FinishDate = param[6];
        String Term = param[7];
        String PlanNumber = param[8];
        String ApproveNumber = param[9];
        String Published = param[10];
        String TeacherJson = param[11];

        StringBuilder Dealer = new StringBuilder();
        Dealer.append("<DealerNo>").append(DealerNo).append("</DealerNo>");

        StringBuilder Course = new StringBuilder();
        Course.append("<CourseNo>").append(CourseNo).append("</CourseNo>");
        Course.append("<CourseName>").append(CourseName).append("</CourseName>");

        StringBuilder Teachers = new StringBuilder();
        Teachers.append("<ApiDealerStaffUser>");
        if (TeacherJson.contains(",")) {
            String[] ids = TeacherJson.split(",");
            Teachers.append("<Id>").append(ids[0]).append("</Id>");
            Teachers.append("<Name>").append(ids[1]).append("</Name>");
        } else {
            Teachers.append("<Id>").append(TeacherJson).append("</Id>");
            Teachers.append("<Name>").append("").append("</Name>");
        }
        Teachers.append("</ApiDealerStaffUser>");

        sbXML.append("<Id>").append(Id).append("</Id>");
        sbXML.append("<Dealer>").append(Dealer).append("</Dealer>");
        sbXML.append("<Type>").append(Type).append("</Type>");
        sbXML.append("<Course>").append(Course).append("</Course>");
        sbXML.append("<StartDate>").append(StartDate).append("</StartDate>");
        sbXML.append("<FinishDate>").append(FinishDate).append("</FinishDate>");
        sbXML.append("<Term>").append(Term).append("</Term>");
        sbXML.append("<PlanNumber>").append(PlanNumber).append("</PlanNumber>");
        sbXML.append("<ApproveNumber>").append(ApproveNumber).append("</ApproveNumber>");
        sbXML.append("<Published>").append(Published).append("</Published>");
        sbXML.append("<Teachers>").append(Teachers).append("</Teachers>");

        return sbXML.toString();
    }
}
