#include <jni.h>
#include <string>
#include <thread>
#include <unistd.h>
#include <android/log.h>

#define TAG "LifeCycleStdThread"

bool finished = false;

void run()
{
    int count = 0;
    while (!finished) {
        sleep(1);
        __android_log_print(ANDROID_LOG_INFO, TAG, "%d", count++);
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_lilioss_lifecycle_activitythread_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    std::thread t(run);
    t.detach();
    return env->NewStringUTF(hello.c_str());
}
