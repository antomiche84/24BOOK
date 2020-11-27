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

public class StateSplashActivity extends Activity {


        private com.example.a24book.StateSplashActivity.UiHandler mHandler;
        private static final int GO_AHEAD_WHAT = 1;
        private static final long MIN_WAIT_INTERVAL = 1500L;
        private static final long MAX_WAIT_INTERVAL = 6000L;
        private boolean mIsDone;


        static class UiHandler extends Handler {
            private static final String TAG_LOG = com.example.a24book.StateSplashActivity.class.getName();
            private WeakReference<com.example.a24book.StateSplashActivity> mActivityRef;

            public  UiHandler(final com.example.a24book.StateSplashActivity srcActivity) {
                this.mActivityRef = new WeakReference<com.example.a24book.StateSplashActivity>(srcActivity);
            }

            @Override
            public void handleMessage(Message msg) {
                final com.example.a24book.StateSplashActivity srcActivity = this.mActivityRef.get();
                if (srcActivity == null) {
                    Log.d(TAG_LOG, "Reference to StateSplashActivity lost!");
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
            mHandler = new com.example.a24book.StateSplashActivity.UiHandler(this);

            // inserimento codice libro
            if (savedInstanceState != null) {
                this.mStartTime = savedInstanceState.getLong(START_TIME_KEY);
            }
            //fine codice libro

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


   // da qui inizia il codice copiato dal libro

    private static final String IS_DONE_KEY =
            "uk.co.maxcarli.apobus.key.IS_DONE_KEY";
    private static final String START_TIME_KEY =
            "uk.co.maxcarli.apobus.key.START_TIME_KEY";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DONE_KEY, mIsDone);
        outState.putLong(START_TIME_KEY, mStartTime);
    }

    private long mStartTime = -1L;
    @Override
    protected void onStart() {
        super.onStart();
        if (mStartTime == -1L) {
            mStartTime = SystemClock.uptimeMillis();
        }
        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mIsDone = savedInstanceState.getBoolean(IS_DONE_KEY);
    }

}
