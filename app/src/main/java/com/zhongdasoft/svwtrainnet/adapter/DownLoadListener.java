package com.zhongdasoft.svwtrainnet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.DownloadListener;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.FileUtil;
import com.zhongdasoft.svwtrainnet.util.PermissionUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.io.File;
import java.net.URLDecoder;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/8 11:43
 * 修改人：Administrator
 * 修改时间：2016/7/8 11:43
 * 修改备注：
 */
public class DownLoadListener implements DownloadListener {
    private Context mContext;
    private String downloadPath;

    public DownLoadListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        final String url_ = url;
        final String contentDisposition_ = contentDisposition;
        PermissionUtil.getInstance().applyPermission((Activity) mContext, new PermissionUtil.PermissionHandler() {
            @Override
            public void handlePermission(boolean granted) {
                if (!granted) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(mContext, mContext.getResources().getString(R.string.sdCardTips));
                        }
                    });
                    return;
                }
                String[] content = contentDisposition_.split(";");
                if ("attachment".equals(content[0])) {
                    final String[] fileNames = content[1].split("=");
                    if (fileNames.length <= 1) {
                        return;
                    }
                    DownloaderTask task = new DownloaderTask(fileNames[1]);
                    task.execute(url_);
                }
            }
        });
    }

    //内部类
    private class DownloaderTask extends AsyncTask<String, Void, String> {
        private String fileName;

        public DownloaderTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
//          Log.i("tag", "url="+url);
//            String fileName = url.substring(url.lastIndexOf("/") + 1);
            fileName = URLDecoder.decode(fileName);
////            Log.i("tag", "fileName="+fileName);
            downloadPath = Environment.getExternalStorageDirectory() + "/download";
            File file = new File(downloadPath, fileName);
            if (file.exists()) {
//                Log.i("tag", "The chat_file has already exists.");
                return fileName;
            }
            boolean result = FileUtil.getInstance().saveFileFromHttp(url, fileName, downloadPath);
            if (!result) {
                return null;
            } else {
                return fileName;
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //closeProgressDialog();
            Waiting.dismiss();
            if (result == null) {
                ToastUtil.show(mContext, "连接错误！请稍后再试！");
                return;
            }
            ToastUtil.show(mContext, "已保存到SD卡。");
            File file = new File(downloadPath, result);
//            Log.i("tag", "Path="+chat_file.getAbsolutePath());
            //通过默认工具软件打开文档
            Intent intent = getFileIntent(file, getMIMEType(file));
            try {
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                //找不到默认工具软件，则提示用户选择相关工具软件打开
                try {
                    intent = getFileIntent(file, "*/*");
                    mContext.startActivity(intent);
                } catch (Exception ex) {
                    //还是有异常，则提示用户下载工具软件
                    ToastUtil.show(mContext, "系统打开文档失败，请安装相关工具软件后重新下载该文档");
                    ex.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            Waiting.show(mContext);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }


    }


    public Intent getFileIntent(File file, String type) {
//       Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
        Uri uri = Uri.fromFile(file);
//        String type = getMIMEType(chat_file);
//        Log.i("tag", "type="+type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        return intent;
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
      /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
        /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else if (end.equals("pptx") || end.equals("ppt")) {
            type = "application/vnd.ms-powerpoint";
        } else if (end.equals("docx") || end.equals("doc")) {
            type = "application/vnd.ms-word";
        } else if (end.equals("xlsx") || end.equals("xls")) {
            type = "application/vnd.ms-excel";
        } else {
//        /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }
}
