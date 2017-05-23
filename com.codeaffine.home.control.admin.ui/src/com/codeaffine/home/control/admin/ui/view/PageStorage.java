package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptyList;
import static org.eclipse.rap.rwt.RWT.getUISession;

import java.util.ArrayList;
import java.util.List;

import com.codeaffine.home.control.admin.ui.api.Page;

class PageStorage {

  static final String KEY = PageStorage.class.getName();

  @SuppressWarnings("unchecked")
  List<Page> getPages() {
    if( getUISession().getAttribute( KEY ) == null ) {
      return emptyList();
    }
    return new ArrayList<>( ( List<Page> )getUISession().getAttribute( KEY ) );
  }

  void register( List<Page> pages ) {
    verifyNotNull( pages, "pages" );

    getUISession().setAttribute( KEY, new ArrayList<>( pages ) );
  }

  void clear() {
    register( emptyList() );
  }
}