package com.codeaffine.home.control.internal.type;

import static com.codeaffine.home.control.internal.type.Messages.*;
import static java.lang.String.format;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.OpenClosedType;
import com.codeaffine.home.control.type.PercentType;
import com.codeaffine.home.control.type.StringType;

public class TypeConverter {

  @SuppressWarnings("unchecked")
  public static <T extends Status> Optional<T> convert( State state, Class<T> destinationType ) {
    if( destinationType == OpenClosedType.class ) {
      return ( Optional<T> )OpenClosedTypeConverter.convert( state );
    } else if( destinationType == DecimalType.class ) {
      return ( Optional<T> )DecimalTypeConverter.convert( state );
    } else if( destinationType == PercentType.class ) {
      return ( Optional<T> )PercentTypeConverter.convert( state );
    } else if( destinationType == OnOffType.class ) {
      return ( Optional<T> )OnOffTypeConverter.convert( state );
    } else if( destinationType == StringType.class ) {
      return ( Optional<T> )StringTypeConverter.convert( state );
    }
    throw new IllegalStateException( format( ERROR_UNKNOWN_STATE_TYPE_TO_ADAPT, state.getClass().getName() ) );
  }

  public static <T extends Status> State convert( T status, Class<T> sourceType ) {
    if( sourceType == OpenClosedType.class ) {
      return OpenClosedTypeConverter.convert( ( OpenClosedType )status );
    } else if( sourceType == DecimalType.class ) {
      return DecimalTypeConverter.convert( ( DecimalType )status );
    } else if( sourceType == PercentType.class ) {
      return PercentTypeConverter.convert( ( PercentType )status );
    } else if( sourceType == OnOffType.class ) {
      return OnOffTypeConverter.convert( ( OnOffType )status );
    } else if( sourceType == StringType.class ) {
      return StringTypeConverter.convert( ( StringType )status );
    }
    throw new IllegalStateException( format( ERROR_UNKNOWN_STATUS_TYPE_TO_ADAPT, sourceType.getName() ) );
  }
}