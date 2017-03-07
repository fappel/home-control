package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.status.StatusProvider;

public interface SunPositionProvider extends StatusProvider<SunPosition> {
  @Override
  SunPosition getStatus();
}