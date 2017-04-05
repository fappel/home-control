package com.codeaffine.home.control.test.util.status;

import com.codeaffine.home.control.status.StatusSupplier;

public class MyStatusSupplier implements StatusSupplier<MyStatus> {

  private MyStatus status;

  @Override
  public MyStatus getStatus() {
    return status;
  }

  public void setStatus( MyStatus status ) {
    this.status = status;
  }
}