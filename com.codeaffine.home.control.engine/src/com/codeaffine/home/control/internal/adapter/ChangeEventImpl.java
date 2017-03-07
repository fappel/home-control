package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.type.TypeConverter.convert;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.event.ChangeEvent;

class ChangeEventImpl<I extends Item<I, S>, S extends Status> implements ChangeEvent<I, S> {

  private final ItemAdapter<I, S> itemAdapter;
  private final State oldState;
  private final State newState;

  ChangeEventImpl( ItemAdapter<I, S> itemAdapter, State oldState, State newState ) {
    this.itemAdapter = itemAdapter;
    this.oldState = oldState;
    this.newState = newState;
  }

  @Override
  @SuppressWarnings("unchecked")
  public I getSource() {
    return ( I )itemAdapter;
  }

  @Override
  public Optional<S> getOldStatus() {
    return convert( oldState, itemAdapter.getStatusType() );
  }

  @Override
  public Optional<S> getNewStatus() {
    return convert( newState, itemAdapter.getStatusType() );
  }
}