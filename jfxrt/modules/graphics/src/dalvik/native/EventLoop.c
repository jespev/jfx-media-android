#include "EventLoop.h"
#include "logging.h"

#define LOG_EVENT(msg, event) \
    char *str = event_toString(event);          \
    LOGV(TAG, "%s%s", msg, str);   \
    free(str)

EventQ eventq;

int eventq_push(Event e) {
    pthread_mutex_lock(&eventq->mtx);
    if (eventq->size == 0) {
        eventq->head = e;
        eventq->tail = e;
    } else {
        eventq->head->prev = e;
        e->next = eventq->head;
        eventq->head = e;
    } 
    eventq->size++;
    pthread_cond_signal(&eventq->cv); 
    pthread_mutex_unlock(&eventq->mtx);
    return TRUE;
}

Event eventq_pop() {
    if (eventq->size == 0) {
        return NULL;
    }
    Event back = eventq->tail;
    if (eventq->size == 1) {
        eventq->head = NULL;
        eventq->tail = NULL;
    } else {        
        eventq->tail = eventq->tail->prev;
        eventq->tail->next = NULL;    
    }
    eventq->size--;
    return back;
}

void *eventq_loop(void *args) {
    pthread_mutex_lock(&eventq->mtx);
    JNIEnv *env;
    if ((*eventq->jvm)->AttachCurrentThread(eventq->jvm, 
            (void **) &env, NULL) != JNI_OK) {
        LOGE(TAG, "Failed attach to vm thread.");
        return 0;
    }
    while(TRUE) {
       while (eventq->size == 0 && eventq->running) {
         pthread_cond_wait(&eventq->cv, &eventq->mtx);         
       }       
       if (!eventq->running) break;
       if (eventq->size == 0) {
            continue;
       }
       Event e = eventq->tail;
       if (eventq->size == 1) {
           eventq->head = NULL;
           eventq->tail = NULL;
       } else {        
           eventq->tail = eventq->tail->prev;
           eventq->tail->next = NULL;    
       }
       eventq->size--;
       eventq->process(env, e);
    }//while
    (*eventq->jvm)->DetachCurrentThread(eventq->jvm);
    pthread_mutex_unlock(&eventq->mtx);
    pthread_mutex_destroy(&eventq->mtx);
    pthread_cond_destroy(&eventq->cv);
}

void eventq_stop() {
    pthread_mutex_lock(&eventq->mtx);
    eventq->running = FALSE;
    pthread_cond_signal(&eventq->cv);
    pthread_mutex_unlock(&eventq->mtx);        
}

int eventq_start(JNIEnv *env) {
    if(!eventq) {
        LOGE(TAG, "Failed to create notification queue");
        return EXIT_FAILURE;
    }
    (*env)->GetJavaVM(env, &eventq->jvm);
    eventq->running = TRUE;
    int err = pthread_create(&eventq->thread, NULL, eventq->loop, NULL);
    if (err) {
        LOGE(TAG, "Failed to create notification queue thread");
        return EXIT_FAILURE;
    }
    LOGV(TAG, "Notification queue started");
    pthread_join(eventq->thread, NULL);
    LOGV(TAG, "Notification queue finished");
    return EXIT_SUCCESS;
}

EventQ eventq_getInstance() {
    if (!eventq) {
        eventq = (EventQ) malloc(sizeof(_EventQ));
        pthread_mutex_init(&eventq->mtx , NULL);
        pthread_cond_init(&eventq->cv, NULL);        
        eventq->size = 0;
        eventq->head = NULL;
        eventq->tail = NULL;
        eventq->start = &eventq_start;
        eventq->stop = &eventq_stop;
        eventq->push = &eventq_push;
        eventq->pop = &eventq_pop;
        eventq->loop = &eventq_loop;
        LOGV(TAG, "Notification queue instance created.");
    }    
    return eventq;
}