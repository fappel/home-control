package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.ERROR_UNKNOWN_STATE_VALUE_FOR_DECIMAL_TYPE;
import static java.lang.String.format;
import static java.util.Optional.empty;

import java.math.BigDecimal;
import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

import com.codeaffine.home.control.type.DecimalType;

public class DecimalTypeConverter {

  public static Optional<DecimalType> convert( State state ) {
    Optional<DecimalType> result = null;
    if( state == UnDefType.NULL ) {
      result = empty();
    } else if( state == UnDefType.UNDEF ) {
      result = empty();
    } else if( state instanceof org.eclipse.smarthome.core.library.types.DecimalType ) {
      BigDecimal value = ( ( org.eclipse.smarthome.core.library.types.DecimalType )state ).toBigDecimal();
      result = Optional.of( new DecimalType( value ) );
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATE_VALUE_FOR_DECIMAL_TYPE, state ) );
    }
    return result;
  }

  public static State convert( DecimalType status ) {
    return new org.eclipse.smarthome.core.library.types.DecimalType( status.toBigDecimal() );
  }
}