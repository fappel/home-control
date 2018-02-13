package com.codeaffine.home.control.util.reflection;

import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtilTest.TestEnum.ONE;
import static com.codeaffine.home.control.util.reflection.Messages.ERROR_LOADING_GENERIC_PARAMETER;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.Test;

import com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.NoSuchMethodRuntimeException;

public class AttributeReflectionUtilTest {

  private static final String UNKNOWN_ENUM_CONSTANT = "unknown";

  interface TestBeanType {
    List<String> getStringList() throws Exception;
    void setStringList( List<String> value ) throws Exception;
  }

  enum TestEnum {
    ONE, TWO;

    @Override
    public String toString() {
      return name() + "-valueOfPrevention-";
    }
  }

  @Test
  public void hasValueOfFactoryMethodMethod() {
    boolean actual = AttributeReflectionUtil.hasValueOfFactoryMethod( Integer.class );

    assertThat( actual ).isTrue();
  }

  @Test
  public void hasValueOfFactoryMethodForSupportedPrimitives() {
    Class<?> primitive = int[].class.getComponentType();

    boolean actual = AttributeReflectionUtil.hasValueOfFactoryMethod( primitive );

    assertThat( actual ).isTrue();
  }

  @Test
  public void hasValueOfFactoryMethodOfTypeWithoutValueOfSupport() {
    Class<?> someType = Runnable.class;

    boolean actual = AttributeReflectionUtil.hasValueOfFactoryMethod( someType );

    assertThat( actual ).isFalse();
  }

  @Test
  public void getValueOfFactoryMethod() {
    Method actual = AttributeReflectionUtil.getValueOfFactoryMethod( Integer.class );

    assertThat( actual ).isNotNull();
  }

  @Test
  public void getValueOfFactoryMethodForSupportedPrimitives() {
    Class<?> primitive = int[].class.getComponentType();

    Method actual = AttributeReflectionUtil.getValueOfFactoryMethod( primitive );

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
  public void invokeArgumentFactoryMethodForEnumWithParticularToStringImplementation() {
    Object actual = AttributeReflectionUtil.invokeArgumentFactoryMethod( TestEnum.class, () -> ONE.toString() );

    assertThat( actual ).isSameAs( ONE );
  }

  @Test
  public void invokeArgumentFactoryMethodForEnumWithParticularToStringImplementationWithUpperCaseEnumConstant() {
    Object actual
      = AttributeReflectionUtil.invokeArgumentFactoryMethod( ChronoUnit.class, () -> DAYS.toString().toUpperCase() );

    assertThat( actual ).isSameAs( DAYS );
  }

  @Test
  public void invokeArgumentFactoryMethodForEnumWithParticularToStringImplementationOfUnknownConstantValue() {
    Throwable actual = thrownBy(
      () -> AttributeReflectionUtil.invokeArgumentFactoryMethod( TestEnum.class, () -> UNKNOWN_ENUM_CONSTANT ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessageContaining( UNKNOWN_ENUM_CONSTANT );
  }

  @Test
  public void initializeAttribute() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    List<String> expected = asList( "value1" );
    TestBeanType bean = stubPreferenceWithStringList( expected );

    AttributeReflectionUtil.initializeAttribute( bean, descriptor );

    verify( bean ).setStringList( expected );
  }

  @Test
  public void initializeAttributeWithRuntimeProblemOnAccessor() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    RuntimeException expected = new RuntimeException();
    TestBeanType bean = stubPreferenceGetterWithProblem( expected );

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.initializeAttribute( bean, descriptor ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void initializeAttributeWithCheckedProblemOnAccessor() throws Exception {
    PropertyDescriptor descriptor = retrievePreferenceAttributeDescriptor();
    Exception cause = new Exception();
    TestBeanType bean = stubPreferenceGetterWithProblem( cause );

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

    Class<?> actual = AttributeReflectionUtil.loadTypeArgument( TestBeanType.class, actualTypeArguments.get( 0 ) );

    assertThat( actual ).isSameAs( String.class );
  }

  @Test
  public void loadTypeArgumentOfUnavailableType() {
    Type unknownType = stubUnknownType();

    Throwable actual = thrownBy( () -> AttributeReflectionUtil.loadTypeArgument( TestBeanType.class, unknownType ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_LOADING_GENERIC_PARAMETER, unknownType.getTypeName() ) );
  }

  private static TestBeanType stubPreferenceGetterWithProblem( Exception problem ) throws Exception {
    TestBeanType result = mock( TestBeanType.class );
    when( result.getStringList() ).thenThrow( problem );
    return result;
  }

  private static TestBeanType stubPreferenceWithStringList( List<String> expected ) throws Exception {
    TestBeanType result = mock( TestBeanType.class );
    when( result.getStringList() ).thenReturn( expected );
    return result;
  }

  private static Type stubUnknownType() {
    Type result = mock( Type.class );
    when( result.getTypeName() ).thenReturn( "UnknownType" );
    return result;
  }

  private static PropertyDescriptor retrievePreferenceAttributeDescriptor() throws IntrospectionException {
    BeanInfo beanInfo = Introspector.getBeanInfo( TestBeanType.class );
    return beanInfo.getPropertyDescriptors()[ 0 ];
  }
}