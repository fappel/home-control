package com.codeaffine.home.control;

import java.util.Optional;

import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.UpdateListener;

public interface Item<I extends Item<I,S>, S extends Status> {
  Optional<S> getStatus();
  void addChangeListener( ChangeListener<I, S> listener );
  void removeChangeListener( ChangeListener<I,S> listener );
  void addUpdateListener( UpdateListener<I,S> listener );
  void removeUpdateListener( UpdateListener<I,S> listener );
}