package com.codeaffine.home.control.application.scene;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class RootMathTest {

  private static final BigDecimal VALUE = new BigDecimal( 100 );
  private static final int SCALE = 2;
  private static final int EXPONENT = 20;

  @Test
  public void nthRootOf() {
    BigDecimal radical = VALUE.pow( EXPONENT );

    BigDecimal actual = RootMath.nthRootOf( radical, EXPONENT, SCALE );

    assertThat( actual ).isEqualTo( VALUE.setScale( SCALE ) );
  }

  @Test
  public void nthRootOfWithZeroAsExponent() {
    BigDecimal radical = VALUE.pow( EXPONENT );

    BigDecimal actual = RootMath.nthRootOf( radical, ZERO.intValue(), SCALE );

    assertThat( actual ).isEqualTo( ONE.setScale( SCALE ) );
  }

  @Test
  public void nthRootOfWithZeroAsRadical() {
    BigDecimal actual = RootMath.nthRootOf( ZERO, EXPONENT, SCALE );

    assertThat( actual ).isSameAs( ZERO.setScale( SCALE ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void nthRootOfWithNullAsRadicalArgument() {
    RootMath.nthRootOf( null, EXPONENT, SCALE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void nthRootOfWithNegativeRadicalArgument() {
    RootMath.nthRootOf( BigDecimal.valueOf( -1 ), EXPONENT, SCALE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void nthRootOfWithNegativeExponentArgument() {
    RootMath.nthRootOf( ZERO, -1, SCALE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void nthRootOfWithNegativeScaleArgument() {
    RootMath.nthRootOf( ZERO, EXPONENT, -1 );
  }
}