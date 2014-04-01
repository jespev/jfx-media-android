#ifndef LOGGING_H
#define	LOGGING_H

#include <android/log.h>

#ifdef	__cplusplus
extern "C" {
#endif

#define TAG "FXActivity native"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, __VA_ARGS__))
#define LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, __VA_ARGS__))


#ifdef	__cplusplus
}
#endif

#endif	/* LOGGING_H */

