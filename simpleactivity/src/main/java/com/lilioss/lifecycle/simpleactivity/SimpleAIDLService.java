package com.lilioss.lifecycle.simpleactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleAIDLService extends Service {
  private final static String TAG = "LifeCycle: AidlSvc";

  public SimpleAIDLService() {
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    super.onCreate();
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return mSimpleManager;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
  }

  private final ISimpleAidlInterface.Stub mSimpleManager = new ISimpleAidlInterface.Stub() {
    @Override
    public String basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble,
        String aString) throws RemoteException {
      return "Int=" + anInt
          + " Long=" + aLong
          + " Boolean=" + aBoolean
          + " Float=" + aFloat
          + " Double=" + aDouble
          + " String=" + aString;
    }

    @Override
    public Bundle shareFile(Bundle bundle) throws RemoteException {
      try {
        Path path = Files.createTempFile("java_lock", null);
        FileInputStream fin = new FileInputStream(path.toFile());
        FileLock lock = fin.getChannel().lock();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }
  };
}