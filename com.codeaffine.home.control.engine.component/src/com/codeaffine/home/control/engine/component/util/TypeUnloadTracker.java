package com.codeaffine.home.control.engine.component.util;

public interface TypeUnloadTracker {
  void unregisterUnloadHook( Class<?> type, Runnable unloadHook );
  void registerUnloadHook( Class<?> type, Runnable unloadHook );
}