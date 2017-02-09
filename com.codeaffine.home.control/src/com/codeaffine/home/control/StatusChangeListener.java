package com.codeaffine.home.control;

import java.util.Optional;

public interface StatusChangeListener <T extends Status> {
  void statusUpdated( Item<T> item, Optional<T> status );
  void statusChanged( Item<T> item, Optional<T> oldStatus, Optional<T> newStatus );
}