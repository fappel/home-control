package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.Messages.ERROR_NOT_INITIALIZED;

import com.codeaffine.home.control.ComponentAccessService;

public class HomeControlAdminService {

  private ComponentAccessService componentAccessService;

  public String getName() {
    if( componentAccessService != null ) {
      return "Hello World from the Home Control Admin Service";
    }
    throw new IllegalStateException( ERROR_NOT_INITIALIZED );
  }

  public void bind( ComponentAccessService componentAccessService ) {
    this.componentAccessService = componentAccessService;
  }

  public void unbind() {
    componentAccessService = null;
  }
}
