package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.TestPreference.*;
import static java.beans.Introspector.getBeanInfo;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class PreferenceAttributeDescriptorTest {

  private PreferenceAttributeDescriptor intAttributeDescriptor;
  private PropertyDescriptor intPropertyDescriptor;
  private BeanInfo beanInfo;

  @Before
  public void setUp() throws IntrospectionException {
    beanInfo = getBeanInfo( TestPreference.class );
    intPropertyDescriptor = getPropertyDescriptor( TestPreference.class, INT_ATTRIBUTE_NAME );
    intAttributeDescriptor = new PreferenceAttributeDescriptor( beanInfo, intPropertyDescriptor );
  }

  @Test
  public void getAttributeType() {
    Class<?> actual = intAttributeDescriptor.getAttributeType();

    assertThat( actual ).isSameAs( TestPreference.INT_ATTRIBUTE_TYPE );
  }

  @Test
  public void getName() {
    String actual = intAttributeDescriptor.getName();

    assertThat( actual ).isEqualTo( INT_ATTRIBUTE_NAME );
  }

  @Test
  public void getDisplayName() {
    String actual = intAttributeDescriptor.getDisplayName();

    assertThat( actual ).isEqualTo( INT_ATTRIBUTE_NAME );
  }

  @Test
  public void getGenericTypeParametersOfAttributeTypeWithUnparameterizedAttributeType() {
    List<Class<?>> actual = intAttributeDescriptor.getGenericTypeParametersOfAttributeType();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getGenericTypeParametersOfAttributeType() throws IntrospectionException {
    PropertyDescriptor mapPropertyDescriptor = getPropertyDescriptor( TestPreference.class, MAP_ATTRIBUTE_NAME );
    PreferenceAttributeDescriptor descriptor = new PreferenceAttributeDescriptor( beanInfo, mapPropertyDescriptor );

    List<Class<?>> actual = descriptor.getGenericTypeParametersOfAttributeType();

    assertThat( actual ).isEqualTo( MAP_ATTRIBUTE_GENERIC_TYPE_PARAMETERS );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new PreferenceAttributeDescriptor( null, intPropertyDescriptor );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDescriptorArgument() {
    new PreferenceAttributeDescriptor( beanInfo, null );
  }

  private static PropertyDescriptor getPropertyDescriptor( Class<?> beanClass, String attributeName )
    throws IntrospectionException
  {
    return Stream.of( getBeanInfo( beanClass ).getPropertyDescriptors() )
      .filter( descriptor -> descriptor.getName().equals( attributeName ) )
      .findFirst()
      .get();
  }
}