package com.codeaffine.home.control.internal.item;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutorImpl;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.type.StringType;


public class StringItemAdapter extends ItemAdapter<StringItem, StringType> implements StringItem {

  protected StringItemAdapter( String key,
                               ItemRegistryAdapter registry,
                               EventPublisher eventPublisher,
                               ShutdownDispatcher shutdownDispatcher,
                               SystemExecutorImpl executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, StringType.class );
  }

  @Override
  public String getStatus( String defaultValue ) {
    return getStatus().orElse( asStringType( defaultValue ) ).toString();
  }

  @Override
  public void setStatus( StringType status ) {
    super.setStatusInternal( status );
  }

  @Override
  public void setStatus( String status ) {
    super.setStatusInternal( asStringType( status ) );
  }

  @Override
  public void updateStatus( StringType status ) {
    super.updateStatusInternal( status );
  }

  @Override
  public void updateStatus( String status ) {
    super.updateStatusInternal( asStringType( status ) );
  }

  private static StringType asStringType( String status ) {
    return new StringType( status );
  }
}