package com.zhongdasoft.svwtrainnet.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.zhongdasoft.svwtrainnet.R;

public class Waiting {
    // 声明进度条对话框
    private static ProgressDialog m_pDialog;

    public static void show(Context context) {
        show(context, "请稍等...");
    }

    public static void show(Context context, String message) {
        // 创建ProgressDialog对象
        if (null == m_pDialog || !m_pDialog.isShowing()) {
            try {
                m_pDialog = new ProgressDialog(context, R.style.dialog);
                WindowManager.LayoutParams params = m_pDialog.getWindow().getAttributes();
                params.alpha = 0.9f;
                params.dimAmount = 0f;
                m_pDialog.getWindow().setAttributes(params);
                // 设置进度条风格，风格为圆形，旋转的
                m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // 设置ProgressDialog 提示信息
                m_pDialog.setMessage(message);
                // 设置ProgressDialog 的进度条是否不明确
                m_pDialog.setIndeterminate(false);
                // 设置ProgressDialog 是否可以按退回按键取消
                m_pDialog.setCancelable(false);
                m_pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            m_pDialog.setMessage(message);
        }
    }

    public static void setText(String text) {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.setMessage(text);
        }
    }

    public static void dismiss() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.dismiss();
            m_pDialog = null;
        }

    }
}
