#ifndef EVENTS_H
#define EVENTS_H

#include <sys/types.h>
#include <stdlib.h>

#define JFX_SIGNAL_EVENT          1    
    
#define JFX_SIGNAL_STARTUP        2
#define JFX_SIGNAL_SHUTDOWN       3    
#define JFX_SIGNAL_SHOW_IME       4
#define JFX_SIGNAL_HIDE_IME       5    


#ifdef	__cplusplus
extern "C" {
#endif

typedef uint16_t EventType;
typedef uint16_t SignalEventType;

typedef struct Event {
    EventType     event;
    struct Event  *prev;
    struct Event  *next;
    char *(*toString)(struct Event *);
} _Event;
typedef _Event *Event;

typedef struct {
    EventType       event;
    struct Event    *prev;
    struct Event    *next;
    char *(*toString)(struct Event *);
    SignalEventType type;
}_SignalEvent;
typedef _SignalEvent *SignalEvent;

char *event_toString(Event e);
SignalEvent createSignalEvent(SignalEventType type);

#ifdef	__cplusplus
}
#endif

#endif	/* EVENTS_H */    