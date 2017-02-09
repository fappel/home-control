package com.codeaffine.home.control;

import java.util.Optional;

public interface Item<T extends Status> {
  Optional<T> getStatus();
  void addItemStateChangeListener( StatusChangeListener<T> listener );
  void removeItemStateChangeListener( StatusChangeListener<T> listener );
}