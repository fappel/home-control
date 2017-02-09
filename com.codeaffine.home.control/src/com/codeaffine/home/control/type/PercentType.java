package com.codeaffine.home.control.type;

import static com.codeaffine.home.control.type.Messages.ERROR_PERCENT_OUT_OF_RANGE;
import static java.lang.String.format;

import java.math.BigDecimal;

public class PercentType extends DecimalType {

  private static final BigDecimal UPPER_BOUND = new BigDecimal( 100 );
  private static final BigDecimal LOWER_BOUND = new BigDecimal( 0 );
  private static final long serialVersionUID = -7397092329797757471L;

  public static final PercentType ZERO = new PercentType( LOWER_BOUND );
  public static final PercentType HUNDRED = new PercentType( UPPER_BOUND );

  public PercentType( BigDecimal value ) {
    super( value );
    validatePercentageRange( toBigDecimal() );
  }

  public PercentType( long value ) {
    super( value );
    validatePercentageRange( toBigDecimal() );
  }

  public PercentType( double value ) {
    super( value );
    validatePercentageRange( toBigDecimal() );
  }

  public PercentType( String value ) {
    super( value );
    validatePercentageRange( toBigDecimal() );
  }

  private static void validatePercentageRange( BigDecimal value ) {
    if( isBelowLowerBound( value ) || isAboveUpperBound( value ) ) {
      throw new IllegalArgumentException( format( ERROR_PERCENT_OUT_OF_RANGE, value ) );
    }
  }

  private static boolean isBelowLowerBound( BigDecimal value ) {
    return LOWER_BOUND.compareTo( value ) > 0;
  }

  private static boolean isAboveUpperBound( BigDecimal value ) {
    return UPPER_BOUND.compareTo( value ) < 0;
  }
}