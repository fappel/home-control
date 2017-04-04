package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.util.AllocationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.application.type.Percent;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class AllocationStatusTest {

  public static Object[] provideData() {
    return new Object[] {
      new StatusData<>( UNUSED, RARE ),
      new StatusData<>( RARE, OCCASIONAL ),
      new StatusData<>( OCCASIONAL, FREQUENT ),
      new StatusData<>( FREQUENT, SUBSTANTIAL ),
      new StatusData<>( SUBSTANTIAL, CONTINUAL ),
      new StatusData<>( CONTINUAL, PERMANENT ),
    };
  }

  @Test
  @Parameters( source = AllocationStatusTest.class )
  public void valueOf( StatusData<AllocationStatus> data ) {
    Percent threshold = data.belowThresholdStatus.threshold;

    AllocationStatus actualBelowThreshold = AllocationStatus.valueOf( threshold );
    AllocationStatus actualAboveThreshold = AllocationStatus.valueOf( Percent.valueOf( threshold.intValue() + 1 ) );

    assertThat( actualBelowThreshold ).isSameAs( data.belowThresholdStatus );
    assertThat( actualAboveThreshold ).isSameAs( data.aboveThresholdStatus );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithNullAsPercentArgument() {
    AllocationStatus.valueOf( ( Percent )null );
  }
}