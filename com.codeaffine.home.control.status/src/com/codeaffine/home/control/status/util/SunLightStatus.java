package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.util.Messages.ZENIT_VALUE_OUT_OF_RANGE;
import static com.codeaffine.util.ArgumentVerification.*;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.status.supplier.SunPosition;

public enum SunLightStatus {
  NIGHT( -18.0D ), TWILIGHT ( 0.0D ), DAY ( 90.0D );

  public final double threshold;

  private SunLightStatus( double threshold ) {
    this.threshold = threshold;
  }

  public static SunLightStatus valueOf( SunPosition value ) {
    verifyNotNull( value, "value" );

    return valueOf( value.getZenit() );
  }

  public static SunLightStatus valueOf( double value ) {
    verifyCondition( value >= -90.0D && value <= +90D, ZENIT_VALUE_OUT_OF_RANGE, Double.valueOf( value ) );

    Predicate<SunLightStatus> exceedsThresholdFilter = status -> value <= status.threshold;
    return Stream.of( values() ).filter( exceedsThresholdFilter ).findFirst().get();
  }
}