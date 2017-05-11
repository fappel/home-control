package com.codeaffine.home.control.admin.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.junit.Test;

import com.codeaffine.home.control.admin.HomeControlAdminService;

public class AdminUiEntryPointFactoryTest {

  @Test
  public void create() {
    HomeControlAdminService adminService = mock( HomeControlAdminService.class );
    AdminUiEntryPointFactory factory = new AdminUiEntryPointFactory( adminService );

    EntryPoint actual = factory.create();

    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdminServiceArgument() {
    new AdminUiEntryPointFactory( null );
  }
}
