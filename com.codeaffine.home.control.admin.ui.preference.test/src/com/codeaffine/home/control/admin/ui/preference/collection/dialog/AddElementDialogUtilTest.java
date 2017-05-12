package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.*;
import static com.codeaffine.home.control.admin.ui.test.util.DisplayHelper.flushPendingEvents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.swt.SWT.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class AddElementDialogUtilTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  @Test
  public void computeShellSize() {
    Rectangle displayBounds = new Rectangle( 0, 0, 400, 200 );
    Point oldSize = new Point( 20, 10 );

    Point actual = AddElementDialogUtil.computeShellSize( displayBounds, oldSize );

    assertThat( actual ).isEqualTo( new Point( 200, 10 ) );
  }

  @Test
  public void computeShellSizeOnSmallDisplay() {
    Rectangle displayBounds = new Rectangle( 0, 0, 30, 20 );
    Point oldSize = new Point( 20, 10 );

    Point actual = AddElementDialogUtil.computeShellSize( displayBounds, oldSize );

    assertThat( actual ).isEqualTo( new Point( 20, 10 ) );
  }

  @Test
  public void computeShellLocation() {
    Rectangle displayBounds = new Rectangle( 0, 0, 400, 200 );
    Point shellSize = new Point( 200, 10 );

    Point actual = AddElementDialogUtil.computeShellLocation( displayBounds, shellSize );

    assertThat( actual ).isEqualTo( new Point( 100, 95 ) );
  }

  @Test
  public void asyncExec() {
    displayHelper.ensureDisplay();
    Runnable runnable = mock( Runnable.class );

    AddElementDialogUtil.asyncExec( runnable );

    verify( runnable, never() ).run();
    flushPendingEvents();
    verify( runnable ).run();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void triggerCallback() {
    Consumer consumer = mock( Consumer.class );
    Map<String, Object> additionInfo = new HashMap<>();
    AddElementDialog dialog = stubDialog( additionInfo );

    AddElementDialogUtil.triggerCallback( consumer, dialog, OK );

    verify( consumer ).accept( additionInfo );
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void triggerCallbackOnCancel() {
    Consumer consumer = mock( Consumer.class );
    AddElementDialog dialog = stubDialog( new HashMap<>() );

    AddElementDialogUtil.triggerCallback( consumer, dialog, CANCEL );

    verify( consumer, never() ).accept( any() );
  }

  @Test
  public void isValidElementPartKey() {
    assertThat( AddElementDialogUtil.isValidElementPartKey( ADDITION_INFO_KEY ) ).isTrue();
    assertThat( AddElementDialogUtil.isValidElementPartKey( ADDITION_INFO_VALUE ) ).isTrue();
    assertThat( AddElementDialogUtil.isValidElementPartKey( "invalid" ) ).isFalse();
  }

  private static AddElementDialog stubDialog( Map<String, Object> additionInfo ) {
    AddElementDialog result = mock( AddElementDialog.class );
    when( result.getAdditionInfo() ).thenReturn( additionInfo );
    return result;
  }
}