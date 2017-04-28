package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.TestPreference.ATTRIBUTE_NAME;
import static java.beans.Introspector.getBeanInfo;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class PreferenceAttributeDescriptorTest {

  private PreferenceAttributeDescriptor descriptor;

  @Before
  public void setUp() throws IntrospectionException {
    descriptor = new PreferenceAttributeDescriptor( getPropertyDescriptor( TestPreference.class, ATTRIBUTE_NAME ) );
  }

  @Test
  public void getAttributeType() {
    Class<?> actual = descriptor.getAttributeType();

    assertThat( actual ).isSameAs( TestPreference.ATTRIBUTE_TYPE );
  }

  @Test
  public void getName() {
    String actual = descriptor.getName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test
  public void getDisplayName() {
    String actual = descriptor.getDisplayName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDescriptorArgument() {
    new PreferenceAttributeDescriptor( null );
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