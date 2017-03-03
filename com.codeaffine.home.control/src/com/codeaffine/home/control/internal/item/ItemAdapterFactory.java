package com.codeaffine.home.control.internal.item;

import static com.codeaffine.home.control.internal.item.Messages.ERROR_ITEM_TYPE_NOT_SUPPORTED;
import static java.lang.String.format;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutorImpl;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.item.SwitchItem;

public class ItemAdapterFactory {

  public static ItemAdapter<? extends Item<?,?>, ? extends Status>
    createAdapter( String key,
                   Class<?> type,
                   ItemRegistryAdapter registry,
                   EventPublisher publisher,
                   ShutdownDispatcher shutdownDispatcher,
                   SystemExecutorImpl executor )
  {
    if( type == NumberItem.class ) {
      return new NumberItemAdapter( key, registry, publisher, shutdownDispatcher, executor );
    } else if( type == ContactItem.class ) {
      return new ContactItemAdapter( key, registry, publisher, shutdownDispatcher, executor );
    } else if( type == SwitchItem.class ) {
      return new SwitchItemAdapter( key, registry, publisher, shutdownDispatcher, executor );
    } else if( type == DimmerItem.class ) {
      return new DimmerItemAdapter( key, registry, publisher, shutdownDispatcher, executor );
    } else if( type == StringItem.class ) {
      return new StringItemAdapter( key, registry, publisher, shutdownDispatcher, executor );
    }
    throw new IllegalArgumentException( format( ERROR_ITEM_TYPE_NOT_SUPPORTED, type.getName() ) );
  }
}