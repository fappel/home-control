package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.type.Percent;

public enum AllocationStatus {
  UNUSED( P_000 ),
  RARE( P_009 ),
  OCCASIONAL( P_019 ),
  FREQUENT( P_029 ),
  SUBSTANTIAL( P_049 ),
  CONTINUAL( P_079 ),
  PERMANENT( P_100 );

  final Percent threshold;

  private AllocationStatus( Percent threshold ) {
    this.threshold = threshold;
  }

  public static AllocationStatus valueOf( Percent value ) {
    verifyNotNull( value, "value" );

    Predicate<AllocationStatus> exceedsThresholdFilter = status -> value.compareTo( status.threshold ) <= 0 ;
    return Stream.of( values() ).filter( exceedsThresholdFilter ).findFirst().get();
  }
}