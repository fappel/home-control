package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.PreferenceHelper.*;
import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.stubInThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.engine.activation.ComponentAccessServiceImpl;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class PreferenceProxyFactoryTest {

  private ComponentAccessService componentAccessService;
  private PreferenceProxyFactory factory;
  private TestPreference delegate;

  @Before
  public void setUp() {
    delegate = TestPreference.newInstance();
    PreferenceModel model = stubPreferenceModel( delegate );
    TestContext context = createContext( model, stubInThreadExecutor() );
    componentAccessService = new ComponentAccessServiceImpl( context );
    factory = new PreferenceProxyFactory();
  }

  @Test
  public void create() {
    TestPreference preference = factory.create( delegate, TestPreference.class, componentAccessService );
    int expected = 234;

    int defaultValue = preference.getIntValue();
    preference.setIntValue( expected );
    int actual = preference.getIntValue();

    assertThat( defaultValue ).isEqualTo( Integer.parseInt( TestPreference.INT_DEFAULT_VALUE ) );
    assertThat( actual ).isEqualTo( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDelegateArgument() {
    factory.create( null, TestPreference.class, componentAccessService );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsTypeArgument() {
    factory.create( delegate, null, componentAccessService );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsComponentAccessServiceArgument() {
    factory.create( delegate, TestPreference.class, null );
  }
}