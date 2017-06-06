package com.zhongdasoft.svwtrainnet.wizlong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.zhongdasoft.svwtrainnet.R;

public class MainActivity extends ReactActivity implements View.OnClickListener {

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
//    @Override
//    protected String getMainComponentName() {
//        return "DZElearning";
//    }

    private Button btnVCT;
    private Button btnSchedule;
    private Button btnScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnVCT = (Button) this.findViewById(R.id.btnVCT);
        btnSchedule = (Button) this.findViewById(R.id.btnSchedule);
        btnScore = (Button) this.findViewById(R.id.btnScore);

        btnVCT.setOnClickListener(this);
        btnSchedule.setOnClickListener(this);
        btnScore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnVCT: {
                gotVCT();
            }
            break;
            case R.id.btnSchedule: {
                courseAction();
            }
            break;
            case R.id.btnScore: {
                dafeng();
            }
            break;
        }

    }

    private void gotVCT() {
        Intent intent = new Intent();
        intent.setClass(this, VCTActivity.class);

        Bundle subBundle = new Bundle();
        subBundle.putString("action", "vct");
        //meetingId=5d742b07c49149e6b1fde6362e16e1f7
        //pData=UwAwADAAMAAwADIAMAA1ADYAMAA7AGOExZayczsANQBkADcANAAyAGIAMAA3AGMANAA5ADEANAA5AGUANgBiADEAZgBkAGUANgAzADYAMgBlADEANgBlADEAZgA3ADsACk53bSdZF099bGaPOwA7YM9+BnQ7ADIAMAAxADcALQAwADMALQAwADcAIAAxADUAOgAzADAAOgAwADgAOwAwADsANwA0ADMAMAAwADAAMAAwADsAhVHokDsAMgAzADAAOQAwADIAMQA=
//        subBundle.putString("pData", "UwAwADAAMAAwADIAMAA1ADYAMAA7AGOExZayczsANQBkADcANAAyAGIAMAA3AGMANAA5ADEANAA5AGUANgBiADEAZgBkAGUANgAzADYAMgBlADEANgBlADEAZgA3ADsACk53bSdZF099bGaPOwA7YM9+BnQ7ADIAMAAxADcALQAwADMALQAwADcAIAAxADUAOgAzADAAOgAwADgAOwAwADsANwA0ADMAMAAwADAAMAAwADsAhVHokDsAMgAzADAAOQAwADIAMQA=");
//        subBundle.putString("meetingId", "5d742b07c49149e6b1fde6362e16e1f7");
        String pData = "UwAwADAAMAAxADIAMAAwADQANQA7APt8336hewZ0OwBiADYANwBlAGYANQA0ADgAMQA2ADkAOQA0ADgAZAA0ADkANQA1ADYAOQA2AGEAMAA4ADQANAAzADMANwA2ADIAOwAKTndtJ1kXT31sZo87AFMAVgBXACWEAJVYVOVdLABTAFYAVwAgAFIAUwBTAEMAuk5YVDsAMgAwADEANwAtADAANAAtADEANAAgADEAMgA6ADUANAA6ADIAMwA7ADAAOwA3ADQAMwAwADAAMAAxADAAOwCFUeiQOwAyADMAMAAwADAAMAAxAA==";
        String meetingId = "b67ef548169948d4955696a084433762";
        subBundle.putString("pData", pData);
        subBundle.putString("meetingId", meetingId);
//        subBundle.putString("pData", "YQBkAG0AaQBuADsA9HatZEtt1Ys7AGYAMgBkAGEAOAAwADgAZgAyADMAMQA1ADQAZQA4AGEAYQA0AGIAMQAzAGEANAAzADUAZgBhAGMANQA5ADkAMQA7ABdTrE6WmX1sfWxmj%2B5PBnQJZ1CWbFH4UzsAVVyFU89%2BBnQsAIVRrYsIXjsAMgAwADEANwAtADAAMgAtADIANAAgADIAMQA6ADAAMwA6ADQAMwA7ADAAOwA3ADQAMwAwADEAMAAxADAAOwAXU7ll&PT=YQBkAG0AaQBuADsA9HatZEtt1Ys7AGYAMgBkAGEAOAAwADgAZgAyADMAMQA1ADQAZQA4AGEAYQA0AGIAMQAzAGEANAAzADUAZgBhAGMANQA5ADkAMQA7ABdTrE6WmX1sfWxmj%2B5PBnQJZ1CWbFH4UzsAVVyFU89%2BBnQsAIVRrYsIXjsAMgAwADEANwAtADAAMgAtADIANAAgADIAMQA6ADAAMwA6ADQAMwA7ADAAOwA3ADQAMwAwADEAMAAxADAAOwAXU7ll");
//        subBundle.putString("meetingId", "1523");

        Bundle bundle = new Bundle();
        bundle.putBundle("action", subBundle);

        intent.putExtra("bundle", bundle);
        this.startActivity(intent);
    }

    private void dafeng() {

        Intent intent = new Intent();
        intent.setClass(this, VCTActivity.class);

        Bundle subBundle = new Bundle();
        subBundle.putString("action", "score");

        Bundle bundle = new Bundle();
        bundle.putBundle("action", subBundle);

        intent.putExtra("bundle", bundle);
        this.startActivity(intent);
    }

    private void courseAction() {
        Intent intent = new Intent();
        intent.setClass(this, VCTActivity.class);

        Bundle subBundle = new Bundle();
        subBundle.putString("action", "scorm");
        subBundle.putString("courseId", "1583");
        subBundle.putString("pData", "UwAwADAAMAAwADYAMwA4ADYANAA7ADEANQA4ADMAOwAyADAAMQA3AC0AMAAyAC0AMgA0ACAAMgAwADoAMQA4ADoANQA1AA==");
        //"UwAwADAAMAAwADYAMwA4ADYANAA7ADEAMgA1ADMAOwAyADAAMQA3AC0AMAAyAC0AMQAyACAAMQAwADoAMwA3ADoANQAzAA==");

        Bundle bundle = new Bundle();
        bundle.putBundle("action", subBundle);

        intent.putExtra("bundle", bundle);
        this.startActivity(intent);
    }
}
