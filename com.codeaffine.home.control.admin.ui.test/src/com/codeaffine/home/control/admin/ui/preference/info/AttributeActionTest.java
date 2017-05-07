package com.codeaffine.home.control.admin.ui.preference.info;

import static com.codeaffine.home.control.admin.ui.preference.info.Messages.ERROR_WRONG_PRESENTATION_TYPE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class AttributeActionTest {

  private MyActionPresentation presentation;
  private AttributeAction attributeAction;
  private Runnable action;

  interface MyActionPresentation extends AttributeActionPresentation {}

  @Before
  public void setUp() {
    presentation = mock( MyActionPresentation.class );
    action = mock( Runnable.class );
    attributeAction = new AttributeAction( action, presentation );
  }

  @Test
  public void run() {
    attributeAction.run();

    verify( action ).run();
  }

  @Test
  public void getPresentationType() {
    MyActionPresentation actual = attributeAction.getPresentation( MyActionPresentation.class );

    assertThat( actual ).isSameAs( presentation );
  }

  @Test
  public void getPresentationTypeWithWrongPresentationType() {
    Class<? extends AttributeActionPresentation> wrongPresentation = new AttributeActionPresentation() {}.getClass();

    Throwable actual = thrownBy( () -> attributeAction.getPresentation( wrongPresentation ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_WRONG_PRESENTATION_TYPE,
                           presentation.getClass().getName(),
                           wrongPresentation.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActionArgument() {
    new AttributeAction( null, presentation );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPresentationArgument() {
    new AttributeAction( action, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getPresentationWithNullAsPresentationTypeArgument() {
    attributeAction.getPresentation( null );
  }
}