package com.codeaffine.home.control.admin.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface PageContribution {
  String getId();
  Control createContent( Composite parent );
  void setFocus();
}
