#ifndef DALVIKINPUT_H
#define	DALVIKINPUT_H

#include <jni.h>
#include <android/native_window_jni.h>
#include "input/LensInput.h"
#include <linux/input.h>

#ifdef	__cplusplus
extern "C" {
#endif 

    
ANativeWindow   *android_getNativeWindow(JNIEnv *env);
const char      *android_getDataDir(JNIEnv *env);
void            lens_input_shutdown(JNIEnv *env);

#ifdef	__cplusplus
}
#endif

#endif	/* DALVIKINPUT_H */