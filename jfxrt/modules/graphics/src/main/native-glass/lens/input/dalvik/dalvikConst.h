#ifndef DALVIKCONST_H
#define	DALVIKCONST_H

#ifdef	__cplusplus
extern "C" {
#endif 

#define RGBA_8888 1
#define RGBX_8888 2
#define RGB_888   3
#define RGB_565   4

#define TOUCH_ACTION_STILL         -1
#define TOUCH_ACTION_DOWN           0
#define TOUCH_ACTION_UP             1
#define TOUCH_ACTION_MOVE           2
#define TOUCH_ACTION_CANCEL         3
#define TOUCH_ACTION_OUTSIDE		4
#define TOUCH_ACTION_POINTER_DOWN	5
#define TOUCH_ACTION_POINTER_UP     6

#define KEY_ACTION_DOWN     0
#define KEY_ACTION_UP       1
#define KEY_ACTION_MULTIPLE 2   
    
#ifdef	__cplusplus
}
#endif

#endif	/* DALVIKCONST_H */