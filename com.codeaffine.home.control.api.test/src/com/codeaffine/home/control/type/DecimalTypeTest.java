package com.codeaffine.home.control.type;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.test.util.lang.EqualsTester;

public class DecimalTypeTest {

  @Test
  public void constructWithBigDecimal() {
    DecimalType actual = new DecimalType( BigDecimal.ZERO );

    assertThat( actual.toBigDecimal() ).isEqualTo( BigDecimal.ZERO );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsBigDecimal() {
    new DecimalType( ( BigDecimal )null );
  }

  @Test
  public void constructWithLongValue() {
    DecimalType actual = new DecimalType( 0L );

    assertThat( actual.toBigDecimal() ).isEqualTo( BigDecimal.ZERO );
  }

  @Test
  public void constructWithDoubleValue() {
    DecimalType actual = new DecimalType( 0.0D );

    assertThat( actual.toBigDecimal() ).isEqualTo( BigDecimal.valueOf( 0.0D ) );
  }

  @Test
  public void constructWithString() {
    DecimalType actual = new DecimalType( "0" );

    assertThat( actual.toBigDecimal() ).isEqualTo( BigDecimal.ZERO );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsString() {
    new DecimalType( ( String )null );
  }

  @Test
  public void intValue() {
    DecimalType decimalType = new DecimalType( Integer.MAX_VALUE );

    int actual = decimalType.intValue();

    assertThat( actual ).isEqualTo( Integer.MAX_VALUE );
  }

  @Test
  public void longValue() {
    DecimalType decimalType = new DecimalType( Long.MAX_VALUE );

    long actual = decimalType.longValue();

    assertThat( actual ).isEqualTo( Long.MAX_VALUE );
  }

  @Test
  public void floatValue() {
    DecimalType decimalType = new DecimalType( Float.MAX_VALUE );

    float actual = decimalType.floatValue();

    assertThat( actual ).isEqualTo( Float.MAX_VALUE );
  }

  @Test
  public void doubleValue() {
    DecimalType decimalType = new DecimalType( Double.MAX_VALUE );

    double actual = decimalType.doubleValue();

    assertThat( actual ).isEqualTo( Double.MAX_VALUE );
  }

  @Test
  public void toBigDecimal() {
    DecimalType decimalType = new DecimalType( BigDecimal.ZERO );

    BigDecimal actual = decimalType.toBigDecimal();

    assertThat( actual ).isSameAs( BigDecimal.ZERO );
  }

  @Test
  public void serialize() {
    DecimalType origial = new DecimalType( Double.MAX_VALUE );

    String value = origial.toString();
    DecimalType restoration = new DecimalType( value );

    assertThat( origial ).isEqualTo( restoration );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<DecimalType> tester = EqualsTester.newInstance( DecimalType.ZERO );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new DecimalType( 2 ), new DecimalType( "2" ), new DecimalType( 2L ) );
    tester.assertEqual( new DecimalType( 2.0F ), new DecimalType( "2.0" ), new DecimalType( 2D ) );
    tester.assertNotEqual( new DecimalType( 2 ), new DecimalType( 3 ) );
  }

  @Test
  public void compareTo() {
    assertThat( DecimalType.ZERO.compareTo( new DecimalType( 0.0 ) ) ).isEqualTo( 0 );
    assertThat( DecimalType.ZERO.compareTo( new DecimalType( 0.1 ) ) ).isLessThan( 0 );
    assertThat( DecimalType.ZERO.compareTo( new DecimalType( -0.1 ) ) ).isGreaterThan( 0 );
  }
}