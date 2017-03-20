package com.codeaffine.home.control.application.status;

import com.codeaffine.home.control.status.StatusProvider;

public interface ActivationProvider extends StatusProvider<Activation> {

  @Override
  Activation getStatus();
}