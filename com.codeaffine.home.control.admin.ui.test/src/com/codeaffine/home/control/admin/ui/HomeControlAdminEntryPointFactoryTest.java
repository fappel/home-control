package com.codeaffine.home.control.admin.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.junit.Test;

import com.codeaffine.home.control.admin.HomeControlAdminService;

public class HomeControlAdminEntryPointFactoryTest {

  @Test
  public void create() {
    HomeControlAdminService adminService = mock( HomeControlAdminService.class );
    HomeControlAdminEntryPointFactory factory = new HomeControlAdminEntryPointFactory( adminService );

    EntryPoint actual = factory.create();

    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdminServiceArgument() {
    new HomeControlAdminEntryPointFactory( null );
  }
}
