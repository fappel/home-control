package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.application.control.StatusProvider;
import com.codeaffine.home.control.application.type.Percent;

public interface ActivityProvider extends StatusProvider<Percent> {

  @Override
  Percent getStatus();
}