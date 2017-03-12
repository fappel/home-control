package com.codeaffine.home.control.type;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Status;

public class StringType implements Status {

  public final static StringType EMPTY = new StringType( "" );

  private final String value;

  public StringType( String value ) {
    verifyNotNull( value, "value" );

    this.value = value;
  }

  @Override
  public String toString() {
    return value;
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
    StringType other = ( StringType )obj;
    if( !value.equals( other.value ) )
      return false;
    return true;
  }
}