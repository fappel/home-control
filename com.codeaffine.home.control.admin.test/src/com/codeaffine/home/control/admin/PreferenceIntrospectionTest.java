package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.stubInThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.activation.ComponentAccessServiceImpl;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class PreferenceIntrospectionTest {

  private PreferenceIntrospection introspection;
  private TestPreference preference;

  @Before
  public void setUp() {
    preference = TestPreference.newInstance();
    PreferenceModel model = stubPreferenceModel( preference );
    TestContext context = createContext( model, stubInThreadExecutor() );
    ComponentAccessService componentAccessService = new ComponentAccessServiceImpl( context );
    introspection = new PreferenceIntrospection( model, componentAccessService );
  }

  @Test
  public void getPreferenceInfos() {
    Integer expected = Integer.valueOf( TestPreference.INT_DEFAULT_VALUE );

    Set<PreferenceInfo> actual = introspection.getPreferenceInfos();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( info -> info.getName().equals( TestPreference.class.getName() ) )
      .allMatch( info -> info.getAttributeValue( TestPreference.INT_ATTRIBUTE_NAME ).equals( expected ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModelArgument() {
    new PreferenceIntrospection( null, mock( ComponentAccessService.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsComponentAccessServiceArgument() {
    new PreferenceIntrospection( mock( PreferenceModel.class ), null );
  }

  private static PreferenceModel stubPreferenceModel( TestPreference preference ) {
    PreferenceModel result = mock( PreferenceModel.class );
    when( result.getAllPreferenceTypes() ).thenReturn( asSet( TestPreference.class ) );
    when( result.get( TestPreference.class ) ).thenReturn( preference );
    return result;
  }

  private static TestContext createContext( PreferenceModel model, SystemExecutor executor ) {
    TestContext result = new TestContext();
    result.set( PreferenceModel.class, model );
    result.set( SystemExecutor.class, executor );
    return result;
  }
}