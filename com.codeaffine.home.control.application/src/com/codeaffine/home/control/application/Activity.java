package com.codeaffine.home.control.application;

import com.codeaffine.home.control.application.control.StatusProvider;
import com.codeaffine.home.control.application.type.Percent;

public interface Activity extends StatusProvider<Percent> {

  @Override
  Percent getStatus();
}