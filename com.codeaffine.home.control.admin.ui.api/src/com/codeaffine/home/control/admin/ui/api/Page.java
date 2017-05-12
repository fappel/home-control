package com.codeaffine.home.control.admin.ui.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface Page {
  String getId();
  Control createContent( Composite parent );
  void setFocus();
  default void dispose() {}
}
