package com.lilioss.lifecycle.library;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.util.List;

public class JavaThread extends Thread {
  public interface JavaThreadCallback {
    void onCallback(String text);
  }

  // Authority of the method in Clock's content provider to get the bedtime schedule.
  private final String AUTHORITY = "com.lilioss.simpleactivity.provider";

  // Name of the method in Clock's content provider to get the bedtime schedule.
  private final String METHOD_NAME = "get_simple_info";

  private final String tag;
  private int interval = 1000;
  private Context context = null;
  private boolean finished = false;
  private boolean workload = false;
  private boolean clock = false;
  private JavaThreadCallback callback = null;

  public JavaThread(String s) {
    tag = "LifeCycle: Java Thread_" + s;
  }

  public void setInterval(int i) {
    interval = i;
  }
  public void setContext(Context cntx) {
    context = cntx;
  }

  public void setCallback(JavaThreadCallback cb) {
    callback = cb;
  }

  public void enableWorkload() {
    workload = true;
  }

  public void enableContent() {
    clock = true;
  }

  @RequiresApi(api = VERSION_CODES.Q)
  @Override
  public void run() {
    Log.i(tag, "JavaThread start");
    super.run();
    int i = 0;
    while (!finished) {
      SystemClock.sleep(interval);
      Log.i(tag, "" + i++);
      if (context != null && workload) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listAppInfo = pm.getInstalledApplications(0);
        List<PackageInfo> listPkgInfo = pm.getInstalledPackages(0);
        Log.i(tag, "Apps=" + listAppInfo.size() + " Pkgs=" + listPkgInfo.size());
      }

      if (context != null && clock) {
        Uri uri = Uri.parse("content://" + AUTHORITY);
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(uri);
        Bundle bundle = null;
        try {
          bundle = client.call(METHOD_NAME, null, null);
        } catch (RemoteException e) {
          e.printStackTrace();
        }

        if (callback != null) {
          if (bundle == null) {
            callback.onCallback("null bundle");
          } else {
            callback.onCallback("simple_int = " + bundle.getInt("simple_int"));
            callback.onCallback("simple_long = " + bundle.getLong("simple_long"));
            callback.onCallback("calling_package = " + bundle.getString("calling_package"));
          }
        }
      }

      if (callback != null) {
        callback.onCallback("callback");
      }
    }
  }

  public void finish() {
    Log.i(tag, "finish");
    finished = true;
  }
}
