package com.codeaffine.home.control.status.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.status.supplier.SunPosition;
import com.codeaffine.test.util.lang.EqualsTester;

public class SunPositionTest {

  private static final double AZIMUTH = 122.7;
  private static final double ZENIT = 8.9;

  @Test
  public void construction() {
    SunPosition sunPosition = new SunPosition( ZENIT, AZIMUTH );

    assertThat( sunPosition.getZenit() ).isEqualTo( ZENIT );
    assertThat( sunPosition.getAzimuth() ).isEqualTo( AZIMUTH );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<SunPosition> instance = EqualsTester.newInstance( new SunPosition( 0.0, 0.0 ) );
    instance.assertEqual( new SunPosition( ZENIT, AZIMUTH ), new SunPosition( ZENIT, AZIMUTH ) );
    instance.assertNotEqual( new SunPosition( ZENIT, AZIMUTH ), new SunPosition( ZENIT, 10.0 ) );
    instance.assertNotEqual( new SunPosition( ZENIT, AZIMUTH ), new SunPosition( 4.0, AZIMUTH ) );
    instance.assertImplementsEqualsAndHashCode();
  }

  @Test
  public void toStringImplementation() {
    String actual = new SunPosition( ZENIT, AZIMUTH ).toString();

    assertThat( actual )
      .contains( String.valueOf( ZENIT ) )
      .contains( String.valueOf( AZIMUTH ) )
      .contains( SunPosition.class.getSimpleName() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithTooLargeZenitValue() {
    new SunPosition( SunPosition.MAX_ZENIT + 0.0000001, AZIMUTH );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithTooSmallZenitValue() {
    new SunPosition( SunPosition.MIN_ZENIT - 0.0000001, AZIMUTH );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithTooLargeAzimuthValue() {
    new SunPosition( ZENIT, SunPosition.MAX_AZIMUTH + 0.0000001 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithTooSmallAzimuthValue() {
    new SunPosition( ZENIT, SunPosition.MIN_AZIMUTH - 0.0000001 );
  }
}