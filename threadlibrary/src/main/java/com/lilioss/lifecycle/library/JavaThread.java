package com.lilioss.lifecycle.library;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.List;

public class JavaThread extends Thread {

  private final String tag;
  private Context context = null;
  private boolean finished = false;
  private boolean workload = false;

  public JavaThread(String s) {
    tag = "LifeCycle: Java Thread_" + s;
  }

  public void setContext(Context cntx) {
    context = cntx;
  }

  public void enableWorkload() {
    workload = true;
  }

  @Override
  public void run() {
    Log.i(tag, "JavaThread start");
    super.run();
    int i = 0;
    while (!finished) {
      SystemClock.sleep(1000);
      Log.i(tag, "" + i++);
      if (context != null && workload) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listAppInfo = pm.getInstalledApplications(0);
        List<PackageInfo> listPkgInfo = pm.getInstalledPackages(0);
        Log.i(tag, "Apps=" + listAppInfo.size() + " Pkgs=" + listPkgInfo.size());
      }
    }
  }

  public void finish() {
    Log.i(tag, "finish");
    finished = true;
  }
}
