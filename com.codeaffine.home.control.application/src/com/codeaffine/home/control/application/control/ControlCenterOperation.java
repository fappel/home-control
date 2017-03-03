package com.codeaffine.home.control.application.control;

public interface ControlCenterOperation {
  void prepare();
  void executeOn( StatusEvent event );
}