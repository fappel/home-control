package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.item.ItemAdapterFactory.createAdapter;
import static com.codeaffine.home.control.internal.util.ServiceCollector.collectServices;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class ItemRegistryAdapter implements Registry {

  private final ShutdownDispatcher shutdownDispatcher;
  private final ResetHookTrigger resetHookTrigger;
  private final SystemExecutor executor;
  private final EventPublisher eventPublisher;
  private final ItemRegistry registry;

  public ItemRegistryAdapter( BundleContext bundleContext, ShutdownDispatcher shutdownDispatcher, SystemExecutor executor ) {
    this.shutdownDispatcher = shutdownDispatcher;
    this.executor = executor;
    this.resetHookTrigger = new ResetHookTrigger( executor );
    this.registry = collectServices( ItemRegistry.class, bundleContext ).get( 0 );
    this.eventPublisher = collectServices( EventPublisher.class, bundleContext ).get( 0 );
    registry.addRegistryChangeListener( resetHookTrigger );
    shutdownDispatcher.addShutdownHook( () -> registry.removeRegistryChangeListener( resetHookTrigger ) );
  }

  @Override
  public <I extends Item<I, ? extends Status>> I getItem( String key, Class<I> itemType ) {
    ItemAdapter<? extends Item<?,?>, ? extends Status> adapter
      = createAdapter( key, itemType, this, eventPublisher, shutdownDispatcher, executor );
    adapter.initialize();
    return itemType.cast( adapter );
  }

  void addResetHook( Runnable resetHook ) {
    resetHookTrigger.addResetHook( resetHook );
  }

  void removeResetHook( Runnable resetHook ) {
    resetHookTrigger.removeResetHook( resetHook );
  }

  GenericItem getGenericItem( String key ) {
    try {
      return ( GenericItem )registry.getItem( key );
    } catch( ItemNotFoundException e ) {
      throw new IllegalStateException( e );
    }
  }
}