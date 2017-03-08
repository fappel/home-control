package com.codeaffine.home.control.application.scene;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HomeScopeTest {

  private static final int ZERO_SIGNIFIER_OF_EQUAL = 0;

  @Test
  public void global() {
    assertThat( HomeScope.GLOBAL.getName() ).isEqualTo( HomeScope.GLOBAL_NAME );
    assertThat( HomeScope.GLOBAL.getName() ).isNotEqualTo( HomeScope.HOTSPOT.getName() );
    assertThat( HomeScope.GLOBAL.getOrdinal() ).isEqualTo( HomeScope.GLOBAL_ORDINAL );
  }

  @Test
  public void hotspot() {
    assertThat( HomeScope.HOTSPOT.getName() ).isEqualTo( HomeScope.HOTSPOT_NAME );
    assertThat( HomeScope.HOTSPOT.getName() ).isNotEqualTo( HomeScope.GLOBAL.getName() );
    assertThat( HomeScope.HOTSPOT.getOrdinal() ).isEqualTo( HomeScope.HOTSPOT_ORDINAL );
  }

  @Test
  public void compareTo() {
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.GLOBAL ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.HOTSPOT.compareTo( HomeScope.HOTSPOT ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.HOTSPOT ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.HOTSPOT.compareTo( HomeScope.GLOBAL ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
  }
}