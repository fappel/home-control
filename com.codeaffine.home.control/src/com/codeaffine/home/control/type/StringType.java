package com.codeaffine.home.control.type;

import com.codeaffine.home.control.Status;
import com.codeaffine.util.ArgumentVerification;

public class StringType implements Status {

  public final static StringType EMPTY = new StringType( "" );

  private final String value;

  public StringType( String value ) {
    ArgumentVerification.verifyNotNull( value, "value" );

    this.value = value;
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
    StringType other = ( StringType )obj;
    if( !value.equals( other.value ) )
      return false;
    return true;
  }
}