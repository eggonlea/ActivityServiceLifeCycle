#include <fcntl.h>
#include <fstream>
#include <jni.h>
#include <pthread.h>
#include <string>
#include <thread>
#include <unistd.h>
#include <android/log.h>
#include <sys/file.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>

#define LOOP_MAX 1000000000
#define FOOTPRINT __android_log_print(ANDROID_LOG_INFO, tag, "--- ===== %s() ===== ---", __func__)

bool finished = false;
char tag[64] = "NativeThread";
int fd = -1;
int loop = LOOP_MAX;

void testCgroup()
{
  FOOTPRINT;
  int uid=0, pid=0;
  int self = getpid();
  std::ifstream cgroup("/proc/self/cgroup");
  std::string line;
  while (std::getline(cgroup, line)) {
    __android_log_print(ANDROID_LOG_INFO, tag, "%s", line.c_str());
    if (line.find("uid") != std::string::npos) {
      sscanf(line.c_str(), "0::/uid_%d/pid_%d", &uid, &pid);
      __android_log_print(ANDROID_LOG_INFO, tag, "pid=%d, uid/pid=%d/%d", self, uid, pid);
    }
  }

  if (uid > 0 && pid > 0) {
    int ret;
    char uiddir[1024];
    char piddir[1024];
    snprintf(uiddir, 1024, "/sys/fs/cgroup/uid_%d", uid);
    snprintf(piddir, 1024, "/sys/fs/cgroup/uid_%d/pid_%d", uid, pid);
    for (int i = 0; i < 3; i++) {
      ret = mkdir(uiddir, 0750);
      __android_log_print(ANDROID_LOG_INFO,
                          tag,
                          "mkdir#%d(%s, 0750) returns %d (errno=%d, EEXIST=%d)",
                          i,
                          uiddir,
                          ret,
                          errno,
                          EEXIST);
      ret = mkdir(piddir, 0750);
      __android_log_print(ANDROID_LOG_INFO,
                          tag,
                          "mkdir#%d(%s, 0750) returns %d (errno=%d, EEXIST=%d)",
                          i,
                          piddir,
                          ret,
                          errno,
                          EEXIST);
    }
  }
}

void testOverload(bool *b) {
  *b = true;
  __android_log_print(ANDROID_LOG_INFO, tag, "%s %u", "Calling bool function: ", *b);
}

void testOverload(uint32_t *u) {
  *u = 0x11;
  __android_log_print(ANDROID_LOG_INFO, tag, "%s %u", "Calling u32 function: ", *u);
}

void testOverload() {
  FOOTPRINT;
  bool b = false;
  uint32_t u = 0;
  testOverload(&b);
  testOverload(&u);
  __android_log_print(ANDROID_LOG_INFO, tag, "b=%u u=%u", b, u);

  testOverload((uint32_t *)(&b));
  testOverload((bool *)(&u));
  __android_log_print(ANDROID_LOG_INFO, tag, "b=%u u=%u", b, u);
}

void testFileLock() {
  FOOTPRINT;
  struct flock lock;
  lock.l_type = F_WRLCK;
  lock.l_start = 0;
  lock.l_whence = SEEK_SET;
  lock.l_len = 0;
  __android_log_print(ANDROID_LOG_INFO, tag, "locking %d", fd);
  int ret = fcntl(fd, F_SETLKW, &lock);
  __android_log_print(ANDROID_LOG_INFO, tag, "locked %d return %d", fd, ret);
  if (ret < 0)
    __android_log_print(ANDROID_LOG_INFO, tag, "(%d) %s", errno, strerror(errno));
}

void testProcLocks() {
  FOOTPRINT;
  int pfd = open("/proc/locks", O_RDONLY);
  if (pfd < 0) {
    __android_log_print(ANDROID_LOG_INFO, tag, "Failed to open /proc/locks. (%d) %s", errno, strerror(errno));
    return;
  }

#define N 102400
  int len;
  char orig[N];
  char buf[N];
  memset(orig, 0, N);
  memset(buf, 0, N);
  len = read(pfd, orig, N);
  for (int i=0; i<300; i++) {
    lseek(pfd, 0, SEEK_SET);
    int ret = read(pfd, buf, N);
    if (ret != len)
      __android_log_print(ANDROID_LOG_INFO,
                          tag,
                          "/proc/locks changed %d ==> %d",
                          len,
                          ret);
    for (int j = 0; j < N; j++) {
      if (orig[j] != buf[j]) {
        __android_log_print(ANDROID_LOG_INFO,
                            tag,
                            "/proc/locks changed at %d / %d",
                            j,
                            len);
        break;
      }
    }
    __android_log_print(ANDROID_LOG_INFO, tag, "/proc/locks %d", len);
    sleep(1);
  }
  close(pfd);
}

