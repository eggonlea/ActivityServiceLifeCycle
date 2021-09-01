package com.lilioss.lifecycle.library;

public class NativeThread {
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private final String tag;

  public NativeThread(String s) {
    tag = "LifeCycle: std::thread_" + s;
  }

  public void start() {
    nativeStart(tag, false, false);
  }

  public void start(boolean cgroup, boolean flock) {
    nativeStart(tag, cgroup, flock);
  }

  public void finish() {
    nativeFinish();
  }

  /**
   * A native method that is implemented by the 'native-lib' native library, which is packaged with
   * this application.
   */
  private native void nativeStart(String s, boolean cgroup, boolean flock);
  private native void nativeFinish();
}
