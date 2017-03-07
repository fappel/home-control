package com.codeaffine.home.control.application.test;

import com.codeaffine.home.control.application.control.StatusProvider;

public class MyStatusProvider implements StatusProvider<MyStatus> {

  private MyStatus status;

  @Override
  public MyStatus getStatus() {
    return status;
  }

  public void setStatus( MyStatus status ) {
    this.status = status;
  }
}