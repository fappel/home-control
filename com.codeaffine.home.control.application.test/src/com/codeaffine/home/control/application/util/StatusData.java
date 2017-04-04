package com.codeaffine.home.control.application.util;

class StatusData<T> {

  final T belowThresholdStatus;
  final T aboveThresholdStatus;

  StatusData( T belowThresholdStatus, T aboveThresholdStatus ) {
    this.belowThresholdStatus = belowThresholdStatus;
    this.aboveThresholdStatus = aboveThresholdStatus;
  }
}