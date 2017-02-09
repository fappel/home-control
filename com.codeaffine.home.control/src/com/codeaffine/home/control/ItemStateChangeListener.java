package com.codeaffine.home.control;

import java.util.Optional;

public interface ItemStateChangeListener <T extends Status> {
  void stateUpdated( Item<T> item, Optional<T> status );
  void stateChanged( Item<T> item, Optional<T> oldStatus, Optional<T> newStatus );
}