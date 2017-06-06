package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/5/1 16:41
 * 修改人：tony
 * 修改时间：2017/5/1 16:41
 * 修改备注：
 */

public class CameraUtil {

    private static CameraUtil instance;

    private CameraUtil() {
    }

    /**
     * 单例模式
     */
    public synchronized static CameraUtil getInstance() {
        if (null == instance) {
            instance = new CameraUtil();
        }
        return instance;
    }

//    public void openPhoneCamera(final BaseActivity activity) {
//        //sd卡可读写
//        PermissionUtil.getInstance().applyPermission(activity, new PermissionUtil.PermissionHandler() {
//            @Override
//            public void handlePermission(boolean granted) {
//                if (!granted) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.show(activity, activity.getResources().getString(R.string.sdCardTips));
//                        }
//                    });
//                    return;
//                }
//                //此处打开手机根目录
//                Bundle bundle = new Bundle();
//                bundle.putString("item", "jpg,gif,png,bmp");
//                bundle.putString("uploadFiles", "");
//                activity.readyGoForResult(UploadImageFileActivity.class, Integer.parseInt(activity.getResources().getString(R.string.UPLOAD_IMAGE_REQUEST_CODE)), bundle);
//            }
//        });
//    }


    public void openCamera(final BaseActivity activity) {
        //sd卡可读写
        PermissionUtil.getInstance().applyPermission(activity, new PermissionUtil.PermissionHandler() {
            @Override
            public void handlePermission(boolean granted) {
                if (!granted) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(activity, activity.getResources().getString(R.string.sdCardTips));
                        }
                    });
                    return;
                }
                //调用拍照功能，获取压缩图片及保存位置
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(getCameraFileTmpPath(), getCameraFileName());
                Uri imageUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(intent, Integer.parseInt(activity.getResources().getString(R.string.CAMERA_REQUEST_CODE)));
            }
        });
    }

    public int compressImage(final BaseActivity activity, String filePath) {
        Bitmap photo = null;
        FileOutputStream fos = null;
        ByteArrayOutputStream baos = null;
        File file = new File(filePath);
        if (file.length() == 0) {
            file.delete();
            return -1;
        }
        try {
            long uploadFileSize = Long.parseLong(activity.getResources().getString(R.string.uploadFileSize));
            photo = BitmapFactory.decodeFile(filePath);
            fos = new FileOutputStream(file);

            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (baos.toByteArray().length > uploadFileSize) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != photo) {
                photo.recycle();
            }
        }
        return -1;
    }

    public byte[] getCompressBitmap(final BaseActivity activity, Bitmap bitmap) {
        final int ImageQuanlity = 100;
        long uploadFileSize = Long.parseLong(activity.getResources().getString(R.string.uploadFileSize));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, ImageQuanlity, baos);
        int highQuality = ImageQuanlity;
        int lowQuality = 0;
        int quality;
        if (baos.toByteArray().length > uploadFileSize) {
            while (true) {
                quality = (highQuality + lowQuality) / 2;
                baos.reset();
                if (quality <= 0) {
                    break;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                if (Math.abs(highQuality - lowQuality) <= 1) {
                    break;
                }
                if (baos.toByteArray().length > uploadFileSize) {
                    highQuality = quality;
                } else {
                    lowQuality = quality;
                }
//                Log.e("------质量--------", baos.toByteArray().length + "," + quality + ",high=" + highQuality + ",low=" + lowQuality);
            }
        }
        if (null != bitmap) {
            bitmap.recycle();
        }
        return baos.toByteArray();
    }

    public void uploadImageFile(final BaseActivity activity, Bitmap bitmap) {
        byte[] content;
        content = getCompressBitmap(activity, bitmap);
        final String contentStr = Base64.encodeToString(content, Base64.DEFAULT);
        Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> resultMap = TrainNetWebService.getInstance().ProfileUploadPhoto(activity, contentStr);
                final String msg = resultMap.get(activity.getResources().getString(R.string.Message)).toString();
//                refreshFilePath(activity);
                WebserviceUtil.getInstance().profilePhotoRefresh(activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(activity, msg);
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    public void refreshFilePath(final BaseActivity activity) {
        if (hasKitkat()) {
            String cameraFilePath = getCameraFilePath();
            File outDir = new File(cameraFilePath);
            File outFile = new File(cameraFilePath + getCameraFileName());
            MediaScannerConnection.scanFile(activity,
                    new String[]{outDir.getAbsolutePath()}, new String[]{"image/*"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            activity.sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                            activity.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
                        }
                    });
            scanPhotos(outFile.getAbsolutePath(), activity); // 实际起作用的方法
        } else {
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public String getCameraFilePath() {
        return TrainNetApp.getContext().getFilesDir().getPath() + "/";
    }

    public String getCameraFileTmpPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    public String getCameraFileName() {
        return "myCamera.jpg";
    }

    private void beginCrop(Uri source, BaseActivity activity) {
        Uri destination = Uri.fromFile(new File(activity.getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(activity);
    }

    private void handleCrop(int resultCode, Intent result, BaseActivity activity) {
        if (resultCode == activity.RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(Crop.getOutput(result).getPath());
            deleteTmpFile();
            uploadImageFile(activity, bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil.show(activity, Crop.getError(result).getMessage());
        }
    }

    private void handleCrop(int resultCode, Intent result, WeakReference<? extends BaseActivity> wr, final LinearLayout ll_upload) {
        final BaseActivity activity = wr.get();
        if (resultCode == activity.RESULT_OK) {
            changeFileName(wr, ll_upload, Crop.getOutput(result).getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil.show(activity, Crop.getError(result).getMessage());
        }
    }

    private void deleteTmpFile() {
        String cameraFile = getCameraFileTmpPath() + getCameraFileName();
        File file = new File(cameraFile);
        if (file.exists()) {
            file.delete();
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data, BaseActivity activity) {
        int camera_request_code = Integer.parseInt(activity.getResources().getString(R.string.CAMERA_REQUEST_CODE));
        String cameraFile = getCameraFileTmpPath() + getCameraFileName();
        if (requestCode == camera_request_code) {
            if (-1 != compressImage(activity, cameraFile)) {
                File file = new File(cameraFile);
                Uri destination = Uri.fromFile(file);
                beginCrop(destination, activity);
            }
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == activity.RESULT_OK) {
            beginCrop(data.getData(), activity);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data, activity);
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data, final LinearLayout ll_upload, WeakReference<? extends BaseActivity> wr) {
        final BaseActivity activity = wr.get();
        int camera_request_code = Integer.parseInt(activity.getResources().getString(R.string.CAMERA_REQUEST_CODE));
        String cameraFile = getCameraFileTmpPath() + getCameraFileName();
        if (requestCode == camera_request_code) {
            if (-1 != compressImage(activity, cameraFile)) {
                File file = new File(cameraFile);
                Uri destination = Uri.fromFile(file);
                beginCrop(destination, activity);
            }
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == activity.RESULT_OK) {
            beginCrop(data.getData(), activity);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data, wr, ll_upload);
        }
    }


    private void changeFileName(final WeakReference<? extends BaseActivity> wr, final LinearLayout ll_upload, final String filePath) {
        final BaseActivity activity = wr.get();
        String newFileName = MySharedPreferences.getInstance().getCurrentTime(activity);
        newFileName = newFileName.replace("-", "").replace(":", "").replace("T", "").replace(" ", "");
        DialogUtil.getInstance().showInputDialog(wr, "请输入文件名", newFileName, 0, new DialogUtil.MyDismiss() {
            @Override
            public String handleDismiss(int which, String str) {
                File file = new File(filePath);
                if (str.startsWith(" ")) {
                    return activity.getResources().getString(R.string.errFileSpace);//"文件名不能以空格开头";
                }
                if (str.contains("?") || str.contains("\\")
                        || str.contains("/") || str.contains("*")
                        || str.contains(":") || str.contains("\"")
                        || str.contains("<") || str.contains(">")
                        || str.contains("|")) {
                    return activity.getResources().getString(R.string.errFileSymbol);
                }
                String uploadFiles = uploadedFiles(ll_upload);
                if (!StringUtil.isNullOrEmpty(uploadFiles) && uploadFiles.indexOf(str + ".jpg,") >= 0) {
                    return activity.getResources().getString(R.string.errFileExist);
                }
                String fileName = str + ".jpg";
                File targetFile = new File(getCameraFilePath() + fileName);
//                boolean renameFile =
                file.renameTo(targetFile);
//                if (renameFile) {
//                    chat_file.delete();
//                }
                saveImageFile(targetFile.getPath(), fileName, ll_upload, wr);
                return null;
            }
        });
    }

    private String uploadedFiles(LinearLayout ll_upload) {
        StringBuilder sbFiles = new StringBuilder();
        View v;
        String tmpFileName;
        for (int i = 0; i < ll_upload.getChildCount(); i++) {
            v = ll_upload.getChildAt(i).findViewById(R.id.upload_item_image);
            tmpFileName = v.getTag(R.string.fileName).toString();
            sbFiles.append(tmpFileName).append(",");
        }
        return sbFiles.toString();
    }

    public void saveImageFile(final String filePath, final String showName, final LinearLayout ll_upload, final WeakReference<? extends BaseActivity> wr) {
        final BaseActivity activity = wr.get();
        int pos = showName.lastIndexOf(".");
        String fileType = showName.substring(pos + 1, showName.length());
        final Bitmap bitmap = getFileIco(filePath, fileType, activity);
        View uploadImageContainer = LayoutInflater.from(activity).inflate(
                R.layout.trainnet_item_uploadview, null);
        ImageView deleteMark = (ImageView) uploadImageContainer.findViewById(R.id.delete_markView);
        uploadImageContainer.setTag(ll_upload.getChildCount());
        deleteMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.parseInt(((View) v.getParent()).getTag().toString());
                removePos(pos, ll_upload);
            }
        });
        ImageView uploadImage = (ImageView) uploadImageContainer.findViewById(R.id.upload_item_image);
        uploadImage.setTag(R.string.fileType, getFileType(fileType));
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = Integer.parseInt(v.getTag(R.string.fileType).toString());
                if (type == 1) {
                    try {
                        Screen screen = Scale.getScreen(wr);
                        Bitmap bitmap = BitmapUtil.getBitmapByCompress(filePath, screen.getPxWidth(), screen.getPxHeight());
//                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        DialogUtil.getInstance().showImageDialog(wr, bitmap, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        TextView uploadText = (TextView) uploadImageContainer.findViewById(R.id.upload_item_name);
        uploadText.setText(showName);
        uploadImage.setTag(R.string.fileName, showName);
        uploadImage.setTag(R.string.filePath, filePath);
        uploadImage.setImageBitmap(bitmap);
        ll_upload.addView(uploadImageContainer);
    }

    private Bitmap getFileIco(String filePath, String fileType, BaseActivity activity) {
        Bitmap bitmap;
        switch (fileType.toLowerCase()) {
            case "zip":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_zip);
                break;
            case "rar":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_rar);
                break;
            case "doc":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_doc);
                break;
            case "docx":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_doc);
                break;
            case "xls":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_xls);
                break;
            case "xlsx":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_xls);
                break;
            case "ppt":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_ppt);
                break;
            case "pptx":
                bitmap = BitmapUtil.fromRes(activity.getResources(), R.drawable.trainnet_ppt);
                break;
            case "png":
            case "jpg":
            case "gif":
            case "bmp":
            default:
                int width = Scale.Dp2Px(activity, 60);
                bitmap = BitmapUtil.getBitmapByCompress(filePath, width, width);
                break;
        }
        return bitmap;
    }

    private int getFileType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "zip":
                return 0;
            case "rar":
                return 0;
            case "doc":
                return 0;
            case "docx":
                return 0;
            case "xls":
                return 0;
            case "xlsx":
                return 0;
            case "ppt":
                return 0;
            case "pptx":
                return 0;
            case "png":
            case "jpg":
            case "gif":
            case "bmp":
            default:
                break;
        }
        return 1;
    }

    private void removePos(int pos, LinearLayout ll_upload) {
        int cPos;
        //最后一个直接删除
        if (pos == ll_upload.getChildCount() - 1) {
            ll_upload.removeViewAt(pos);
        } else {
            for (int i = pos + 1; i < ll_upload.getChildCount(); i++) {
                cPos = Integer.parseInt(ll_upload.getChildAt(i).getTag().toString());
                ll_upload.getChildAt(i).setTag(cPos - 1);
            }
            ll_upload.removeViewAt(pos);
        }
    }

}
