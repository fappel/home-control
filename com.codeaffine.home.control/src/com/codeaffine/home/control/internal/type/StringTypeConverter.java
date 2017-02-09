package com.codeaffine.home.control.internal.type;

import static com.codeaffine.home.control.internal.type.Messages.*;
import static java.lang.String.format;
import static java.util.Optional.empty;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

import com.codeaffine.home.control.type.StringType;

public class StringTypeConverter {

  public static Optional<StringType> convert( State state ) {
    Optional<StringType> result = null;
    if( state == UnDefType.NULL ) {
      result = empty();
    } else if( state == UnDefType.UNDEF ) {
      result = empty();
    } else if( state instanceof org.eclipse.smarthome.core.library.types.DateTimeType ) {
      throw new IllegalStateException( format( ERROR_UNSUPPORTED_CONVERSION_FROM_DATA_TIME_TYPE, state ) );
    } else if( state instanceof org.eclipse.smarthome.core.library.types.StringType ) {
      String value = ( ( org.eclipse.smarthome.core.library.types.StringType )state ).toString();
      result = Optional.of( new StringType( value ) );
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATE_VALUE_FOR_STRING_TYPE, state ) );
    }
    return result;
  }

  public static State convert( StringType status ) {
    return new org.eclipse.smarthome.core.library.types.StringType( status.toString() );
  }
}