package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.stubInThreadExecutor;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.activation.ComponentAccessServiceImpl;
import com.codeaffine.home.control.test.util.context.TestContext;

public class PreferenceInfoTest {

  private TestPreference preference;
  private SystemExecutor executor;
  private PreferenceInfo info;

  @Before
  public void setUp() {
    preference = TestPreference.newInstance();
    executor = stubInThreadExecutor();
    TestContext context = createContext( executor );
    ComponentAccessServiceImpl service = new ComponentAccessServiceImpl( context );
    info = new PreferenceInfo( TestPreference.class, preference, service );
  }

  @Test
  public void getName() {
    String actual = info.getName();

    assertThat( actual ).isEqualTo( TestPreference.class.getName() );
  }

  @Test
  public void getAttributeDescriptors() {
    List<PreferenceAttributeDescriptor> actual = info.getAttributeDescriptors();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( descriptor -> descriptor.getName().equals( TestPreference.ATTRIBUTE_NAME ) );
  }

  @Test
  public void getAttributeDescriptor() {
    PreferenceAttributeDescriptor actual = info.getAttributeDescriptor( TestPreference.ATTRIBUTE_NAME );

    assertThat( actual )
      .matches( descriptor -> descriptor.getAttributeType() == TestPreference.ATTRIBUTE_TYPE )
      .matches( descriptor -> descriptor.getName().equals( TestPreference.ATTRIBUTE_NAME ) );
  }

  @Test
  public void getAttributeValue() {
    Object actual = info.getAttributeValue( TestPreference.ATTRIBUTE_NAME );

    assertThat( actual ).isEqualTo( Integer.valueOf( TestPreference.DEFAULT_VALUE ) );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getAttributeValueWithProblemOnExecutorDelegation() {
    RuntimeException expected = new RuntimeException();
    doThrow( expected ).when( executor ).submit( any( Callable.class ) );

    Throwable actual = thrownBy( () -> info.getAttributeValue( TestPreference.ATTRIBUTE_NAME ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void setAttributeValue() {
    int expected = 23;

    info.setAttributeValue( TestPreference.ATTRIBUTE_NAME, expected );
    int actual = preference.getValue();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void setAttributeValueWithProblemOnExecutorDelegation() {
    RuntimeException expected = new RuntimeException();
    doThrow( expected ).when( executor ).execute( any( Runnable.class ) );

    Throwable actual = thrownBy( () -> info.setAttributeValue( TestPreference.ATTRIBUTE_NAME, 25 ) );

    assertThat( actual ).isEqualTo( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTypeArgument() {
    new PreferenceInfo( null, preference, mock( ComponentAccessService.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInstanceArgument() {
    new PreferenceInfo( TestPreference.class, null, mock( ComponentAccessService.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsServiceArgument() {
    new PreferenceInfo( TestPreference.class, preference, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeDescriptorWithNullAsAttributeNameArgument() {
    info.getAttributeDescriptor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeValueWithNullAsAttributeNameArgument() {
    info.getAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setAttributeValueWithNullAsAttributeNameArgument() {
    info.setAttributeValue( null, Integer.valueOf( 32 ) );
  }

  private static TestContext createContext( SystemExecutor systemExecutor ) {
    TestContext result = new TestContext();
    result.set( SystemExecutor.class, systemExecutor );
    return result;
  }
}