package com.codeaffine.home.control.admin.ui;

import org.junit.Test;

public class HomeControlAdminEntryPointFactoryTest {

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdminServiceArgument() {
    new HomeControlAdminEntryPointFactory( null );
  }
}
