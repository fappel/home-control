package com.codeaffine.home.control.internal.item;

import java.math.BigDecimal;

import org.eclipse.smarthome.core.events.EventPublisher;

import com.codeaffine.home.control.internal.adapter.ItemAdapter;
import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.type.DecimalType;

public class NumberItemAdapter extends ItemAdapter<NumberItem, DecimalType> implements NumberItem {

  protected NumberItemAdapter( String key,
                               ItemRegistryAdapter registry,
                               EventPublisher eventPublisher,
                               ShutdownDispatcher shutdownDispatcher,
                               SystemExecutor executor )
  {
    super( key, registry, eventPublisher, shutdownDispatcher, executor, DecimalType.class );
  }

  @Override
  public BigDecimal getStatus( BigDecimal defaultValue ) {
    return getStatus().orElse( new DecimalType( defaultValue ) ).toBigDecimal();
  }

  @Override
  public int getStatus( int defaultValue ) {
    return getStatus().orElse( new DecimalType( defaultValue ) ).intValue();
  }

  @Override
  public long getStatus( long defaultValue ) {
    return getStatus().orElse( new DecimalType( defaultValue ) ).longValue();
  }

  @Override
  public float getStatus( float defaultValue ) {
    return getStatus().orElse( new DecimalType( defaultValue ) ).floatValue();
  }

  @Override
  public double getStatus( double defaultValue ) {
    return getStatus().orElse( new DecimalType( defaultValue ) ).doubleValue();
  }

  @Override
  public void setStatus( DecimalType status ) {
    super.setStatusInternal( status );
  }

  @Override
  public void setStatus( BigDecimal value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void setStatus( int value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void setStatus( long value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void setStatus( float value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void setStatus( double value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void setStatus( String value ) {
    setStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( DecimalType status ) {
    super.updateStatusInternal( status );
  }

  @Override
  public void updateStatus( BigDecimal value ) {
    updateStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( int value ) {
    updateStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( long value ) {
    updateStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( float value ) {
    updateStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( double value ) {
    updateStatus( new DecimalType( value ) );
  }

  @Override
  public void updateStatus( String value ) {
    updateStatus( new DecimalType( value ) );
  }
}