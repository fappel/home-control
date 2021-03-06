package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static java.lang.Math.max;

import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;

class AddElementDialogUtil {

  static Point computeShellSize( Rectangle displayBounds, Point oldSize ) {
    return new Point( max( oldSize.x, displayBounds.width / 2 ), oldSize.y );
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

  static boolean isValidElementPartKey( String elementPartKey ) {
    return    elementPartKey.equals( ModifyAdapter.ADDITION_INFO_KEY ) 
           || elementPartKey.equals( ModifyAdapter.ADDITION_INFO_VALUE );
  }

}