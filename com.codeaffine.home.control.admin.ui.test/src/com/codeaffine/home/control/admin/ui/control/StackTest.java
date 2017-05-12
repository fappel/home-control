package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.test.util.ShellHelper.createDemoShell;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.swt.SWT.NONE;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class StackTest {

  private static final Object ELEMENT_ID_1 = new Object();
  private static final Object ELEMENT_ID_2 = new Object();

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private Stack stack;
  private Shell parent;

  @Before
  public void setUp() {
    parent = createDemoShell( displayHelper );
    stack = new Stack( parent );
  }

  @Test
  public void newElement() {
    AtomicReference<Composite> elementParentCaptor = new AtomicReference<>();
    AtomicReference<Label> elementCaptor = new AtomicReference<>();

    stack.newElement( ELEMENT_ID_1, elementParent -> {
      elementCaptor.set( new Label( elementParent, NONE ) );
      elementParentCaptor.set( elementParent );
    } );

    assertThat( elementCaptor.get() ).isNotNull();
    assertThat( elementParentCaptor.get() ).isNotEqualTo( stack.getControl() );
    assertThat( elementParentCaptor.get().getLayout() ).isInstanceOf( FillLayout.class );
  }

  @Test
  public void show() {
    AtomicReference<Label> elementCaptor1 = new AtomicReference<>();
    stack.newElement( ELEMENT_ID_1, elementParent -> elementCaptor1.set( new Label( elementParent, NONE ) ) );
    AtomicReference<Text> elementCaptor2 = new AtomicReference<>();
    stack.newElement( ELEMENT_ID_2, elementParent -> elementCaptor2.set( new Text( elementParent, NONE ) ) );
    parent.open();

    stack.show( ELEMENT_ID_1 );

    assertThat( elementCaptor1.get().isVisible() ).isTrue();
    assertThat( elementCaptor1.get().getBounds() ).isEqualTo( ( ( Composite )stack.getControl() ).getClientArea() );
    assertThat( elementCaptor2.get().isVisible() ).isFalse();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsParentArgument() {
    new Stack( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newElementWithNullAsElementIdArgument() {
    stack.newElement( null, elementParent -> {} );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newElementWithNullAsElementFactoryArgument() {
    stack.newElement( ELEMENT_ID_1, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void showWithNullAsElementIdArgument() {
    stack.show( null );
  }

  @Test
  public void newElementIfElementWithIdAlreadyExists() {
    stack.newElement( ELEMENT_ID_1, elementParent -> {} );

    Throwable actual = thrownBy( () -> stack.newElement( ELEMENT_ID_1, elementParent -> {} ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void showWithElementIdThatDoesNotExist() {
    stack.show( ELEMENT_ID_1 );
  }
}