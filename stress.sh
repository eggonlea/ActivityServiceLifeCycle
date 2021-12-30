#!/bin/bash

adbroot.sh
adb logcat -c
#adb shell device_config put activity_manager_native_boot freeze_debounce_timeout 1000

II=0

while [ $II -lt 5000 ];
do
  echo "Iteration $II"
  #adb shell am start -n com.lilioss.lifecycle.simpleactivity/.SimpleActivity
  #adb shell am start -n com.lilioss.lifecycle.simpleactivity/.BackgroundActivity
  adb shell am start -n com.lilioss.lifecycle.simpleactivity/.BatteryActivity
  sleep 1
  adb shell am start -n com.android.settings/.homepage.SettingsHomepageActivity
  sleep 1
  adb shell am start -n com.google.android.deskclock/com.android.deskclock.DeskClock
  sleep 1
  adb shell am start -n com.google.android.calculator/com.android.calculator2.Calculator
  sleep 1
  adb shell input keyevent KEYCODE_HOME
  sleep 1

  PID1=`pid.sh lilioss`
  PID2=`pid.sh deskclock`
  PID3=`pid.sh com.android.settings`
  echo "pid $PID1 $PID2 $PID3"
  #adb logcat -d | grep -E "FAILED BINDER TRANSACTION|DeadObjectException|freeze binder"
  adb logcat -d | grep -E "FAILED BINDER TRANSACTION|DeadObjectException|freeze binder|29202" | grep -E "$PID1|$PID2|$PID3"
  if [ $? -eq 0 ];
  then
    echo "ERROR FOUND"
    adb logcat -d > logcat.log
    exit
  fi

  II=$((II + 1))
done
