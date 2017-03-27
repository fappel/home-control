/**
 * Based on the second answer of
 * http://stackoverflow.com/questions/22695654/computing-the-nth-root-of-p-using-bigdecimals
 */
package com.codeaffine.home.control.application.scene;

import static com.codeaffine.util.ArgumentVerification.*;
import static java.math.BigDecimal.*;

import java.math.BigDecimal;

class RootMath {

  static BigDecimal nthRootOf( BigDecimal radical, int rootExponent, int scale ) {
    verifyNotNull( radical, "radical" );
    verifyCondition( radical.compareTo( ZERO ) >= 0, "The argument radical must be greater or equals to Zero." );
    verifyCondition( rootExponent >= 0, "The argument rootExponent must be greater or equals to Zero." );
    verifyCondition( scale >= 0, "The argument scale must be greater or equals to Zero." );

    return nthRootOf( radical, rootExponent, BigDecimal.valueOf( .1 ).movePointLeft( scale ), scale );
  }

  private static BigDecimal nthRootOf( BigDecimal radical, int exponent, BigDecimal precision, int scale ) {
    if( radical.equals( ZERO ) ) {
      return ZERO.setScale( scale );
    }
    if( exponent == 0 ) {
      return ONE.setScale( scale );
    }
    return doNthRootOf( radical, exponent, precision, scale );
  }

  private static BigDecimal doNthRootOf( BigDecimal radical, int exponent, BigDecimal precision, int scale ) {
    BigDecimal previous = radical;
    BigDecimal result = radical.divide( new BigDecimal( exponent ), scale, ROUND_HALF_DOWN );
    while( result.subtract( previous ).abs().compareTo( precision ) > 0 ) {
      previous = result;
      result = BigDecimal.valueOf( exponent - 1.0 )
        .multiply( result )
        .add( radical.divide( result.pow( exponent - 1 ), scale, ROUND_HALF_DOWN ) )
        .divide( new BigDecimal( exponent ), scale, ROUND_HALF_DOWN );
    }
    return result;
  }
}