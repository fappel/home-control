package com.codeaffine.home.control.admin.ui.console;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.api.PageFactory;

public class ConsolePageFactory implements PageFactory {

  private HomeControlAdminService adminService;

  @Override
  public Page create() {
    return new ConsolePage( adminService.getPreference( ConsolePreference.class ) );
  }

  public void bindHomeControlAdminService( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  public void unbindHomeControlAdminService( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = null;
  }
}