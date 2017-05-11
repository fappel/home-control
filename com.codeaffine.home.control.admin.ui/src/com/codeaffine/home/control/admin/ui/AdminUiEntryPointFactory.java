package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.ui.internal.console.ConsoleContribution;
import com.codeaffine.home.control.admin.ui.preference.PreferenceContribution;

class AdminUiEntryPointFactory implements EntryPointFactory {

  private final HomeControlAdminService adminService;

  AdminUiEntryPointFactory( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  @Override
  public EntryPoint create() {
    return new AdminUiEntryPoint( getPageContributions() );
  }

  private List<PageContribution> getPageContributions() {
    return Arrays.asList( new PreferenceContribution( adminService ), new ConsoleContribution() );
  }
}
