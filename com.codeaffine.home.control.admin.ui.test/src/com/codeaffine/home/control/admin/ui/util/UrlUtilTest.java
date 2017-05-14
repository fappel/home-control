package com.codeaffine.home.control.admin.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.rap.rwt.testfixture.internal.TestRequest.*;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings("restriction")
public class UrlUtilTest {

  @Rule
  public final TestContext testContext = new TestContext();

  @Test
  public void getServletUrl() {
    UrlUtil urlUtil = new UrlUtil();

    String actual = urlUtil.getServletUrl();

    assertThat( actual ).isEqualTo( expectedServletUrl() );
  }

  private static String expectedServletUrl() {
    return new TestRequest().getScheme()
         + "://"
         + DEFAULT_SERVER_NAME
         + ":"
         + PORT
         + DEFAULT_CONTEX_PATH
         + DEFAULT_SERVLET_PATH;
  }
}