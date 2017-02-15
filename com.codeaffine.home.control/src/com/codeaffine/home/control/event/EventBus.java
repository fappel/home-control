package com.codeaffine.home.control.event;

public interface EventBus {
  void post( Object eventObject );
  void register( Object eventObserver );
  void unregister( Object eventObserver );
}