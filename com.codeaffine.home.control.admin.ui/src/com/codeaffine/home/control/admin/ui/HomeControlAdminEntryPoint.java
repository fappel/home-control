package com.codeaffine.home.control.admin.ui;

import java.util.Locale;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class HomeControlAdminEntryPoint implements EntryPoint {

  @Override
  public int createUI() {
    Locale.setDefault( Locale.ENGLISH );
    Shell shell = new Shell( new Display(), SWT.NO_TRIM );
    shell.setMaximized( true );
    shell.setLayout( new FillLayout() );

    Label label = new Label( shell, SWT.NONE );
    label.setText( "Hello Home Control World." );

    shell.open();
    return 0;
  }
}
