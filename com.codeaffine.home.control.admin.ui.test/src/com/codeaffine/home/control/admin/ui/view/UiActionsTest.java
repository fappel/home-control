package com.codeaffine.home.control.admin.ui.view;

import static org.mockito.Mockito.*;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.api.Page;

public class UiActionsTest {

  private static final String ID = "id";

  @Test
  public void activatePage() {
    String id = ID;
    AdminUiView view = mock( AdminUiView.class );
    BrowserNavigation navigation = mock( BrowserNavigation.class );
    Page page = stubContribution( id );

    UiActions.activatePage( page, navigation, view );

    InOrder order = inOrder( view, page, navigation );
    order.verify( view ).showPage( ID );
    order.verify( page ).setFocus();
    order.verify( navigation ).pushState( ID, ID );
  }

  private static Page stubContribution( String id ) {
    Page result = mock( Page.class );
    when( result.getId() ).thenReturn( id );
    return result;
  }
}
