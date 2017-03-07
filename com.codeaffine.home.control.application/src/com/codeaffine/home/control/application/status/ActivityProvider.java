package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.status.StatusProvider;

public interface ActivityProvider extends StatusProvider<Percent> {

  @Override
  Percent getStatus();
}