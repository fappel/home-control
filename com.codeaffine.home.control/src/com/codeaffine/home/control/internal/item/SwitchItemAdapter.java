package com.codeaffine.home.control.internal.item;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutorImpl;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;

public class SwitchItemAdapter extends ItemAdapter<SwitchItem, OnOffType> implements SwitchItem {

  protected SwitchItemAdapter( String key,
                               ItemRegistryAdapter registry,
                               EventPublisher eventPublisher,
                               ShutdownDispatcher shutdownDispatcher,
                               SystemExecutorImpl executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, OnOffType.class );
  }

  @Override
  public OnOffType getStatus( OnOffType defaultValue ) {
    return getStatus().orElse( defaultValue );
  }

  @Override
  public void setStatus( OnOffType status ) {
    setStatusInternal( status );
  }

  @Override
  public void updateStatus( OnOffType status ) {
    updateStatusInternal( status );
  }
}