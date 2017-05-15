package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.Messages.ERROR_NOT_INITIALIZED;
import static com.codeaffine.home.control.admin.PreferenceHelper.*;
import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.stubInThreadExecutor;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.activation.ComponentAccessServiceImpl;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class HomeControlAdminServiceTest {

  private ComponentAccessServiceImpl componentAccessService;
  private HomeControlAdminService service;
  private TestPreference preference;

  @Before
  public void setUp() {
    preference = TestPreference.newInstance();
    PreferenceModel model = stubPreferenceModel( preference );
    TestContext context = createContext( model, stubInThreadExecutor() );
    componentAccessService = new ComponentAccessServiceImpl( context );
    service = new HomeControlAdminService();
  }

  @Test
  public void getPreferenceIntrospection() {
    service.bind( componentAccessService );

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
    service.bind( componentAccessService );
    service.unbind( componentAccessService );

    Throwable actual = thrownBy( () -> service.getPreferenceIntrospection() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_NOT_INITIALIZED );
  }

  @Test
  public void getPreference() {
    service.bind( componentAccessService );

    TestPreference actual = service.getPreference( TestPreference.class );

    assertThat( actual )
      .isNotNull()
      .isNotSameAs( preference )
      .matches( proxy -> proxy.getIntValue() == preference.getIntValue() );
  }

  @Test
  public void getPreferenceIfComponentAccessServiceWasNotBound() {
    Throwable actual = thrownBy( () -> service.getPreference( TestPreference.class ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_NOT_INITIALIZED );
  }

  @Test
  public void getPreferenceIfComponentAccessServiceHasBeenUnbound() {
    service.bind( componentAccessService );
    service.unbind( componentAccessService );

    Throwable actual = thrownBy( () -> service.getPreference( TestPreference.class ) );

    assertThat( actual )
    .isInstanceOf( IllegalStateException.class )
    .hasMessage( ERROR_NOT_INITIALIZED );
  }

  @Test( expected = IllegalArgumentException.class )
  public void bindWithNullAsComponentAccessServiceArgument() {
    service.bind( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unbindWithNullAsComponentAccessServiceArgument() {
    service.unbind( null );
  }
}