package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.status.StatusProvider;

public interface ActivityProvider extends StatusProvider<Activity> {

  @Override
  Activity getStatus();
}