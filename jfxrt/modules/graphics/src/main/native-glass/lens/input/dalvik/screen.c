#if (defined(ANDROID_NDK) && defined(DALVIK_VM))

#include "LensCommon.h"
#include "../../wm/LensWindowManager.h"
#include "dalvikInput.h"

static NativeScreen localScreen;
static int windowIndex = 1;

NativeScreen lens_screen_initialize(JNIEnv *env) {

    GLASS_LOG_FINEST("Android/Lens screen initialize");
    if (localScreen) {
        free(localScreen);
    }
    ANativeWindow *androidWindow = android_getNativeWindow(env);    
    if (!androidWindow) {
        return 0;
    }
    localScreen = (NativeScreen)malloc(sizeof(struct _NativeScreen));
    int32_t width = ANativeWindow_getWidth(androidWindow);
    int32_t height = ANativeWindow_getHeight(androidWindow);
    localScreen->width = width;
    localScreen->height = height;
    localScreen->visibleWidth = width;
    localScreen->visibleHeight = height;
    localScreen->visibleX = 0;
    localScreen->visibleY = 0;
    localScreen->x = 0;
    localScreen->y = 0;
    localScreen->depth = 24; //????? Samsung has 16

    // convert pixels/mm to pixels/inch
    localScreen->resolutionX = 100;
    localScreen->resolutionY = 100;

    GLASS_LOG_FINEST("Screen [%d, %d]", width, height);
    return localScreen;
}

jboolean glass_application_initialize(JNIEnv *env) {
    //nothing to do
    return JNI_TRUE;
}

jboolean glass_window_setAlpha(JNIEnv *env, NativeWindow window, float alpha) {
    window->alpha = alpha;
    lens_wm_repaint(env, window);
    return JNI_TRUE;
}

LensResult glass_view_PlatformViewData_create(NativeView view) {
    view->data = NULL;
    return LENS_OK;
}

LensResult glass_view_PlatformViewRelease(JNIEnv *env, NativeView view) {
    // No data to free
    return LENS_OK;
}

LensResult glass_window_PlatformWindowData_create(JNIEnv *env,
                                                  NativeWindow window) {

    window->id = windowIndex++;
    window->data = NULL; //no platfrom specific data

    return LENS_OK;
}

LensResult glass_window_PlatformWindowRelease(JNIEnv *env, NativeWindow window) {
    // No data to free
    return LENS_OK;
}

void *glass_window_getPlatformWindow(JNIEnv *env, NativeWindow window) {
    return android_getNativeWindow(env);
}

void lens_platform_shutdown(JNIEnv *env) {
    lens_input_shutdown(env);
}

void glass_screen_clear() {
    // NOOP
}

void glass_pixel_attachIntBuffer(JNIEnv *env, jint *src,
                                 NativeWindow window,
                                 jint width, jint height, int offset) {
   GLASS_LOG_FINE("androidScreen: glass_pixel_attachIntBuffer not implemented!");
}

jboolean glass_screen_capture(jint x,
                              jint y,
                              jint width,
                              jint height,
                              jint *pixels) {
   GLASS_LOG_FINE("androidScreen: glass_screen_capture not implemented!");
}

LensResult lens_platform_windowMinimize(JNIEnv *env,
                                        NativeWindow window,
                                        jboolean toMinimize) {
    //noop for fb
    return LENS_OK;
}

LensResult lens_platform_windowSetVisible(JNIEnv *env,
                                        NativeWindow window,
                                        jboolean visible) {
    return LENS_OK;
}

#endif /* ANDROID_NDK */
