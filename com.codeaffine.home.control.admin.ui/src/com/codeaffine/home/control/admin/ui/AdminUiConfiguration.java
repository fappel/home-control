package com.codeaffine.home.control.admin.ui;

import static com.eclipsesource.tabris.TabrisClientInstaller.install;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

public class AdminUiConfiguration implements ApplicationConfiguration {

  @Override
  public void configure( Application application ) {
    install( application );
    application.addEntryPoint( "/admin", HomeControlAdminEntryPoint.class, null );
  }
}