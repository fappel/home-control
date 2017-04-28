package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.Messages.ERROR_NOT_INITIALIZED;
import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.stubInThreadExecutor;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.activation.ComponentAccessServiceImpl;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class HomeControlAdminServiceTest {

  private HomeControlAdminService service;

  @Before
  public void setUp() {
    service = new HomeControlAdminService();
  }

  @Test
  public void getPreferenceIntrospection() {
    service.bind( new ComponentAccessServiceImpl( newContext() ) );

    PreferenceIntrospection actual = service.getPreferenceIntrospection();

    assertThat( actual ).isNotNull();
  }

  @Test
  public void getPreferenceIntrospectionIfComponentAccessServiceWasNotBound() {
    Throwable actual = thrownBy( () -> service.getPreferenceIntrospection() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_NOT_INITIALIZED );
  }

  @Test
  public void getPreferenceIntrospectionIfComponentAccessServiceHasBeenUnbound() {
    ComponentAccessServiceImpl componentAccessService = new ComponentAccessServiceImpl( newContext() );

    service.bind( componentAccessService );
    service.unbind( componentAccessService );
    Throwable actual = thrownBy( () -> service.getPreferenceIntrospection() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_NOT_INITIALIZED );
  }

  @Test( expected = IllegalArgumentException.class )
  public void bindWithNullAsComponentAccessServiceArgument() {
    service.bind( null );
  }

  private static TestContext newContext() {
    TestContext result = new TestContext();
    result.set( SystemExecutor.class, stubInThreadExecutor() );
    result.set( PreferenceModel.class, mock( PreferenceModel.class ) );
    return result;
  }
}