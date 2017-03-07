package com.codeaffine.home.control.event;

import java.util.Optional;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;

public interface UpdateEvent<I extends Item<I, S>, S extends Status> {
  I getSource();
  Optional<S> getUpdatedStatus();
}