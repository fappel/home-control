package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.adapter.Messages.ERROR_UNKNOWN_ITEM_KEY;
import static com.codeaffine.home.control.internal.item.ItemAdapterFactory.createAdapter;
import static com.codeaffine.home.control.internal.util.ServiceCollector.collectServices;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.internal.util.SystemExecutorImpl;

public class ItemRegistryAdapter implements Registry {

  private final Map<String, Item<? extends Item<?, ?>, ? extends Status>> referencedItemAdapters;
  private final ShutdownDispatcher shutdownDispatcher;
  private final ResetHookTrigger resetHookTrigger;
  private final SystemExecutorImpl executor;
  private final EventPublisher eventPublisher;
  private final ItemRegistry registry;

  public ItemRegistryAdapter(
    BundleContext bundleContext, ShutdownDispatcher shutdownDispatcher, SystemExecutorImpl executor )
  {
    this.shutdownDispatcher = shutdownDispatcher;
    this.executor = executor;
    this.resetHookTrigger = new ResetHookTrigger( executor );
    this.registry = collectServices( ItemRegistry.class, bundleContext ).get( 0 );
    this.eventPublisher = collectServices( EventPublisher.class, bundleContext ).get( 0 );
    this.referencedItemAdapters = new HashMap<>();
    registry.addRegistryChangeListener( resetHookTrigger );
    shutdownDispatcher.addShutdownHook( () -> registry.removeRegistryChangeListener( resetHookTrigger ) );
  }

  @Override
  public <I extends Item<I, ? extends Status>> I getItem( String key, Class<I> itemType ) {
    I result = itemType.cast( referencedItemAdapters.get( key ) );
    if( result == null ) {
      result = createItemAdapter( key, itemType );
    }
    return result;
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
      throw new IllegalArgumentException( format( ERROR_UNKNOWN_ITEM_KEY, key ), e );
    }
  }

  private <I extends Item<I, ? extends Status>> I createItemAdapter( String key, Class<I> itemType ) {
    ItemAdapter<? extends Item<?,?>, ? extends Status> adapter
      = createAdapter( key, itemType, this, eventPublisher, shutdownDispatcher, executor );
    adapter.initialize();
    referencedItemAdapters.put( key, adapter );
    return itemType.cast( adapter );
  }
}