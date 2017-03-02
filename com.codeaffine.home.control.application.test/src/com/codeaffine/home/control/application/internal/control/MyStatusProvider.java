package com.codeaffine.home.control.application.internal.control;

import com.codeaffine.home.control.application.control.StatusProvider;

class MyStatusProvider implements StatusProvider<Status> {

  private Status status;

  @Override
  public Status getStatus() {
    return status;
  }

  void setStatus( Status status ) {
    this.status = status;
  }
}