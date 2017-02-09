package com.codeaffine.home.control.internal.item;

import java.math.BigDecimal;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.type.PercentType;

public class DimmerItemAdapter extends ItemAdapter<PercentType> implements DimmerItem {

  protected DimmerItemAdapter( String key,
                               ItemRegistryAdapter registry,
                               EventPublisher eventPublisher,
                               ShutdownDispatcher shutdownDispatcher,
                               SystemExecutor executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, PercentType.class );
  }

  @Override
  public BigDecimal getStatus( BigDecimal defaultValue ) {
    return getStatus().orElse( new PercentType( defaultValue ) ).toBigDecimal();
  }

  @Override
  public int getStatus( int defaultValue ) {
    return getStatus().orElse( new PercentType( defaultValue ) ).intValue();
  }

  @Override
  public long getStatus( long defaultValue ) {
    return getStatus().orElse( new PercentType( defaultValue ) ).longValue();
  }

  @Override
  public float getStatus( float defaultValue ) {
    return getStatus().orElse( new PercentType( defaultValue ) ).floatValue();
  }

  @Override
  public double getStatus( double defaultValue ) {
    return getStatus().orElse( new PercentType( defaultValue ) ).doubleValue();
  }

  @Override
  public void setStatus( PercentType status ) {
    super.setStatusInternal( status );
  }

  @Override
  public void setStatus( BigDecimal value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void setStatus( int value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void setStatus( long value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void setStatus( float value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void setStatus( double value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void setStatus( String value ) {
    setStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( PercentType status ) {
    super.sendStatusInternal( status );
  }

  @Override
  public void sendStatus( BigDecimal value ) {
    sendStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( int value ) {
    sendStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( long value ) {
    sendStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( float value ) {
    sendStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( double value ) {
    sendStatus( new PercentType( value ) );
  }

  @Override
  public void sendStatus( String value ) {
    sendStatus( new PercentType( value ) );
  }
}