package com.codeaffine.home.control.status.type;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class TypeConverter {

  public static OnOffType convertFromOnOff( OnOff value ) {
    verifyNotNull( value, "value" );

    return value == OnOff.ON ? OnOffType.ON : OnOffType.OFF;
  }

  public static OnOff convertToOnOff( Optional<OnOffType> value, OnOffType defaultValue ) {
    verifyNotNull( defaultValue, "defaultValue" );
    verifyNotNull( value, "value" );

    return value
      .map( actual -> doConvertToOnOff( actual ) )
      .orElse( doConvertToOnOff( defaultValue )  );
  }

  private static OnOff doConvertToOnOff( OnOffType value ) {
    verifyNotNull( value, "value" );

    return value == OnOffType.ON ? OnOff.ON : OnOff.OFF;
  }

  public static PercentType convertFromPercent( Percent value ) {
    verifyNotNull( value, "value" );

    return new PercentType( value.intValue() );
  }

  public static Percent convertToPercent( Optional<PercentType> value, PercentType defaultValue ) {
    verifyNotNull( defaultValue, "defaultValue" );
    verifyNotNull( value, "value" );

    return value
      .map( actual -> doConvertToPercent( actual ) )
      .orElse( doConvertToPercent( defaultValue ) );
  }

  private static Percent doConvertToPercent( PercentType value ) {
    verifyNotNull( value, "value" );

    return Percent.valueOf( value.intValue() );
  }
}