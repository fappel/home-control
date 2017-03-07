package com.codeaffine.home.control.engine.item;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.engine.adapter.ItemAdapter;
import com.codeaffine.home.control.engine.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OpenClosedType;

public class ContactItemAdapter extends ItemAdapter<ContactItem, OpenClosedType> implements ContactItem {

  protected ContactItemAdapter( String key,
                                ItemRegistryAdapter registry,
                                EventPublisher eventPublisher,
                                ShutdownDispatcher shutdownDispatcher,
                                SystemExecutorImpl executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, OpenClosedType.class );
  }

  @Override
  public OpenClosedType getStatus( OpenClosedType defaultValue ) {
    return getStatus().orElse( defaultValue );
  }
}