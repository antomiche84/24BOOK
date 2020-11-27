package com.example.a24book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;

public class NoLeakSplashActivity extends Activity {
    private UiHandler mHandler;
    private static final int GO_AHEAD_WHAT = 1;
    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 6000L;
    private long mStartTime;
    private boolean mIsDone;


    static class UiHandler extends Handler {
        private static final String TAG_LOG = NoLeakSplashActivity.class.getName();
        private WeakReference<NoLeakSplashActivity> mActivityRef;

        public  UiHandler(final NoLeakSplashActivity srcActivity) {
            this.mActivityRef = new WeakReference<NoLeakSplashActivity>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final NoLeakSplashActivity srcActivity = this.mActivityRef.get();
            if (srcActivity == null) {
                Log.d(TAG_LOG, "Reference to NoLeakSplashActivity lost!");
                return;
            }
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis() - srcActivity.mStartTime;
                    if (elapsedTime >= MIN_WAIT_INTERVAL && !srcActivity.mIsDone) {
                        srcActivity.mIsDone = true;
                        srcActivity.goAhead();
                    }
                    break;
            }
        }
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        // We initialize the Handler
        mHandler = new UiHandler(this);
    }

    private void goAhead() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
