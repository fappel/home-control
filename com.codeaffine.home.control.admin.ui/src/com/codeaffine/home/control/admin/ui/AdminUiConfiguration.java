package com.codeaffine.home.control.admin.ui;

import static com.eclipsesource.tabris.TabrisClientInstaller.install;
import static org.eclipse.rap.rwt.RWT.DEFAULT_THEME_ID;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

import com.codeaffine.home.control.admin.ui.api.PageFactory;
import com.codeaffine.home.control.admin.ui.model.PageFactoryList;

public class AdminUiConfiguration implements ApplicationConfiguration {

  private static final String STANDARD_THEME_EXTRENSION_FILE_LOCATION = "theme/default-theme-extension.css";

  private final PageFactoryList pageFactoryList;

  public AdminUiConfiguration() {
    pageFactoryList = new PageFactoryList();
  }

  @Override
  public void configure( Application application ) {
    install( application );
    application.addStyleSheet( DEFAULT_THEME_ID, STANDARD_THEME_EXTRENSION_FILE_LOCATION );
    application.addEntryPoint( "/admin", () -> new AdminUiEntryPoint( pageFactoryList ), null );
  }

  public void addPageFactory( PageFactory pageFactory ) {
    pageFactoryList.addPageFactory( pageFactory );
  }

  public void removePageFactory( PageFactory pageFactory ) {
    pageFactoryList.removePageFactory( pageFactory );
  }
}