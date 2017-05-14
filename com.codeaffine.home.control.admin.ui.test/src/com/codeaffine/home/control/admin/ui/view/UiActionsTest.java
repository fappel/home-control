package com.codeaffine.home.control.admin.ui.view;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.api.Page;

public class UiActionsTest {

  private static final String LABEL = "Label";

  @Test
  public void activatePage() {
    AdminUiView view = mock( AdminUiView.class );
    Page page = stubContribution( LABEL );

    UiActions.activatePage( page, view );

    InOrder order = inOrder( view, page );
    order.verify( view ).showPage( page );
    order.verify( page ).setFocus();
  }

  private static Page stubContribution( String id ) {
    Page result = mock( Page.class );
    when( result.getLabel() ).thenReturn( id );
    return result;
  }
}
