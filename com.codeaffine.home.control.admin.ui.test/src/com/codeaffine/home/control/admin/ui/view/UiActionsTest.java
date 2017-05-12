package com.codeaffine.home.control.admin.ui.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.api.Page;

public class UiActionsTest {

  private static final String LABEL = "Label";

  @Test
  public void activatePage() {
    AdminUiView view = mock( AdminUiView.class );
    BrowserNavigation navigation = mock( BrowserNavigation.class );
    Page page = stubContribution( LABEL );

    UiActions.activatePage( page, navigation, view );

    InOrder order = inOrder( view, page, navigation );
    order.verify( view ).showPage( page );
    order.verify( page ).setFocus();
    order.verify( navigation ).pushState( LABEL.toLowerCase(), LABEL );
  }

  @Test
  public void getFragmentId() {
    Page page = stubContribution( LABEL );

    String actual = UiActions.getFragmentId( page );

    assertThat( actual ).isEqualTo( LABEL.toLowerCase() );
  }

  private static Page stubContribution( String id ) {
    Page result = mock( Page.class );
    when( result.getLabel() ).thenReturn( id );
    return result;
  }
}
