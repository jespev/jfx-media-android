#if (defined(ANDROID_NDK) && defined(DALVIK_VM))

#include <android/keycodes.h>
#include "dalvikConst.h"
#include "dalvikUtils.h"
#include "com_sun_glass_events_TouchEvent.h"
#include "com_sun_glass_events_MouseEvent.h"
#include "com_sun_glass_events_KeyEvent.h"

int to_jfx_touch_action(int state) {
    switch (state) {
        case TOUCH_ACTION_DOWN:
        case TOUCH_ACTION_POINTER_DOWN:    
            return com_sun_glass_events_TouchEvent_TOUCH_PRESSED;
        case TOUCH_ACTION_UP:
        case TOUCH_ACTION_POINTER_UP:    
            return com_sun_glass_events_TouchEvent_TOUCH_RELEASED;
        case TOUCH_ACTION_MOVE:
            return com_sun_glass_events_TouchEvent_TOUCH_MOVED;
        case TOUCH_ACTION_CANCEL:
            return com_sun_glass_events_TouchEvent_TOUCH_RELEASED;                    
        case TOUCH_ACTION_STILL:
            return com_sun_glass_events_TouchEvent_TOUCH_STILL;
        default:
            return 0;
    }
}

int to_jfx_key_action(int action) {
    switch (action) {
        case KEY_ACTION_DOWN:
            return com_sun_glass_events_KeyEvent_PRESS;
        case KEY_ACTION_UP:
            return com_sun_glass_events_KeyEvent_RELEASE;
        case KEY_ACTION_MULTIPLE:
            return com_sun_glass_events_KeyEvent_TYPED;
    }
}

int to_linux_keycode(int androidKeyCode) {
    for (int i = 0; i < sizeof (keyMap); ++i) {
        if (keyMap[i].androidKC == androidKeyCode) {
            return keyMap[i].linuxKC;
        }
    }
    return KEY_RESERVED;
}


char *describe_surface_format(int f) {    
    switch (f) {
        case RGBA_8888:
            return "RGBA_8888";
        case RGBX_8888:
            return "RGBX_8888";
        case RGB_888:
            return "RGB_888";
        case RGB_565:
            return "RGB_565";
        default:
            return "UNKNOWN";
    }
}

char *describe_touch_action(int state) {
    switch (state) {
        case TOUCH_ACTION_DOWN:
            return "TOUCH_ACTION_DOWN";
        case TOUCH_ACTION_UP:
            return "TOUCH_ACTION_UP";
        case TOUCH_ACTION_MOVE:
            return "TOUCH_ACTION_MOVE";
        case TOUCH_ACTION_CANCEL:
            return "TOUCH_ACTION_CANCEL";
        case TOUCH_ACTION_OUTSIDE:
            return "TOUCH_ACTION_OUTSIDE";
        case TOUCH_ACTION_POINTER_DOWN:
            return "TOUCH_ACTION_POINTER_DOWN";
        case TOUCH_ACTION_POINTER_UP:
            return "TOUCH_ACTION_POINTER_UP";
        case TOUCH_ACTION_STILL:
            return "TOUCH_ACTION_STILL";
        default:
            return "TOUCH_ACTION_UNKNOWN";
    }
}

char *describe_key_action(int action) {
    switch(action) {
        case KEY_ACTION_DOWN:
            return "KEY_ACTION_DOWN";
        case KEY_ACTION_UP:
            return "KEY_ACTION_UP";
        case KEY_ACTION_MULTIPLE:
            return "KEY_ACTION_MULTIPLE";
    }
}

#endif