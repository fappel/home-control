package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.rap.rwt.RWT.getUISession;
import static org.eclipse.swt.SWT.NO_TRIM;

import java.util.Locale;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.admin.ui.model.ActionMap;
import com.codeaffine.home.control.admin.ui.view.AdminUiPreference;
import com.codeaffine.home.control.admin.ui.view.AdminUiView;
import com.codeaffine.home.control.admin.ui.view.DynamicViewControl;
import com.codeaffine.home.control.admin.ui.view.ViewContentLifeCycle;

class AdminUiEntryPoint implements EntryPoint {

  private final HomeControlAdminService adminService;
  private final PageFactorySupplier pageFactories;

  AdminUiEntryPoint( PageFactorySupplier pageFactories, HomeControlAdminService adminService ) {
    verifyNotNull( pageFactories, "pageFactories" );
    verifyNotNull( adminService, "adminService" );

    this.pageFactories = pageFactories;
    this.adminService = adminService;
  }

  @Override
  public int createUI() {
    configureSession();
    showUi( prepareView() );
    return 0;
  }

  private static void configureSession() {
    Locale.setDefault( Locale.ENGLISH );
    getUISession().getHttpSession().setMaxInactiveInterval( 0 );
  }

  private DynamicViewControl prepareView() {
    ActionMap actionMap = new ActionMap();
    AdminUiView view = new AdminUiView( actionMap, adminService.getPreference( AdminUiPreference.class ) );
    ViewContentLifeCycle lifeCycle = new ViewContentLifeCycle( view, pageFactories, actionMap );
    return new DynamicViewControl( lifeCycle, pageFactories, getUISession(), new ServerPushSession() );
  }

  private void showUi( DynamicViewControl dynamicViewControl ) {
    observeAdminUiPreferenceChanges( dynamicViewControl );
    Shell shell = createShell( new FillLayout() );
    dynamicViewControl.createContent( shell );
    shell.open();
  }

  private void observeAdminUiPreferenceChanges( DynamicViewControl dynamicViewControl ) {
    adminService.registerEventObserver( dynamicViewControl );
    getUISession().addUISessionListener( evt -> adminService.unregisterEventObserver( dynamicViewControl ) );
  }

  private static Shell createShell( Layout layout ) {
    Shell result = new Shell( new Display(), NO_TRIM );
    result.setMaximized( true );
    result.setLayout( layout );
    return result;
  }
}