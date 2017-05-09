package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static java.lang.Math.max;

import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

class AddElementDialogUtil {

  static Point computeShellSize( Rectangle displayBounds, Rectangle shellBounds ) {
    return new Point( max( shellBounds.width, displayBounds.width / 2 ), shellBounds.height );
  }

  static Point computeShellLocation( Rectangle displayBounds, Point shellSize ) {
    int xLocation = ( displayBounds.width - shellSize.x ) / 2;
    int yLocation = ( displayBounds.height - shellSize.y ) / 2;
    return new Point( xLocation, yLocation );
  }

  static void asyncExec( Runnable runnable ) {
    Display.getCurrent().asyncExec( runnable );
  }

  static void triggerCallback(
    Consumer<Map<String, Object>> additionCallback, AddElementDialog dialog, int returnCode )
  {
    if( returnCode == SWT.OK ) {
      additionCallback.accept( dialog.getAdditionInfo() );
    }
  }

}