void runLoop() {
  FOOTPRINT;
  int count = 0;
  while (count < loop && !finished) {
    sleep(1);
    __android_log_print(ANDROID_LOG_INFO, tag, "%d (%d)", count++, loop);
  }
  finished = true;
}

void run() {
  __android_log_print(ANDROID_LOG_INFO, tag, "NativeThread start");
  finished = false;

  if (fd >= 0) testFileLock();

  runLoop();

  if (fd >= 0) {
    __android_log_print(ANDROID_LOG_INFO, tag, "release lock %d", fd);
    close(fd);
    fd = -1;
  }
}

void housekeeping() {
  while (true) {
    int ret;
    int pid = wait(&ret);
    if (pid > 0)
      __android_log_print(ANDROID_LOG_INFO,
                          tag,
                          "pid %d finished with %d",
                          pid,
                          ret);
    sleep(1);
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeTestCgroup(
    JNIEnv* env,
    jobject /* this */) {
  testCgroup();
  int pid = fork();
  __android_log_print(ANDROID_LOG_INFO, tag, "fork %d", pid);

  if (pid == 0) {
    setsid();
    testCgroup();
    execl("/system/bin/true", "/system/bin/true", (char *) nullptr);
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeTestOverload(
    JNIEnv* env,
    jobject /* this */) {
  testOverload();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeTestProcLocks(
    JNIEnv* env,
    jobject /* this */) {
  std::thread t(testProcLocks);
  t.detach();
}

extern "C" JNIEXPORT int JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeOpenFD(
    JNIEnv* env,
    jobject /* this */,
    jstring path) {
  const char* str = env->GetStringUTFChars(path, 0);
  char fname[1024];
  snprintf(fname, 1024, "%s/%s", str, "file_lock");
  fd = open(fname, O_CREAT | O_RDWR, S_IRWXU | S_IRWXG | S_IRWXO);
  __android_log_print(ANDROID_LOG_INFO,
                      tag,
                      "open %d (%s) %d %s",
                      fd,
                      fname,
                      errno,
                      strerror(errno));
  return fd;
}

extern "C" JNIEXPORT int JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeGetFD(
    JNIEnv* env,
    jobject /* this */) {
  int n = (fd == -1 ? -1 : dup(fd));
  __android_log_print(ANDROID_LOG_INFO, tag, "%d = GetFD(%d)", n, fd);
  return n;
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeSetFD(
    JNIEnv* env,
    jobject /* this */,
    jint n) {
  fd = dup(n);
  __android_log_print(ANDROID_LOG_INFO, tag, "%d = SetFD(%d)", fd, n);
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeInit(
    JNIEnv* env,
    jobject /* this */,
    jstring s) {
  int n = env->GetStringLength(s);
  const char *str = env->GetStringUTFChars(s, 0);
  strncpy(tag, str, 64);
  tag[63] = '\0';
  env->ReleaseStringUTFChars(s, str);
  std::thread t(housekeeping);
  t.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeStart(
    JNIEnv* env,
    jobject /* this */) {
  loop = LOOP_MAX;
  std::thread t(run);
  t.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFork(
    JNIEnv* env,
    jobject /* this */) {
  int pid = fork();
  __android_log_print(ANDROID_LOG_INFO, tag, "fork %d", pid);

  if (pid == 0) {
    loop = 10;
    setsid();
    sleep(1);
    run();
    execl("/system/bin/true", "/system/bin/true", (char *) nullptr);
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFinish(
    JNIEnv* env,
    jobject /* this */) {
  __android_log_print(ANDROID_LOG_INFO, tag, "finish");
  finished = true;
}

static void lockLocal(const char *path) {
  __android_log_print(ANDROID_LOG_INFO, tag, "Create %s", path);
  int fd = open(path, O_CREAT | O_WRONLY, S_IRWXU | S_IRWXG | S_IRWXO);
  if (fd < 0) {
    __android_log_print(ANDROID_LOG_INFO, tag, "Failed to open file");
    return;
  }
  __android_log_print(ANDROID_LOG_INFO, tag, "Locking %d %s", fd, path);
  flock(fd, LOCK_EX);
  __android_log_print(ANDROID_LOG_INFO, tag, "Locked %s", path);
}

static void *lockRemote(void *path) {
  __android_log_print(ANDROID_LOG_INFO, tag, "Sleep 1s");
  sleep(1);
  lockLocal((const char *)path);
  return NULL;
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeLockLocal(
    JNIEnv* env,
    jobject /* this */,
    jstring path) {
  lockLocal(env->GetStringUTFChars(path, 0));
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeLockRemote(
    JNIEnv* env,
    jobject /* this */,
    jstring path) {
  const char* str = env->GetStringUTFChars(path, 0);

  pthread_t thread;
  __android_log_print(ANDROID_LOG_INFO, tag, "Create new thread");
  pthread_create(&thread, NULL, lockRemote, (void *)str);
  __android_log_print(ANDROID_LOG_INFO, tag, "Detach new thread");
  pthread_detach(thread);
}