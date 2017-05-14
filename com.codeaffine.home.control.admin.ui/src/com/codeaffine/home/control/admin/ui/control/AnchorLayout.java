package com.codeaffine.home.control.admin.ui.control;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

class AnchorLayout extends Layout {

  private final Composite control;
  private final Label label;

  AnchorLayout( Composite control, Label label ) {
    this.control = control;
    this.label = label;
  }

  @Override
  protected void layout( Composite composite, boolean flushCache ) {
    label.pack();
    label.setLocation( computeLabelLocation( control.getClientArea(), label.getSize() ) );
  }

  @Override
  protected Point computeSize( Composite composite, int wHint, int hHint, boolean flushCache ) {
    return label.computeSize( wHint, hHint );
  }

  static Point computeLabelLocation( Rectangle clientArea, Point size ) {
    int x = ( clientArea.width - size.x ) / 2;
    int y = ( clientArea.height - size.y ) / 2;
    return new Point( x, y );
  }
}