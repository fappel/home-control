package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.util.ActivityStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.status.type.Percent;
import com.codeaffine.home.control.status.util.ActivityStatus;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class ActivityStatusTest {

  public static Object[] provideData() {
    return new Object[] {
      new StatusData<>( IDLE, QUIET ),
      new StatusData<>( QUIET, AROUSED ),
      new StatusData<>( AROUSED, LIVELY ),
      new StatusData<>( LIVELY, BRISK ),
      new StatusData<>( BRISK, BUSY ),
      new StatusData<>( BUSY, RUSH ),
    };
  }

  @Test
  @Parameters( source = ActivityStatusTest.class )
  public void valueOf( StatusData<ActivityStatus> data ) {
    Percent threshold = data.belowThresholdStatus.threshold;

    ActivityStatus actualBelowThreshold = ActivityStatus.valueOf( threshold );
    ActivityStatus actualAboveThreshold = ActivityStatus.valueOf( Percent.valueOf( threshold.intValue() + 1 ) );

    assertThat( actualBelowThreshold ).isSameAs( data.belowThresholdStatus );
    assertThat( actualAboveThreshold ).isSameAs( data.aboveThresholdStatus );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithNullAsPercentArgument() {
    ActivityStatus.valueOf( ( Percent )null );
  }
}