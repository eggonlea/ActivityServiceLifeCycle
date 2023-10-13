package com.lilioss.lifecycle.simpleactivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * A simple activity manipulating displays and listening to corresponding display events
 */
public class DisplayEventActivity extends Activity {
  private static final String TAG = DisplayEventActivity.class.getSimpleName();
  private DisplayManager mDisplayManager;
  private DisplayManager.DisplayListener mDisplayListener;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDisplayManager = getApplicationContext().getSystemService(DisplayManager.class);
    mDisplayListener = new DisplayManager.DisplayListener() {
      @Override
      public void onDisplayAdded(int displayId) {
        Log.d(TAG, "onDisplayAdded: " + displayId);
      }

      @Override
      public void onDisplayRemoved(int displayId) {
        Log.d(TAG, "onDisplayRemoved: " + displayId);
      }

      @Override
      public void onDisplayChanged(int displayId) {
        Log.d(TAG, "onDisplayChanged: " + displayId);
      }
    };
    Handler handler = new Handler(Looper.getMainLooper());
    mDisplayManager.registerDisplayListener(mDisplayListener, handler);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mDisplayManager.unregisterDisplayListener(mDisplayListener);
  }
}
