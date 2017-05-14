package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.test.util.ShellHelper.createDemoShell;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class AnchorLayoutTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private AnchorLayout layout;
  private Shell control;
  private Label label;

  @Before
  public void setUp() {
    control = createDemoShell( displayHelper );
    label = new Label( control, SWT.NONE );
    layout = new AnchorLayout( control, label );
  }

  @Test
  public void layout() {
    layout.layout( control, false );

    assertThat( label.getLocation() ).isEqualTo( expectedLabelLocation() );
    assertThat( label.getSize() ).isEqualTo( label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void computeSize() {
    int wHint = SWT.DEFAULT;
    int hHint = SWT.DEFAULT;
    boolean flushCache = true;

    Point actual = layout.computeSize( control, wHint, hHint, flushCache );

    assertThat( actual ).isEqualTo( label.computeSize( wHint, wHint, flushCache ) );
  }

  private Point expectedLabelLocation() {
    Rectangle clientArea = control.getClientArea();
    Point size = label.getSize();
    return AnchorLayout.computeLabelLocation( clientArea, size );
  }
}
