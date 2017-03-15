package com.codeaffine.home.control.status;

public interface HomeControlOperation {
  void reset();
  void executeOn( StatusEvent event );
}