package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Locale;
import java.util.Set;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.PreferenceView;

class HomeControlAdminEntryPoint implements EntryPoint {

  private final HomeControlAdminService adminService;

  HomeControlAdminEntryPoint( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  @Override
  public int createUI() {
    Locale.setDefault( Locale.ENGLISH );
    Shell shell = new Shell( new Display(), SWT.NO_TRIM );
    shell.setMaximized( true );
    shell.setLayout( new FillLayout() );
    new PreferenceView( shell ).setInput( getPreferenceInfos() );
    shell.open();
    return 0;
  }

  private PreferenceInfo[] getPreferenceInfos() {
    Set<PreferenceInfo> infos = adminService.getPreferenceIntrospection().getPreferenceInfos();
    return infos.toArray( new PreferenceInfo[ infos.size() ] );
  }
}