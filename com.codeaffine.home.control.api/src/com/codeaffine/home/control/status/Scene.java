package com.codeaffine.home.control.status;

public interface Scene {
  String getName();
  void activate();
  default void deactivate() {}
}