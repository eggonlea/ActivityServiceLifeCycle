package com.lilioss.lifecycle.simpleactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class SimpleBroadcastReceiver extends BroadcastReceiver {

  private final static String TAG = "LifeCycle: BroadcastReceiver";

  private SimpleActivity mSimpleActivity = null;

  public SimpleBroadcastReceiver() {
    mSimpleActivity = null;
  }

  public SimpleBroadcastReceiver(SimpleActivity activity) {
    mSimpleActivity = activity;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "onReceive: " + intent);
    if (mSimpleActivity != null) {
      if (mSimpleActivity.simpleManager != null) {
        try {
          Log.i(TAG, mSimpleActivity.simpleManager.basicTypes(1,
              2,
              true,
              3,
              4,
              "abc"));
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
