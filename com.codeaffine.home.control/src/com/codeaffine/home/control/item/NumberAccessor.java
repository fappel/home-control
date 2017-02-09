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

  void sendStatus( T status );
  void sendStatus( BigDecimal value );
  void sendStatus( int value );
  void sendStatus( long value );
  void sendStatus( float value );
  void sendStatus( double value );
  void sendStatus( String value );
}