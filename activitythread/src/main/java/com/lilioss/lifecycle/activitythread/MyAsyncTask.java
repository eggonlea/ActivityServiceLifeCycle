package com.lilioss.lifecycle.activitythread;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class MyAsyncTask extends AsyncTask {

  private final static String TAG="LifeCycleAsyncTask";
  boolean finished = false;

  @Override
  protected Object doInBackground(Object[] objects) {
    int i = 0;
    while (!finished) {
      SystemClock.sleep(1000);
      Log.i(TAG, "" + i++);
    }
    return null;
  }
}
