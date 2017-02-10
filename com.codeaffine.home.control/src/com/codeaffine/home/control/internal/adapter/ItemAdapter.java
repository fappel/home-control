package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.type.TypeConverter.convert;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.smarthome.core.items.events.ItemEventFactory.createCommandEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.types.Command;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.ItemListener;
import com.codeaffine.home.control.event.UpdateListener;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class ItemAdapter<I extends Item<I, S>, S extends Status> implements Item<I, S> {

  private final Map<ItemListener<I, S>, StateChangeAdapter<I, S>> listeners;
  private final Map<ItemListener<I, S>, Runnable> shutdownHooks;
  private final ShutdownDispatcher shutdownDispatcher;
  private final EventPublisher eventPublisher;
  private final ItemRegistryAdapter registry;
  private final SystemExecutor executor;
  private final Class<S> statusType;
  private final String key;

  private GenericItem item;

  protected ItemAdapter( String key,
                         ItemRegistryAdapter registry,
                         EventPublisher eventPublisher,
                         ShutdownDispatcher shutdownDispatcher,
                         SystemExecutor executor,
                         Class<S> statusType )
  {
    verifyNotNull( key, "key" );
    verifyNotNull( registry, "registry" );
    verifyNotNull( eventPublisher, "eventPublisher" );
    verifyNotNull( shutdownDispatcher, "shutdownDispatcher" );
    verifyNotNull( executor, "executor" );
    verifyNotNull( statusType, "statusType" );

    this.shutdownHooks = new HashMap<>();
    this.listeners = new HashMap<>();
    this.shutdownDispatcher = shutdownDispatcher;
    this.eventPublisher = eventPublisher;
    this.statusType = statusType;
    this.executor = executor;
    this.registry = registry;
    this.key = key;
  }

  @Override
  public void addChangeListener( ChangeListener<I, S> listener ) {
    addStatusListener( listener );
  }

  @Override
  public void removeChangeListener( ChangeListener<I, S> listener ) {
    removeStatusListener( listener );
  }

  @Override
  public void addUpdateListener( UpdateListener<I, S> listener ) {
    addStatusListener( listener );
  }

  @Override
  public void removeUpdateListener( UpdateListener<I, S> listener ) {
    removeStatusListener( listener );
  }

  @Override
  public Optional<S> getStatus() {
    return convert( item.getState(), getStatusType() );
  }

  protected void setStatusInternal( S status ) {
    verifyNotNull( status, "status" );

    item.setState( convert( status, getStatusType() ) );
  }

  protected void updateStatusInternal( S status ) {
    verifyNotNull( status, "status" );

    Command command = ( Command )convert( status, getStatusType() );
    eventPublisher.post( createCommandEvent( item.getName(), command ) );
  }

  public Class<S> getStatusType() {
    return statusType;
  }

  GenericItem getItem() {
    return item;
  }

  private void addStatusListener( ItemListener<I, S> listener ) {
    if( !listeners.containsKey( listener ) ) {
      registerStateListener( listener );
      registerShutdownHook( listener );
    }
  }

  private void removeStatusListener( ItemListener<I, S> listener ) {
    if( listeners.containsKey( listener ) ) {
      StateChangeAdapter<I, S> remove = listeners.remove( listener );
      item.removeStateChangeListener( remove );
      shutdownHooks.remove( listener );
    }
  }

  private void registerStateListener( ItemListener<I, S> listener ) {
    StateChangeAdapter<I, S> stateChangeAdapter = new StateChangeAdapter<>( this, listener, executor );
    listeners.put( listener, stateChangeAdapter );
    item.addStateChangeListener( stateChangeAdapter );
  }

  private void registerShutdownHook( ItemListener<I, S> listener ) {
    Runnable shutdownHook = () -> removeStatusListener( listener );
    shutdownHooks.put( listener, shutdownHook );
    shutdownDispatcher.addShutdownHook( shutdownHook );
  }

  void initialize() {
    item = getGenericItem();
    Runnable resetHook = () -> rebind();
    registry.addResetHook( resetHook );
    shutdownDispatcher.addShutdownHook( () -> registry.removeResetHook( resetHook ) );
  }

  private void rebind() {
    listeners.values().forEach( listener -> item.removeStateChangeListener( listener ) );
    item = getGenericItem();
    listeners.values().forEach( listener -> item.addStateChangeListener( listener ) );
  }

  private GenericItem getGenericItem() {
    return registry.getGenericItem( key );
  }
}