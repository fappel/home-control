package com.codeaffine.home.control.application.status;

import static com.codeaffine.home.control.application.status.Messages.*;
import static com.codeaffine.util.ArgumentVerification.verifyCondition;
import static java.lang.Double.valueOf;

public class SunPosition {

  public static final int MAX_ZENIT = 90;
  public static final int MIN_ZENIT = -90;
  public static final int MAX_AZIMUTH = 360;
  public static final int MIN_AZIMUTH = 0;

  private final double azimuth;
  private final double zenit;

  public SunPosition( double zenit, double azimuth ) {
    verifyCondition( zenit <= MAX_ZENIT && zenit >= MIN_ZENIT, INVALID_ZENIT_VALUE, valueOf( zenit ) );
    verifyCondition( azimuth <= MAX_AZIMUTH && azimuth >= MIN_AZIMUTH, INVALID_AZIMUTH_VALUE, valueOf( azimuth ) );

    this.zenit = zenit;
    this.azimuth = azimuth;
  }

  public double getZenit() {
    return zenit;
  }

  public double getAzimuth() {
    return azimuth;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits( azimuth );
    result = prime * result + ( int )( temp ^ ( temp >>> 32 ) );
    temp = Double.doubleToLongBits( zenit );
    result = prime * result + ( int )( temp ^ ( temp >>> 32 ) );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    SunPosition other = ( SunPosition )obj;
    if( Double.doubleToLongBits( azimuth ) != Double.doubleToLongBits( other.azimuth ) )
      return false;
    if( Double.doubleToLongBits( zenit ) != Double.doubleToLongBits( other.zenit ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SunPosition [zenit=" + zenit + ", azimuth=" + azimuth + "]";
  }
}