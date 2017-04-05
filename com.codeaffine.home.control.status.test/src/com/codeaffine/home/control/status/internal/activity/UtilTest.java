package com.codeaffine.home.control.status.internal.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.codeaffine.home.control.status.internal.activity.Util;

public class UtilTest {

  @Test
  public void calculateMaxActivations() {
    BigDecimal actual = Util.calculateMaxActivations( 5L, 3l );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }
}
