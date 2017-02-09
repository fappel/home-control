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
import com.codeaffine.home.control.StatusChangeListener;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class ItemAdapter<T extends Status> implements Item<T> {

  private final Map<StatusChangeListener<T>, StateChangeAdapter<T>> listeners;
  private final Map<StatusChangeListener<T>, Runnable> shutdownHooks;
  private final ShutdownDispatcher shutdownDispatcher;
  private final EventPublisher eventPublisher;
  private final ItemRegistryAdapter registry;
  private final SystemExecutor executor;
  private final Class<T> statusType;
  private final String key;

  private GenericItem item;

  protected ItemAdapter( String key,
                         ItemRegistryAdapter registry,
                         EventPublisher eventPublisher,
                         ShutdownDispatcher shutdownDispatcher,
                         SystemExecutor executor,
                         Class<T> statusType )
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
  public void addItemStateChangeListener( StatusChangeListener<T> listener ) {
    if( !listeners.containsKey( listener ) ) {
      registerStateChangeListener( listener );
      registerShutdownHook( listener );
    }
  }

  @Override
  public void removeItemStateChangeListener( StatusChangeListener<T> listener ) {
    if( listeners.containsKey( listener ) ) {
      StateChangeAdapter<T> remove = listeners.remove( listener );
      item.removeStateChangeListener( remove );
      shutdownHooks.remove( listener );
    }
  }

  @Override
  public Optional<T> getStatus() {
    return convert( item.getState(), getStatusType() );
  }

  protected void setStatusInternal( T status ) {
    verifyNotNull( status, "status" );

    item.setState( convert( status, getStatusType() ) );
  }

  protected void sendStatusInternal( T status ) {
    verifyNotNull( status, "status" );

    Command command = ( Command )convert( status, getStatusType() );
    eventPublisher.post( createCommandEvent( item.getName(), command ) );
  }

  public Class<T> getStatusType() {
    return statusType;
  }

  GenericItem getItem() {
    return item;
  }

  private void registerStateChangeListener( StatusChangeListener<T> listener ) {
    StateChangeAdapter<T> stateChangeAdapter = new StateChangeAdapter<>( this, listener, executor );
    listeners.put( listener, stateChangeAdapter );
    item.addStateChangeListener( stateChangeAdapter );
  }

  private void registerShutdownHook( StatusChangeListener<T> listener ) {
    Runnable shutdownHook = () -> removeItemStateChangeListener( listener );
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