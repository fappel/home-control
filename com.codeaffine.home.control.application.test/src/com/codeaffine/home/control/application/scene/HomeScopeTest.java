package com.codeaffine.home.control.application.scene;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HomeScopeTest {

  private static final int ZERO_SIGNIFIER_OF_EQUAL = 0;

  @Test
  public void global() {
    assertThat( HomeScope.GLOBAL.getName() ).isEqualTo( HomeScope.GLOBAL_NAME );
    assertThat( HomeScope.GLOBAL.getName() ).isNotEqualTo( HomeScope.LIVING_ROOM.getName() );
    assertThat( HomeScope.GLOBAL.getName() ).isNotEqualTo( HomeScope.KITCHEN.getName() );
    assertThat( HomeScope.GLOBAL.getName() ).isNotEqualTo( HomeScope.BED_ROOM.getName() );
    assertThat( HomeScope.GLOBAL.getOrdinal() ).isEqualTo( HomeScope.GLOBAL_ORDINAL );
  }

  @Test
  public void bedroom() {
    assertThat( HomeScope.BED_ROOM.getName() ).isEqualTo( HomeScope.BED_ROOM_NAME );
    assertThat( HomeScope.BED_ROOM.getName() ).isNotEqualTo( HomeScope.KITCHEN.getName() );
    assertThat( HomeScope.BED_ROOM.getName() ).isNotEqualTo( HomeScope.LIVING_ROOM.getName() );
    assertThat( HomeScope.BED_ROOM.getName() ).isNotEqualTo( HomeScope.GLOBAL.getName() );
    assertThat( HomeScope.BED_ROOM.getOrdinal() ).isEqualTo( HomeScope.BED_ROOM_ORDINAL );
  }

  @Test
  public void kitchen() {
    assertThat( HomeScope.KITCHEN.getName() ).isEqualTo( HomeScope.KITCHEN_NAME );
    assertThat( HomeScope.KITCHEN.getName() ).isNotEqualTo( HomeScope.LIVING_ROOM.getName() );
    assertThat( HomeScope.KITCHEN.getName() ).isNotEqualTo( HomeScope.BED_ROOM.getName() );
    assertThat( HomeScope.KITCHEN.getName() ).isNotEqualTo( HomeScope.GLOBAL.getName() );
    assertThat( HomeScope.KITCHEN.getOrdinal() ).isEqualTo( HomeScope.KITCHEN_ORDINAL );
  }

  @Test
  public void livingRoom() {
    assertThat( HomeScope.LIVING_ROOM.getName() ).isEqualTo( HomeScope.LIVING_ROOM_NAME );
    assertThat( HomeScope.LIVING_ROOM.getName() ).isNotEqualTo( HomeScope.GLOBAL.getName() );
    assertThat( HomeScope.LIVING_ROOM.getName() ).isNotEqualTo( HomeScope.KITCHEN.getName() );
    assertThat( HomeScope.LIVING_ROOM.getName() ).isNotEqualTo( HomeScope.BED_ROOM.getName() );
    assertThat( HomeScope.LIVING_ROOM.getOrdinal() ).isEqualTo( HomeScope.LIVING_ROOM_ORDINAL );
  }

  @Test
  public void compareTo() {
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.GLOBAL ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.BED_ROOM ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.KITCHEN ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.GLOBAL.compareTo( HomeScope.LIVING_ROOM ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.BED_ROOM.compareTo( HomeScope.GLOBAL ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.BED_ROOM.compareTo( HomeScope.BED_ROOM ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.BED_ROOM.compareTo( HomeScope.KITCHEN ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.BED_ROOM.compareTo( HomeScope.LIVING_ROOM ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.KITCHEN.compareTo( HomeScope.GLOBAL ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.KITCHEN.compareTo( HomeScope.BED_ROOM ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.KITCHEN.compareTo( HomeScope.KITCHEN ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.KITCHEN.compareTo( HomeScope.LIVING_ROOM ) ).isLessThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.GLOBAL ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.BED_ROOM ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.KITCHEN ) ).isGreaterThan( ZERO_SIGNIFIER_OF_EQUAL );
    assertThat( HomeScope.LIVING_ROOM.compareTo( HomeScope.LIVING_ROOM ) ).isEqualTo( ZERO_SIGNIFIER_OF_EQUAL );
  }

  @Test
  public void values() {
    HomeScope[] actual = HomeScope.values();

    assertThat( actual )
      .containsExactly( HomeScope.GLOBAL, HomeScope.BED_ROOM, HomeScope.KITCHEN, HomeScope.LIVING_ROOM );
  }
}