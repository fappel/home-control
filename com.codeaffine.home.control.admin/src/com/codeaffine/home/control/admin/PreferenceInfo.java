package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.util.reflection.ReflectionUtil.invoke;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import com.codeaffine.home.control.ComponentAccessService;

public class PreferenceInfo {

  private final ComponentAccessService service;
  private final Object instance;
  private final Class<?> type;
  private final BeanInfo info;

  PreferenceInfo( Class<?> type, Object instance, ComponentAccessService service ) {
    verifyNotNull( instance, "instance" );
    verifyNotNull( service, "service" );
    verifyNotNull( type, "type" );

    this.type = type;
    this.instance = instance;
    this.service = service;
    this.info = getBeanInfo( type );
  }

  public String getName() {
    return type.getName();
  }

  public List<PreferenceAttributeDescriptor> getAttributeDescriptors() {
    return asList( info.getPropertyDescriptors() )
      .stream()
      .map( descriptor -> new PreferenceAttributeDescriptor( descriptor ) )
      .collect( toList() );
  }

  public PreferenceAttributeDescriptor getAttributeDescriptor( String attributeName ) {
    verifyNotNull( attributeName, "attributeName" );

    return new PreferenceAttributeDescriptor( getPropertyDescriptor( attributeName ) );
  }

  public void setAttributeValue( String attributeName, Object value ) {
    verifyNotNull( attributeName, "attributeName" );

    PropertyDescriptor propertyDescriptor = getPropertyDescriptor( attributeName );
    Method writeMethod = propertyDescriptor.getWriteMethod();
    service.execute( supplier -> invoke( writeMethod, instance, value ) );
  }

  public Object getAttributeValue( String attributeName ) {
    verifyNotNull( attributeName, "attributeName" );

    PropertyDescriptor propertyDescriptor = getPropertyDescriptor( attributeName );
    Method readMethod = propertyDescriptor.getReadMethod();
    return service.submit( supplier -> invoke( readMethod, instance ) );
  }

  private PropertyDescriptor getPropertyDescriptor( String attributeName ) {
    return Stream.of( info.getPropertyDescriptors() )
      .filter( descriptor -> descriptor.getName().equals( attributeName ) )
      .findFirst()
      .get();
  }

  private static BeanInfo getBeanInfo( Class<?> type ) {
    try {
      return Introspector.getBeanInfo( type );
    } catch( IntrospectionException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}