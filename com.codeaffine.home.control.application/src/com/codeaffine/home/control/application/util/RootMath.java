/**
 * Based on the second answer of
 * http://stackoverflow.com/questions/22695654/computing-the-nth-root-of-p-using-bigdecimals
 */
package com.codeaffine.home.control.application.util;

import static com.codeaffine.util.ArgumentVerification.*;
import static java.math.BigDecimal.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

class RootMath {

  static BigDecimal nthRootOf( BigDecimal radical, int rootExponent, int scale ) {
    verifyNotNull( radical, "radical" );
    verifyCondition( radical.compareTo( ZERO ) >= 0, "The argument radical must be greater or equals to Zero." );
    verifyCondition( rootExponent >= 0, "The argument rootExponent must be greater or equals to Zero." );
    verifyCondition( scale >= 0, "The argument scale must be greater or equals to Zero." );

    BigDecimal precision = scale == 0 ? ZERO : BigDecimal.valueOf( .1 ).movePointLeft( scale );
    return nthRootOf( radical, rootExponent, precision, scale );
  }

  private static BigDecimal nthRootOf( BigDecimal radical, int exponent, BigDecimal precision, int scale ) {
    if( radical.equals( ZERO ) ) {
      return ZERO.setScale( scale );
    }
    if( exponent == 0 ) {
      return ONE.setScale( scale );
    }
    if( radical.compareTo( ONE ) == 0 ) {
      return ONE.setScale( scale );
    }
    return doNthRootOf( radical, exponent, precision, scale );
  }

  private static BigDecimal doNthRootOf( BigDecimal radical, int exponent, BigDecimal precision, int scale ) {
    BigDecimal previous = radical;
    BigDecimal result = radical.divide( new BigDecimal( exponent ), scale, ROUND_HALF_DOWN );
    if( result.compareTo( ZERO ) == 0 ) {
      return ONE.setScale( scale );
    }
    Set<BigDecimal> results = new HashSet<>();
    while( !results.contains( result ) && result.subtract( previous ).abs().compareTo( precision ) > 0 ) {
      results.add( result );
      previous = result;
      BigDecimal pow = result.pow( exponent - 1 );
      result = BigDecimal.valueOf( exponent - 1.0 )
        .multiply( result )
        .add( radical.divide( pow.compareTo( ZERO ) == 0 ? ONE : pow, scale, ROUND_HALF_DOWN ) )
        .divide( new BigDecimal( exponent ), scale, ROUND_HALF_DOWN );
    }
    return result;
  }
}