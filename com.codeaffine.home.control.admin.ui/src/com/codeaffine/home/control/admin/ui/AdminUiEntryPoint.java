package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.rap.rwt.RWT.getUISession;
import static org.eclipse.swt.SWT.NO_TRIM;

import java.util.Locale;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.admin.ui.model.ActionMap;
import com.codeaffine.home.control.admin.ui.view.AdminUiView;
import com.codeaffine.home.control.admin.ui.view.DynamicViewControl;
import com.codeaffine.home.control.admin.ui.view.ViewContentLifeCycle;

class AdminUiEntryPoint implements EntryPoint {

  private final PageFactorySupplier pageFactories;

  AdminUiEntryPoint( PageFactorySupplier pageFactories ) {
    verifyNotNull( pageFactories, "pageFactories" );

    this.pageFactories = pageFactories;
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
    AdminUiView view = new AdminUiView( actionMap );
    ViewContentLifeCycle lifeCycle = new ViewContentLifeCycle( view, pageFactories, actionMap );
    return new DynamicViewControl( lifeCycle, pageFactories, getUISession() );
  }

  private static void showUi( DynamicViewControl dynamicViewControl ) {
    Shell shell = createShell( new FillLayout() );
    dynamicViewControl.createContent( shell );
    shell.open();
  }

  private static Shell createShell( Layout layout ) {
    Shell result = new Shell( new Display(), NO_TRIM );
    result.setMaximized( true );
    result.setLayout( layout );
    return result;
  }
}