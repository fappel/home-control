package com.codeaffine.home.control.application.internal.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class UtilTest {

  @Test
  public void calculateMaxActivations() {
    BigDecimal actual = Util.calculateMaxActivations( 5L, 3l );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }
}
