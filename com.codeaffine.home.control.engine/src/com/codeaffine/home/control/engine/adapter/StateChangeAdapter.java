package com.codeaffine.home.control.engine.adapter;

import org.eclipse.smarthome.core.items.StateChangeListener;
import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.ItemListener;
import com.codeaffine.home.control.event.UpdateListener;

class StateChangeAdapter<I extends Item<I, S>, S extends Status>
  implements StateChangeListener
{

  private final ChangeListener<I, S> changeListener;
  private final UpdateListener<I ,S> updateListener;
  private final ItemAdapter<I, S> itemAdapter;
  private final SystemExecutorImpl executor;

  StateChangeAdapter( ItemAdapter<I, S> itemAdapter, ItemListener<I, S> listener, SystemExecutorImpl executor ) {
    this.changeListener = ensureChangeListener( listener );
    this.updateListener = ensureUpdateListener( listener );
    this.itemAdapter = itemAdapter;
    this.executor = executor;
  }

  @Override
  public void stateUpdated( org.eclipse.smarthome.core.items.Item item, State state ) {
    executor.execute( () -> updateListener.itemUpdated( new UpdateEventImpl<>( itemAdapter, state ) ) );
  }

  @Override
  public void stateChanged( org.eclipse.smarthome.core.items.Item item, State oldState, State newState ) {
    executor.execute( () -> changeListener.itemChanged( new ChangeEventImpl<>( itemAdapter, oldState, newState )  ) );
  }

  ChangeListener<I,S> getChangeListener() {
    return changeListener;
  }

  UpdateListener<I,S> getUpdateListener() {
    return updateListener;
  }

  private ChangeListener<I, S> ensureChangeListener( ItemListener<I, S> lsnr ) {
    return lsnr instanceof ChangeListener ? ( com.codeaffine.home.control.event.ChangeListener<I, S> )lsnr : evt -> {};
  }

  private UpdateListener<I, S> ensureUpdateListener( ItemListener<I, S> lsnr ) {
    return lsnr instanceof UpdateListener ? ( com.codeaffine.home.control.event.UpdateListener<I, S> )lsnr : evt -> {};
  }
}