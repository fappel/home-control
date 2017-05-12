package com.codeaffine.home.control.admin.ui.preference;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.api.PageFactory;

public class PreferencePageFactory implements PageFactory {

  private HomeControlAdminService adminService;

  @Override
  public Page create() {
    return new PreferencePage( adminService );
  }

  public void bindAdminService( HomeControlAdminService adminService ) {
    this.adminService = adminService;
  }

  public void unbindAdminService( @SuppressWarnings("unused") HomeControlAdminService adminService ) {
    this.adminService = null;
  }
}