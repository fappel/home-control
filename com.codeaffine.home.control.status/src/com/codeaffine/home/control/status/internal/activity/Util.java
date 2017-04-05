package com.codeaffine.home.control.status.internal.activity;

import static java.math.BigDecimal.ROUND_HALF_UP;

import java.math.BigDecimal;

class Util {

  static BigDecimal calculateMaxActivations( long observationTimeFrame, long calculationIntervalDuration ) {
    return new BigDecimal( observationTimeFrame * 60 )
      .divide( new BigDecimal( calculationIntervalDuration ), 2, ROUND_HALF_UP );
  }
}