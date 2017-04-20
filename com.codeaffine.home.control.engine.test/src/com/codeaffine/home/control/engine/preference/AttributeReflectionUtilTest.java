package com.codeaffine.home.control.engine.preference;

import static com.codeaffine.home.control.engine.preference.Messages.ERROR_LOADING_GENERIC_PARAMETER;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import com.codeaffine.home.control.engine.preference.AttributeReflectionUtil.NoSuchMethodRuntimeException;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

public class AttributeReflectionUtilTest {

  @Preference
  interface TestPreference {
    @DefaultValue( "{entry}" )
    List<String> getStringList() throws Exception;
    void setStringList( List<String> value ) throws Exception;
  }

  @Test
  public void getValueOfFactoryMethod() {
    Method actual = AttributeReflectionUtil.getValueOfFactoryMethod( Integer.class );

    assertThat( actual ).isNotNull();
  }

  @Test
  public void getValueOfFactoryMethodIfNotExistent() {
    Throwable actual = thrownBy( () -> AttributeReflectionUtil.getValueOfFactoryMethod( Object.class ) );

    assertThat( actual )
      .isInstanceOf( NoSuchMethodRuntimeException.class )
      .hasCauseInstanceOf( NoSuchMethodException.class );
  }

  @Test
  public void invokeArgumentFactoryMethod() {
    Object actual = AttributeReflectionUtil.invokeArgumentFactoryMethod( Integer.class, () -> "12" );

    assertThat( actual ).isEqualTo( Integer.valueOf( 12 ) );
  }

  @Test
  public void initializeAttribute() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    List<String> expected = asList( "value1" );
    TestPreference bean = stubPreferenceWithStringList( expected );

    AttributeReflectionUtil.initializeAttribute( bean, descriptor );

    verify( bean ).setStringList( expected );
  }

  @Test
  public void initializeAttributeWithRuntimeProblemOnAccessor() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    RuntimeException expected = new RuntimeException();
    TestPreference bean = stubPreferenceGetterWithProblem( expected );

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.initializeAttribute( bean, descriptor ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void initializeAttributeWithCheckedProblemOnAccessor() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    Exception cause = new Exception();
    TestPreference bean = stubPreferenceGetterWithProblem( cause );

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.initializeAttribute( bean, descriptor ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCause( cause );
  }

  @Test
  public void initializeAttributeWithNullAsBeanArgument() {
    PropertyDescriptor descriptor = mock( PropertyDescriptor.class );

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.initializeAttribute( null, descriptor ) );

    assertThat( actual ).isInstanceOf( NullPointerException.class );
  }

  @Test
  public void initializeAttributeWithNullAsDescriptorArgument() {
    Throwable actual = thrownBy( () -> AttributeReflectionUtil.initializeAttribute( new Object(), null ) );

    assertThat( actual ).isInstanceOf( NullPointerException.class );
  }

  @Test
  public void getActualTypeArgumentsOfGenericAttributeType() throws IntrospectionException {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();

    List<Type> actual = AttributeReflectionUtil.getActualTypeArgumentsOfGenericAttributeType( descriptor );

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( type -> type.getTypeName().equals( String.class.getName() ) );
  }

  @Test
  public void loadTypeArgument() throws IntrospectionException {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    List<Type> actualTypeArguments = AttributeReflectionUtil.getActualTypeArgumentsOfGenericAttributeType( descriptor );

    Class<?> actual = AttributeReflectionUtil.loadTypeArgument( TestPreference.class, actualTypeArguments.get( 0 ) );

    assertThat( actual ).isSameAs( String.class );
  }

  @Test
  public void loadTypeArgumentOfUnavailableType() {
    Type unknownType = stubUnknownType();

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.loadTypeArgument( TestPreference.class, unknownType ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_LOADING_GENERIC_PARAMETER, unknownType.getTypeName() ) );
  }

  private static TestPreference stubPreferenceGetterWithProblem( Exception problem ) throws Exception {
    TestPreference result = mock( TestPreference.class );
    when( result.getStringList() ).thenThrow( problem );
    return result;
  }

  private static TestPreference stubPreferenceWithStringList( List<String> expected ) throws Exception {
    TestPreference result = mock( TestPreference.class );
    when( result.getStringList() ).thenReturn( expected );
    return result;
  }

  private static Type stubUnknownType() {
    Type result = mock( Type.class );
    when( result.getTypeName() ).thenReturn( "UnknownType" );
    return result;
  }

  private static PropertyDescriptor retrievePreferenceAttributeDescriptor() throws IntrospectionException {
    BeanInfo beanInfo = Introspector.getBeanInfo( TestPreference.class );
    return beanInfo.getPropertyDescriptors()[ 0 ];
  }
}