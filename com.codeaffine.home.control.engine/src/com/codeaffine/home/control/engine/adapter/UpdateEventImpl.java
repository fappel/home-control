package com.codeaffine.home.control.engine.adapter;

import static com.codeaffine.home.control.engine.type.TypeConverter.convert;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.event.UpdateEvent;

class UpdateEventImpl<I extends Item<I, S>, S extends Status> implements UpdateEvent<I, S> {

  private final ItemAdapter<I, S> itemAdapter;
  private final State state;

  UpdateEventImpl( ItemAdapter<I, S> itemAdapter, State state ) {
    this.itemAdapter = itemAdapter;
    this.state = state;
  }

  @Override
  @SuppressWarnings("unchecked")
  public I getSource() {
    return ( I )itemAdapter;
  }

  @Override
  public Optional<S> getUpdatedStatus() {
    return convert( state, itemAdapter.getStatusType() );
  }
}