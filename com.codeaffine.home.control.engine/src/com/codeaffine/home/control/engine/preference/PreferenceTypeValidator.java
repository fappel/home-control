package com.codeaffine.home.control.engine.preference;

import static com.codeaffine.home.control.engine.preference.Messages.*;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.*;
import static com.codeaffine.util.ArgumentVerification.*;
import static java.beans.Introspector.getBeanInfo;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

class PreferenceTypeValidator {

  protected BeanInfo validate( Class<?> preferenceType ) {
    verifyNotNull( preferenceType, "preferenceType" );
    verifyCondition( preferenceType.isInterface(), ERROR_NOT_A_INTERFACE, preferenceType.getName() );
    verifyCondition( hasPreferenceAnnotation( preferenceType ), ERROR_NOT_A_PREFERENCE, preferenceType.getName() );
    BeanInfo result = getBeanInfoFor( preferenceType );
    verifyWriteAccessorsExist( preferenceType, result );
    verifyReadAccessorsExist( preferenceType, result );
    verifyDefaultValuesAreDefined( preferenceType, result );
    verifyAttributeTypesAreSupported( preferenceType, result );
    verifyAllMethodsAreAttributeAccessors( preferenceType, result );
    return result;
  }

  private static <T> boolean hasPreferenceAnnotation( Class<T> preferenceType ) {
    return preferenceType.getAnnotation( Preference.class ) != null;
  }

  private static BeanInfo getBeanInfoFor( Class<?> preferenceType ) {
    try {
      return getBeanInfo( preferenceType );
    } catch( IntrospectionException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private static void verifyWriteAccessorsExist( Class<?> preferenceType, BeanInfo result ) {
    streamOfPropertyDescriptors( result )
      .forEach( descriptor -> verifyWriteAccessorExists( preferenceType, descriptor ) );
  }

  private static void verifyWriteAccessorExists( Class<?> preferenceType, PropertyDescriptor descriptor ) {
    String typeName = preferenceType.getName();
    String attributeName = descriptor.getName();
    verifyCondition( descriptor.getWriteMethod() != null, ERROR_MISSING_WRITE_ACCESSOR, typeName, attributeName );
  }

  private static void verifyReadAccessorsExist( Class<?> preferenceType, BeanInfo preferenceInfo ) {
    streamOfPropertyDescriptors( preferenceInfo )
      .forEach( descriptor -> verifyReadAccessorExists( preferenceType, descriptor ) );
  }

  private static void verifyReadAccessorExists( Class<?> preferenceType, PropertyDescriptor descriptor ) {
    String typeName = preferenceType.getName();
    String attributeName = descriptor.getName();
    verifyCondition( descriptor.getReadMethod() != null, ERROR_MISSING_READ_ACCESSOR, typeName, attributeName );
  }

  private static void verifyDefaultValuesAreDefined( Class<?> preferenceType, BeanInfo preferenceInfo ) {
    streamOfPropertyDescriptors( preferenceInfo )
      .forEach( descriptor -> verifyDefaultValueIsDefined( preferenceType, descriptor ) );
  }

  private static void verifyDefaultValueIsDefined( Class<?> preferenceType, PropertyDescriptor descriptor ) {
    String typeName = preferenceType.getName();
    String attributeName = descriptor.getName();
    boolean condition = hasDefaultValueAnnotation( descriptor.getReadMethod() );
    verifyCondition( condition, ERROR_MISSING_DEFAULT_VALUE_DEFINITION, typeName, attributeName );
  }

  private static boolean hasDefaultValueAnnotation( Method readMethod ) {
    return readMethod.getAnnotation( DefaultValue.class ) != null;
  }

  private static void verifyAttributeTypesAreSupported( Class<?> preferenceType, BeanInfo preferenceInfo ) {
    streamOfPropertyDescriptors( preferenceInfo )
      .forEach( descriptor -> verifyAttributeTypeIsSupported( preferenceType, descriptor ) );
  }

  private static void verifyAttributeTypeIsSupported( Class<?> preferenceType, PropertyDescriptor descriptor ) {
    String typeName = preferenceType.getName();
    String attributeName = descriptor.getName();
    String attributeTypeName = descriptor.getPropertyType().getName();
    boolean condition = isSupportedAttributeType( preferenceType, descriptor );
    verifyCondition( condition, ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, typeName, attributeName, attributeTypeName );
  }

  private static boolean isSupportedAttributeType( Class<?> preferenceType, PropertyDescriptor descriptor ) {
    Class<?> propertyType = descriptor.getPropertyType();
    if( SUPPORTED_COLLECTION_TYPES.contains( propertyType ) ) {
      return getActualTypeArgumentsOfGenericAttributeType( descriptor )
        .stream()
        .allMatch( typeArgument -> isSupportedValueType( loadTypeArgument( preferenceType, typeArgument ) ) );
    }
    return isSupportedValueType( propertyType );
  }

  private static boolean isSupportedValueType( Class<?> valueType ) {
    if( String.class == valueType ) {
      return true;
    }
    return hasValueOfFactoryMethod( valueType );
  }

  private static void verifyAllMethodsAreAttributeAccessors( Class<?> preferenceType, BeanInfo preferenceInfo ) {
    Set<Method> accessorMethods = streamOfPropertyDescriptors( preferenceInfo )
      .flatMap( descriptor -> asList( descriptor.getReadMethod(), descriptor.getWriteMethod() ).stream() )
      .collect( toSet() );
    Stream.of( preferenceInfo.getMethodDescriptors() )
      .forEach( descriptor -> verifyMethodIsAttributeAccessors( preferenceType, accessorMethods, descriptor ) );
  }

  private static Stream<PropertyDescriptor> streamOfPropertyDescriptors( BeanInfo preferenceInfo ) {
    return Stream.of( preferenceInfo.getPropertyDescriptors() );
  }

  private static void verifyMethodIsAttributeAccessors(
    Class<?> preferenceType, Set<Method> accessorMethods, MethodDescriptor descriptor )
  {
    String typeName = preferenceType.getName();
    String methodName = descriptor.getMethod().getName();
    boolean condition = accessorMethods.contains( descriptor.getMethod() );
    verifyCondition( condition, ERROR_INVALID_BEAN_PROPERTY_ACCESSOR, methodName, typeName );
  }
}