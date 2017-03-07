package com.codeaffine.home.control.event;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;

@FunctionalInterface
public interface ChangeListener<I extends Item<I,S>, S extends Status> extends ItemListener<I, S> {
  void itemChanged( ChangeEvent<I, S> event );
}