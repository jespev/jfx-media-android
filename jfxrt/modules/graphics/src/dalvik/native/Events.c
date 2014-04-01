#include "Events.h"
#include <stdio.h>
#include "logging.h"

static char *SIGNAL_STARTUP  = "Signal Event: startup";
static char *SIGNAL_SHUTDOWN = "Signal Event: shutdown";
static char *SIGNAL_SHOW_IME = "Signal Event: show IME";
static char *SIGNAL_HIDE_IME = "Signal Event: hide IME";
static char *SIGNAL_UNKNOWN  = "Signal Event: unknown";
static char *EVENT_UNKNOWN   = "Event unknown";

char *SignalEvent_toString(Event e) {
    SignalEvent sevent = (SignalEvent)e;
    if (sevent->type == JFX_SIGNAL_STARTUP) {
        return SIGNAL_STARTUP;
    } else if (sevent->type == JFX_SIGNAL_SHUTDOWN) {
        return SIGNAL_SHUTDOWN;
    } else if (sevent->type == JFX_SIGNAL_SHOW_IME) {
        return SIGNAL_SHOW_IME;
    } else if (sevent->type == JFX_SIGNAL_HIDE_IME) {
        return SIGNAL_HIDE_IME;
    }
    return SIGNAL_UNKNOWN;
}

SignalEvent createSignalEvent(SignalEventType type) {
    SignalEvent signal_event = (SignalEvent)malloc(sizeof(_SignalEvent));
    signal_event->event = JFX_SIGNAL_EVENT;
    signal_event->prev = 0;
    signal_event->next = 0;
    signal_event->type = type;
    signal_event->toString = &SignalEvent_toString;
    return signal_event;
}

char *event_toString(Event e) {
    char *out = (char*)malloc(100);
    sprintf(out, "[%s]", e->toString(e));
    return out;    
}