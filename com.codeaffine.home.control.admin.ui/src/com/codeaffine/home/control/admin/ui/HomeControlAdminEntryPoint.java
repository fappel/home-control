package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.home.control.admin.ui.internal.util.FormDatas.attach;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Locale;
import java.util.Set;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.PreferenceView;

class HomeControlAdminEntryPoint implements EntryPoint {

  private static final int MARGIN = 20;

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
    shell.setLayout( new FormLayout() );

    Composite banner = new Composite( shell, SWT.NONE );
    banner.setLayout( new FormLayout() );
    attach( banner ).toLeft().toTop().toRight().withHeight( 70 );
    Label logo = new Label( banner, SWT.NONE );
    attach( logo ).toLeft( MARGIN ).toTop( 15 );
    logo.setText( "Home Control" );
    logo.setData( RWT.CUSTOM_VARIANT, "logo" );
    Label appName = new Label( banner, SWT.NONE );
    attach( appName ).toTop( MARGIN ).toRight( MARGIN );
    appName.setText( "Administration" );
    appName.setData( RWT.CUSTOM_VARIANT, "applicationName" );
    Label separator = new Label( banner, SWT.NONE );
    attach( separator ).toLeft().toBottom().toRight().withHeight( 1 );
    separator.setData( RWT.CUSTOM_VARIANT, "bannerSeparator" );



    PreferenceView preferenceView = new PreferenceView( shell );
    preferenceView.setInput( getPreferenceInfos() );
    Control control = preferenceView.getControl();
    attach( control ).toLeft( MARGIN ).atTopTo( banner, MARGIN ).toRight( MARGIN ).toBottom( MARGIN );

    shell.open();
    return 0;
  }

  private PreferenceInfo[] getPreferenceInfos() {
    Set<PreferenceInfo> infos = adminService.getPreferenceIntrospection().getPreferenceInfos();
    return infos.toArray( new PreferenceInfo[ infos.size() ] );
  }
}