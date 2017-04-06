package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.util.SunLightStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.status.supplier.SunPosition;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class SunLightStatusTest {

  public static Object[] provideData() {
    return new Object[] {
      new StatusData<>( NIGHT, TWILIGHT ),
      new StatusData<>( TWILIGHT, DAY ),
    };
  }

  @Test
  @Parameters( source = SunLightStatusTest.class )
  public void valueOf( StatusData<SunLightStatus> data ) {
    double threshold = data.belowThresholdStatus.threshold;

    SunLightStatus actualBelowThreshold = SunLightStatus.valueOf( newSunPosition( threshold ) );
    SunLightStatus actualAboveThreshold = SunLightStatus.valueOf( newSunPosition( threshold + 0.1D ) );

    assertThat( actualBelowThreshold ).isSameAs( data.belowThresholdStatus );
    assertThat( actualAboveThreshold ).isSameAs( data.aboveThresholdStatus );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithNullAsSunPositionArgument() {
    SunLightStatus.valueOf( ( SunPosition )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithArgumentBelowZenitRange() {
    SunLightStatus.valueOf( -90.1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithArgumentAboveZenitRange() {
    SunLightStatus.valueOf( 90.1 );
  }

  private static SunPosition newSunPosition( double threshold ) {
    return new SunPosition( threshold, 0.0D );
  }
}