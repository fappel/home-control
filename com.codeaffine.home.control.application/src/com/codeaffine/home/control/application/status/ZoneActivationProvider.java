package com.codeaffine.home.control.application.status;

import java.util.Set;

import com.codeaffine.home.control.application.control.StatusProvider;

public interface ZoneActivationProvider extends StatusProvider<Set<ZoneActivation>> {

  @Override
  Set<ZoneActivation> getStatus();
}