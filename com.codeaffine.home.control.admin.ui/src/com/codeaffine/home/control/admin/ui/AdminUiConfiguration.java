package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static org.eclipse.rap.rwt.RWT.DEFAULT_THEME_ID;
import static org.eclipse.rap.rwt.application.ApplicationRunner.RESOURCES;
import static org.eclipse.rap.rwt.client.WebClient.HEAD_HTML;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.ui.api.PageFactory;
import com.codeaffine.home.control.admin.ui.model.PageFactoryList;

public class AdminUiConfiguration implements ApplicationConfiguration {

  private static final String STYLE_SHEET_LINK_PATTERN ="<link rel=\"stylesheet\" href=\"%s/%s\">";
  private static final String STANDARD_THEME_EXTENSION = "theme/default-theme-extension.css";
  private static final String STANDARD_THEME_NATIVE = "theme/default-theme-native.css";
  private static final String SERVLET_PATH = "/admin";

  private final PageFactoryList pageFactoryList;
  private HomeControlAdminService adminService;

  public AdminUiConfiguration() {
    pageFactoryList = new PageFactoryList();
  }

  @Override
  public void configure( Application application ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( HEAD_HTML, format( STYLE_SHEET_LINK_PATTERN, RESOURCES, STANDARD_THEME_NATIVE ) );
    application.addResource( STANDARD_THEME_NATIVE, location -> getResourceAsStream( location ) );
    application.addStyleSheet( DEFAULT_THEME_ID, STANDARD_THEME_EXTENSION );
    application.addEntryPoint( SERVLET_PATH, () -> new AdminUiEntryPoint( pageFactoryList, adminService ), properties );
  }

  public void bindHomeControlAdminService( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  public void unbindHomeControlAdminService( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = null;
  }

  public void addPageFactory( PageFactory pageFactory ) {
    pageFactoryList.addPageFactory( pageFactory );
  }

  public void removePageFactory( PageFactory pageFactory ) {
    pageFactoryList.removePageFactory( pageFactory );
  }

  private InputStream getResourceAsStream( String location ) {
    return getClass().getClassLoader().getResourceAsStream( location );
  }
}