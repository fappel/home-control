package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.status.type.Percent;

public enum ActivityStatus {
  IDLE( P_000 ), QUIET( P_009 ), AROUSED( P_019 ), LIVELY( P_029 ), BRISK( P_049 ), BUSY( P_079 ), RUSH( P_100 );

  public final Percent threshold;

  private ActivityStatus( Percent threshold ) {
    this.threshold = threshold;
  }

  public static ActivityStatus valueOf( Percent value ) {
    verifyNotNull( value, "value" );

    Predicate<ActivityStatus> exceedsThresholdFilter = status -> value.compareTo( status.threshold ) <= 0;
    return Stream.of( values() ).filter( exceedsThresholdFilter ).findFirst().get();
  }
}