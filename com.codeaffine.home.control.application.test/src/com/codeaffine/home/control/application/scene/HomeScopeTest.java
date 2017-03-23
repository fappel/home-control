package com.codeaffine.home.control.application.scene;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HomeScopeTest {

  private static final int ZERO_SIGNIFIER_OF_EQUAL = 0;

  @Test
  public void global() {
    assertThat( HomeScope.GLOBAL.getName() ).isEqualTo( HomeScope.GLOBAL_NAME );
    assertThat( HomeScope.GLOBAL.getName() ).isNotEqualTo( HomeScope.LIVING_ROOM.getName() );
    assertThat( HomeScope.GLOBAL.getOrdinal() ).isEqualTo( HomeScope.GLOBAL_ORDINAL );
  }

  @Test
  public void hotspot() {
    assertThat( HomeScope.LIVING_ROOM.getName() ).isEqualTo( HomeScope.LIVING_ROOM_NAME );
    assertThat( HomeScope.LIVING_ROOM.getName() ).isNotEqualTo( HomeScope.GLOBAL.getName() );
    assertThat( HomeScope.LIVING_ROOM.getOrdinal() ).isEqualTo( HomeScope.LIVING_ROOM_ORDINAL );
  }

  @Test
  public void compareTo() {
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.GLOBAL ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.LIVING_ROOM ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.LIVING_ROOM ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.GLOBAL ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
  }
}