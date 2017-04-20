package com.codeaffine.home.control.engine.preference;

import static com.codeaffine.home.control.engine.preference.Messages.ERROR_LOADING_GENERIC_PARAMETER;
import static com.codeaffine.home.control.engine.util.ReflectionUtil.execute;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

class AttributeReflectionUtil {

  static final List<Class<?>> SUPPORTED_COLLECTION_TYPES = asList( Map.class, Set.class, List.class );

  static class NoSuchMethodRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    NoSuchMethodRuntimeException( NoSuchMethodException cause ) {
      super( cause );
    }
  }

  static List<Type> getActualTypeArgumentsOfGenericAttributeType( PropertyDescriptor descriptor ) {
    ParameterizedType type = ( ParameterizedType )descriptor.getWriteMethod().getGenericParameterTypes()[ 0 ];
    return Stream.of( type.getActualTypeArguments() ).collect( toList() );
  }

  static Class<?> loadTypeArgument( Class<?> preferenceType, Type typeArgument ) {
    try {
      return preferenceType.getClassLoader().loadClass( typeArgument.getTypeName() );
    } catch( ClassNotFoundException shouldNotHappen ) {
      String message = format( ERROR_LOADING_GENERIC_PARAMETER, typeArgument.getTypeName() );
      throw new IllegalStateException( message, shouldNotHappen );
    }
  }

  static void initializeAttribute( Object bean, PropertyDescriptor descriptor ) {
    execute( () -> {
      doInitializeAttribute( bean, descriptor );
      return null;
    } );
  }

  private static void doInitializeAttribute( Object bean, PropertyDescriptor descriptor )
    throws IllegalAccessException, InvocationTargetException
  {
    Method writeMethod = descriptor.getWriteMethod();
    Method readMethod = descriptor.getReadMethod();
    writeMethod.setAccessible( true );
    readMethod.setAccessible( true );
    writeMethod.invoke( bean, readMethod.invoke( bean ) );
  }

  static Method getValueOfFactoryMethod( Class<?> type ) throws NoSuchMethodRuntimeException {
    try {
      return type.getMethod( "valueOf", String.class );
    } catch( NoSuchMethodException cause ) {
      throw new NoSuchMethodRuntimeException( cause );
    }
  }

  static Object invokeArgumentFactoryMethod( Class<?> destinationType, Supplier<String> valueAsStringSupplier ) {
    Method argumentFactoryMethod = getValueOfFactoryMethod( destinationType );
    return invokeArgumentFactory( argumentFactoryMethod, valueAsStringSupplier );
  }

  private static Object invokeArgumentFactory( Method argumentFactoryMethod, Supplier<String> valueAsStringSupplier ) {
    return execute( () -> argumentFactoryMethod.invoke( null, valueAsStringSupplier.get() ) );
  }
}