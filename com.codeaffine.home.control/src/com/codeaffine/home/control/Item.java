package com.codeaffine.home.control;

import java.util.Optional;

public interface Item<T extends Status> {
  Optional<T> getStatus();
  void addItemStateChangeListener( ItemStateChangeListener<T> listener );
  void removeItemStateChangeListener( ItemStateChangeListener<T> listener );
}