package com.zhongdasoft.svwtrainnet.module.more;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.BitmapUtil;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.widget.zxingcamera.CameraManager;
import com.zhongdasoft.svwtrainnet.widget.zxingdecoding.CaptureActivityHandler;
import com.zhongdasoft.svwtrainnet.widget.zxingdecoding.InactivityTimer;
import com.zhongdasoft.svwtrainnet.widget.zxingview.ViewfinderView;
import com.zhongdasoft.svwtrainnet.wizlong.VCTActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Vector;

public class ScanActivity extends BaseActivity implements Callback {
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    // private Integer returnCode;
    // private String message;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private String scanResult;
    private HashMap<String, Object> scanList;
    private String url;
    private Drawable image;
    public Handler scanHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (scanList.size() > 0) {
                image = null;
                String text = "";
                if ("1".equals(scanList.get("ReturnCode").toString())) {
                    boolean isSimple = false;
                    boolean isWebLogin = false;
                    if (scanList.containsKey("Url") && null != scanList.get("Url").toString()) {
                        url = scanList.get("Url").toString();
                        if (url.startsWith("url:")) {
                            url = url.substring(3, url.length());
                        }
                        url = url + "&token=" + MySharedPreferences.getInstance().getAccessToken(ScanActivity.this);
                    }
                    if (scanList.containsKey("PicUrl") && null != scanList.get("PicUrl").toString()) {
                        image = BitmapUtil.bitmap2Drawable((Bitmap) scanList.get("PicUrl"));
                    }
                    if (scanList.containsKey("Title") && null != scanList.get("Title")) {
                        text = scanList.get("Title").toString();
                        isSimple = false;
                    }
                    if (scanList.containsKey("Text") && null != scanList.get("Text")) {
                        text = scanList.get("Text").toString();
                        isSimple = true;
                    }
                    String buttonText = null;
                    if (scanList.containsKey("ButtonText") && null != scanList.get("ButtonText")) {
                        buttonText = scanList.get("ButtonText").toString();
                    }
                    if (scanList.containsKey("Message") && null != scanList.get("Message")) {
                        String message = scanList.get("Message").toString();
                        if ("WebLogin".equals(message)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("scan", "允许网页登录");
                            bundle.putString("item", url);
                            readyGo(TvContentActivity.class, bundle);
                            isWebLogin = true;
                        } else {
                            isWebLogin = false;
                        }
                    }
                    if (!isWebLogin) {
                        if (isSimple) {
                            DialogUtil.getInstance().showScanDialog(new WeakReference<>(ScanActivity.this), getResources().getString(R.string.tips), text);
                        } else {
                            DialogUtil.getInstance().showScanDialog(new WeakReference<>(ScanActivity.this), text, text, image, url,
                                    buttonText);
                        }
                    }
                } else {
                    if ("0".equals(scanList.get("ReturnCode").toString())) {
                        Boolean openFeature = Boolean.parseBoolean(scanList.get("OpenFeature").toString());
                        if (openFeature) {
                            Bundle bundle = new Bundle();
                            String featureName = scanList.get("FeatureName").toString();
                            String Parameter = scanList.get("Parameter").toString();
                            if ("wizlong".equals(featureName)) {
                                bundle.putString("data", Parameter);
                                readyGo(VCTActivity.class, bundle, "bundle");
                            }
                        } else {
                            String title = (null == scanList.get("Title") ? getResources().getString(R.string.tips) : scanList.get("Title").toString());
                            text = scanList.get("Message").toString();
                            url = scanList.get("Url").toString();
                            //此处测试用????
                            if ("疯狂打地鼠".equals(title)) {
                                Bundle bundle = new Bundle();
                                bundle.putString("scan", title);
                                bundle.putString("item", url);
                                readyGo(TvContentActivity.class, bundle);
                            } else {
                                DialogUtil.getInstance().showScanDialog(new WeakReference<>(ScanActivity.this), title, text, null, url, getResources().getString(R.string.readAll));
                            }
                        }
                    } else {
                        text = scanList.get("Message").toString();
                        DialogUtil.getInstance().showScanDialog(new WeakReference<>(ScanActivity.this), getResources().getString(R.string.tips), text);
                    }
                }
            }
            return false;
        }
    });
    private WeakReference<? extends BaseActivity> wr;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

//        LinearLayout ll = (LinearLayout) findViewById(R.id.include_title);
//        ll.setBackgroundColor(getResources().getColor(R.color.transparent));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_scan;
    }

    @Override
    protected int getMTitle() {
        return R.string.menu_scan;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        scanResult = result.getText();
        if (scanResult.equals("")) {
            ToastUtil.show(ScanActivity.this, "Scan failed!");
        } else {
            ScanBarcode();
        }
        // MipcaActivityCapture.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(wr, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public void continuePreview() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.qrcode_found);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private void ScanBarcode() {
        new Thread(new ScanThread()).start();
    }

    private class ScanThread implements Runnable {
        @Override
        public void run() {
            scanList = TrainNetWebService.getInstance().ScanBarcode(ScanActivity.this, scanResult);
            if (scanList.containsKey("PicUrl")) {
                if (null != scanList.get("PicUrl")) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(ScanActivity.this).load(scanList.get("PicUrl").toString()).asBitmap().centerCrop().into(500, 500).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scanList.put("PicUrl", bitmap);
                } else {
                    scanList.put("PicUrl", null);
                }
            }
            scanHandler.sendEmptyMessage(0);
        }
    }
}
