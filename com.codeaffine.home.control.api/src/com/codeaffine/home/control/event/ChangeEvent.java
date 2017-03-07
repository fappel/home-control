package com.codeaffine.home.control.event;

import java.util.Optional;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;

public interface ChangeEvent<I extends Item<I,S>,S extends Status> {
  I getSource();
  Optional<S> getOldStatus();
  Optional<S> getNewStatus();
}