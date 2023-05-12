#!/bin/bash

date

adb root
adb wait-for-device

adb shell am clear-exit-info

#PKGS=`adb shell pm list packages | sed -e 's/^package://g'`
PKGS=`adb shell pm query-activities -a android.intent.action.MAIN -c android.intent.category.LAUNCHER | grep "        packageName=" | sed -e 's/        packageName=//'`

while true
do
  for PKG in $PKGS
  do
    echo "Launching $PKG"
    adb shell "monkey -p $PKG -c android.intent.category.LAUNCHER 1" > /dev/null 2> /dev/null
    sleep 1

    adb shell input keyevent KEYCODE_HOME
    sleep 1
  done
done

date

adb shell dumpsys activity exit-info > exit-info.log
adb logcat -d > logcat.log
adb bugreport
