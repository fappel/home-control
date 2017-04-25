package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;

import com.codeaffine.home.control.admin.HomeControlAdminService;

public class HomeControlAdminEntryPointFactory implements EntryPointFactory {

  private final HomeControlAdminService adminService;

  public HomeControlAdminEntryPointFactory( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  @Override
  public EntryPoint create() {
    return new HomeControlAdminEntryPoint( adminService );
  }
}
