package com.codeaffine.home.control.engine.type;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;

import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.home.control.type.PercentType;

public class PercentTypeConverter {

  public static Optional<PercentType> convert( State state ) {
    Optional<DecimalType> asDecimal = DecimalTypeConverter.convert( state );
    return asDecimal.map( decimal -> new PercentType( decimal.toBigDecimal() ) );
  }

  public static State convert( PercentType status ) {
    return new org.eclipse.smarthome.core.library.types.PercentType( status.toBigDecimal() );
  }
}