package com.codeaffine.home.control.application.control;

public interface Scene {
  void activate();
  default void deactivate() {}
}