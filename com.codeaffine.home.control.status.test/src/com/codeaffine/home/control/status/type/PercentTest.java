package com.codeaffine.home.control.status.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.status.type.Percent;

public class PercentTest {

  @Test
  public void toStringImplementation() {
    assertThat( Percent.P_000.toString() ).isEqualTo( "0%" );
    assertThat( Percent.P_011.toString() ).isEqualTo( "11%" );
    assertThat( Percent.P_100.toString() ).isEqualTo( "100%" );
  }

  @Test
  public void intValue() {
    int actual = Percent.P_004.intValue();

    assertThat( actual ).isEqualTo( 4 );
  }

  @Test
  public void valueOf() {
    Percent actual = Percent.valueOf( 3 );

    assertThat( actual ).isSameAs( Percent.P_003 );
  }

  @Test
  public void valueOfLowerBound() {
    Percent actual = Percent.valueOf( 0 );

    assertThat( actual ).isSameAs( Percent.P_000 );
  }

  @Test
  public void valueOfUpperBound() {
    Percent actual = Percent.valueOf( 100 );

    assertThat( actual ).isSameAs( Percent.P_100 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfIfValueIsTooSmall() {
    Percent.valueOf( -1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfIfValueIsTooLarge() {
    Percent.valueOf( 101 );
  }
}