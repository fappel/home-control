package com.codeaffine.home.control.status;

public interface Scene {
  String getName();
  void prepare();
  default void close() {}
}