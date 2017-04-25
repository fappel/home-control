package com.codeaffine.home.control.admin.ui;

import static com.eclipsesource.tabris.TabrisClientInstaller.install;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

import com.codeaffine.home.control.admin.HomeControlAdminService;

public class AdminUiConfiguration implements ApplicationConfiguration {

  private HomeControlAdminService adminService;

  @Override
  public void configure( Application application ) {
    install( application );
    application.addEntryPoint( "/admin", new HomeControlAdminEntryPointFactory( adminService ), null );
  }

  public void bind( HomeControlAdminService adminService ) {
    this.adminService = adminService;

  }

  public void unbind() {
    adminService = null;
  }
}