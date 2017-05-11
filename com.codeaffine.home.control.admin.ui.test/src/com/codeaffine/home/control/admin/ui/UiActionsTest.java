package com.codeaffine.home.control.admin.ui;

import static org.mockito.Mockito.*;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.junit.Test;
import org.mockito.InOrder;

public class UiActionsTest {

  private static final String ID = "id";

  @Test
  public void activatePage() {
    String id = ID;
    AdminUiView view = mock( AdminUiView.class );
    BrowserNavigation navigation = mock( BrowserNavigation.class );
    PageContribution contribution = stubContribution( id );

    UiActions.activatePage( contribution, navigation, view );

    InOrder order = inOrder( view, contribution, navigation );
    order.verify( view ).showPage( ID );
    order.verify( contribution ).setFocus();
    order.verify( navigation ).pushState( ID, ID );
  }

  private static PageContribution stubContribution( String id ) {
    PageContribution result = mock( PageContribution.class );
    when( result.getId() ).thenReturn( id );
    return result;
  }
}
