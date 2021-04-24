#include <jni.h>
#include <string>
#include <thread>
#include <unistd.h>
#include <android/log.h>

bool finished = false;
char tag[64];

void run()
{
    int count = 0;
    while (!finished) {
        __android_log_print(ANDROID_LOG_INFO, tag, "%d", count++);
        sleep(1);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeStart(
        JNIEnv* env,
        jobject /* this */,
        jstring s) {
    int n = env->GetStringLength(s);
    const char *str = env->GetStringUTFChars(s, 0);
    strncpy(tag, str, 64);
    tag[63] = '\0';
    env->ReleaseStringUTFChars(s, str);
    std::thread t(run);
    t.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFinish(
    JNIEnv* env,
    jobject /* this */) {
    __android_log_print(ANDROID_LOG_INFO, tag, "finish");
    finished = true;
}
