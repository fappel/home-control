package com.codeaffine.home.control.admin;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class HomeControlAdminServiceTest {

  private HomeControlAdminService service;

  @Before
  public void setUp() {
    service = new HomeControlAdminService();
  }

  @Test
  public void getNameIfNotInitialized() {
    Throwable actual = thrownBy( () -> service.getName() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( Messages.ERROR_NOT_INITIALIZED );
  }
}
