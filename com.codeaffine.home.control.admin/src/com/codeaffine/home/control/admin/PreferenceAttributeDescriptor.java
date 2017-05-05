package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;

public class PreferenceAttributeDescriptor {

  private final PropertyDescriptor descriptor;
  private final BeanInfo info;

  PreferenceAttributeDescriptor( BeanInfo info, PropertyDescriptor descriptor ) {
    verifyNotNull( descriptor, "descriptor" );
    verifyNotNull( info, "info" );

    this.descriptor = descriptor;
    this.info = info;
  }

  public Class<?> getAttributeType() {
    return descriptor.getPropertyType();
  }

  public String getName() {
    return descriptor.getName();
  }

  public String getDisplayName() {
    return descriptor.getDisplayName();
  }

  public List<Class<?>> getGenericTypeParametersOfAttributeType() {
    try {
      return getGenericTypeParametersOfAttributeType( info.getBeanDescriptor().getBeanClass() );
    } catch( ClassCastException cce ) {
      return emptyList();
    }
  }

  private List<Class<?>> getGenericTypeParametersOfAttributeType( Class<?> beanType ) {
    return getActualTypeArgumentsOfGenericAttributeType( descriptor )
      .stream()
      .map( typeArgument -> loadTypeArgument( beanType, typeArgument ) )
      .collect( toList() );
  }
}
