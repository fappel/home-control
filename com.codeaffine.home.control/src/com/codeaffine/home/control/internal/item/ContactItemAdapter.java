package com.codeaffine.home.control.internal.item;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OpenClosedType;

public class ContactItemAdapter extends ItemAdapter<OpenClosedType> implements ContactItem {

  protected ContactItemAdapter( String key,
                                ItemRegistryAdapter registry,
                                EventPublisher eventPublisher,
                                ShutdownDispatcher shutdownDispatcher,
                                SystemExecutor executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, OpenClosedType.class );
  }

  @Override
  public OpenClosedType getStatus( OpenClosedType defaultValue ) {
    return getStatus().orElse( defaultValue );
  }
}