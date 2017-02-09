package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.type.TypeConverter.convert;

import java.util.Optional;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.StateChangeListener;
import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.ItemStateChangeListener;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.internal.util.SystemExecutor;

class StateChangeAdapter<T extends Status> implements StateChangeListener {

  private final ItemStateChangeListener<T> listener;
  private final ItemAdapter<T> itemAdapter;
  private final SystemExecutor executor;

  StateChangeAdapter( ItemAdapter<T> itemAdapter, ItemStateChangeListener<T> listener, SystemExecutor executor ) {
    this.itemAdapter = itemAdapter;
    this.listener = listener;
    this.executor = executor;
  }

  @Override
  public void stateUpdated( Item item, State state ) {
    executor.execute( () -> listener.stateUpdated( itemAdapter, convertTo( state ) ) );
  }

  @Override
  public void stateChanged( Item item, State oldState, State newState ) {
    executor.execute( () -> listener.stateChanged( itemAdapter, convertTo( oldState ), convertTo( newState ) ) );
  }

  ItemStateChangeListener<T> getListener() {
    return listener;
  }

  private Optional<T> convertTo( State source ) {
    return convert( source, itemAdapter.getStatusType() );
  }
}