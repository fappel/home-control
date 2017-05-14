package com.codeaffine.home.control.admin.ui.view;

import com.codeaffine.home.control.admin.ui.api.Page;

class UiActions {

  static void activatePage( Page page, AdminUiView view ) {
    view.showPage( page );
    page.setFocus();
  }
}