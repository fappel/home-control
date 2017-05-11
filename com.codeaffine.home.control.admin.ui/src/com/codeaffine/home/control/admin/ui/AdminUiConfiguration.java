package com.codeaffine.home.control.admin.ui;

import static com.eclipsesource.tabris.TabrisClientInstaller.install;
import static org.eclipse.rap.rwt.RWT.DEFAULT_THEME_ID;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

import com.codeaffine.home.control.admin.HomeControlAdminService;

public class AdminUiConfiguration implements ApplicationConfiguration {

  private static final String STANDARD_THEME_EXTRENSION_FILE_LOCATION = "theme/default-theme-extension.css";

  private HomeControlAdminService adminService;

  @Override
  public void configure( Application application ) {
    install( application );
    application.addStyleSheet( DEFAULT_THEME_ID, STANDARD_THEME_EXTRENSION_FILE_LOCATION );
    application.addEntryPoint( "/admin", new AdminUiEntryPointFactory( adminService ), null );
  }

  public void bindAdminService( HomeControlAdminService adminService ) {
    this.adminService = adminService;

  }

  public void unbindAdminService( @SuppressWarnings("unused") HomeControlAdminService adminService ) {
    this.adminService = null;
  }
}