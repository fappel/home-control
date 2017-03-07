package com.codeaffine.home.control.status;

public interface Scene {
  void activate();
  default void deactivate() {}
}