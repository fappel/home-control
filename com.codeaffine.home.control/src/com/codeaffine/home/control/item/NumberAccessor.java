package com.codeaffine.home.control.item;

import java.math.BigDecimal;

import com.codeaffine.home.control.type.DecimalType;

public interface NumberAccessor<T extends DecimalType> {

  BigDecimal getStatus( BigDecimal defaultValue );
  int getStatus( int defaultValue );
  long getStatus( long defaultValue );
  float getStatus( float defaultValue );
  double getStatus( double defaultValue );

  void setStatus( T status );
  void setStatus( BigDecimal value );
  void setStatus( int value );
  void setStatus( long value );
  void setStatus( float value );
  void setStatus( double value );
  void setStatus( String value );

  void updateStatus( T status );
  void updateStatus( BigDecimal value );
  void updateStatus( int value );
  void updateStatus( long value );
  void updateStatus( float value );
  void updateStatus( double value );
  void updateStatus( String value );
}