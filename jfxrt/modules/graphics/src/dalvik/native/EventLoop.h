#ifndef EVENTLOOP_H
#define	EVENTLOOP_H

#ifdef	__cplusplus
extern "C" {
#endif

#include <jni.h>
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "Events.h"    
        
#define TRUE    1
#define FALSE   0

typedef struct {
    JavaVM          *jvm;
    size_t          size;
    int             running;
    pthread_t       thread;
    pthread_mutex_t mtx;
    pthread_cond_t  cv;
    Event           head;
    Event           tail;
    int     (*start)(JNIEnv *);
    void    (*stop)();
    void    *(*loop)(void*);
    int     (*push)(Event);
    Event   (*pop)();
    void    (*process)(JNIEnv *, Event);
} _EventQ;    
typedef _EventQ* EventQ;

EventQ eventq_getInstance();

#ifdef	__cplusplus
}
#endif

#endif	/* EVENTLOOP_H */