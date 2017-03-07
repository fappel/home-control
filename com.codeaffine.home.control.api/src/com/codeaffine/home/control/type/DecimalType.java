package com.codeaffine.home.control.type;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import java.math.BigDecimal;

import com.codeaffine.home.control.Status;

public class DecimalType extends Number implements Status, Comparable<DecimalType> {

  public final static DecimalType ZERO = new DecimalType( 0L );

  private static final long serialVersionUID = -4402309446204164305L;

  private final BigDecimal value;

  public DecimalType( BigDecimal value ) {
    verifyNotNull( value, "value" );

    this.value = value;
  }

  public DecimalType( long value ) {
    this.value = BigDecimal.valueOf( value );
  }

  public DecimalType( double value ) {
    this.value = BigDecimal.valueOf( value );
  }

  public DecimalType( String value ) {
    verifyNotNull( value, "value" );

    this.value = new BigDecimal( value );
  }

  @Override
  public int intValue() {
    return value.intValue();
  }

  @Override
  public long longValue() {
    return value.longValue();
  }

  @Override
  public float floatValue() {
    return value.floatValue();
  }

  @Override
  public double doubleValue() {
    return value.doubleValue();
  }

  public BigDecimal toBigDecimal() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + value.hashCode();
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
    DecimalType other = ( DecimalType )obj;
    if( !value.equals( other.value ) )
      return false;
    return true;
  }

  @Override
  public int compareTo( DecimalType other ) {
    return value.compareTo( other.value );
  }
}