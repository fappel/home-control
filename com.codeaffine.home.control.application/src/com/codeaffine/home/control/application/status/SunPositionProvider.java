package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.application.control.StatusProvider;

public interface SunPositionProvider extends StatusProvider<SunPosition> {
  @Override
  SunPosition getStatus();
}