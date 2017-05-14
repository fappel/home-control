package com.codeaffine.home.control.admin.ui.util;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.RWT;

public class UrlUtil {

  public String getServletUrl() {
    HttpServletRequest request = RWT.getRequest();
    return request.getScheme()
         + "://"
         + request.getServerName()
         + ":"
         + request.getServerPort()
         + request.getContextPath()
         + request.getServletPath();
  }
}