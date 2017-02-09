package com.codeaffine.home.control.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.codeaffine.home.control.type.PercentType;

public class PercentTypeTest {

  @Test
  public void constructWithBigDecimalOnLowerBound() {
    PercentType actual = new PercentType( BigDecimal.ZERO );

    assertThat( actual.intValue() ).isEqualTo( 0 );
  }

  @Test
  public void constructWithBigDecimalOnUpperBound() {
    PercentType actual = new PercentType( new BigDecimal( 100 ) );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithBigDecimalThatIsTooSmall() {
    new PercentType( new BigDecimal( -1 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithBigDecimalThatIsTooLarge() {
    new PercentType( new BigDecimal( 101 ) );
  }

  @Test
  public void constructWithLongOnLowerBound() {
    PercentType actual = new PercentType( 0L );

    assertThat( actual.intValue() ).isEqualTo( 0 );
  }

  @Test
  public void constructWithLongOnUpperBound() {
    PercentType actual = new PercentType( 100L );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithLongThatIsTooSmall() {
    new PercentType( -1L );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithLongThatIsTooLarge() {
    new PercentType( 101L );
  }

  @Test
  public void constructWithDoubleOnLowerBound() {
    PercentType actual = new PercentType( 0.0D );

    assertThat( actual.intValue() ).isEqualTo( 0 );
  }

  @Test
  public void constructWithDoubleOnUpperBound() {
    PercentType actual = new PercentType( 100.0D );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithDoubleThatIsTooSmall() {
    new PercentType( -0.1D );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithDoubleThatIsTooLarge() {
    new PercentType( 100.1D );
  }

  @Test
  public void constructWithStringRepresentationOnLowerBound() {
    PercentType actual = new PercentType( "0" );

    assertThat( actual.intValue() ).isEqualTo( 0 );
  }

  @Test
  public void constructWithStringRepresentationOnUpperBound() {
    PercentType actual = new PercentType( "100" );

    assertThat( actual.intValue() ).isEqualTo( 100 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithStringRepresentationThatIsTooSmall() {
    new PercentType( "-1" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithStringRepresentationThatIsTooLarge() {
    new PercentType( "101" );
  }

}