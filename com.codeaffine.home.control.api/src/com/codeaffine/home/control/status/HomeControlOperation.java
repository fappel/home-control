package com.codeaffine.home.control.status;

public interface HomeControlOperation {
  void prepare();
  void executeOn( StatusEvent event );
}