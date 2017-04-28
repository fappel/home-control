package com.codeaffine.home.control.util.reflection;

import static com.codeaffine.home.control.util.reflection.Messages.ERROR_LOADING_GENERIC_PARAMETER;
import static com.codeaffine.home.control.util.reflection.PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType;
import static com.codeaffine.home.control.util.reflection.ReflectionUtil.execute;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
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

public class AttributeReflectionUtil {

  public static final List<Class<?>> SUPPORTED_COLLECTION_TYPES = asList( Map.class, Set.class, List.class );

  public static class NoSuchMethodRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoSuchMethodRuntimeException( NoSuchMethodException cause ) {
      super( cause );
    }
  }

  public static List<Type> getActualTypeArgumentsOfGenericAttributeType( PropertyDescriptor descriptor ) {
    ParameterizedType type = ( ParameterizedType )descriptor.getWriteMethod().getGenericParameterTypes()[ 0 ];
    return Stream.of( type.getActualTypeArguments() ).collect( toList() );
  }

  public static Class<?> loadTypeArgument( Class<?> preferenceType, Type typeArgument ) {
    try {
      return preferenceType.getClassLoader().loadClass( typeArgument.getTypeName() );
    } catch( ClassNotFoundException shouldNotHappen ) {
      String message = format( ERROR_LOADING_GENERIC_PARAMETER, typeArgument.getTypeName() );
      throw new IllegalStateException( message, shouldNotHappen );
    }
  }

  public static void initializeAttribute( Object bean, PropertyDescriptor descriptor ) {
    execute( () -> {
      doInitializeAttribute( bean, descriptor );
      return null;
    } );
  }

  public static boolean hasValueOfFactoryMethod( Class<?> valueType ) {
    try {
      Class<?> type = replacePrimitiveTypeByBoxedType( valueType );
      Method factoryMethod = getValueOfFactoryMethod( type );
      return isStatic( factoryMethod.getModifiers() );
    } catch( Exception ex  ) {
      return false;
    }
  }

  public static Method getValueOfFactoryMethod( Class<?> valueType ) throws NoSuchMethodRuntimeException {
    try {
      Class<?> type = replacePrimitiveTypeByBoxedType( valueType );
      return type.getMethod( "valueOf", String.class );
    } catch( NoSuchMethodException cause ) {
      throw new NoSuchMethodRuntimeException( cause );
    }
  }

  public static Object invokeArgumentFactoryMethod( Class<?> destinationType, Supplier<String> valueAsStringSupplier ) {
    Method argumentFactoryMethod = getValueOfFactoryMethod( destinationType );
    return invokeArgumentFactory( argumentFactoryMethod, valueAsStringSupplier );
  }

  private static Object invokeArgumentFactory( Method argumentFactoryMethod, Supplier<String> valueAsStringSupplier ) {
    return execute( () -> {
      argumentFactoryMethod.setAccessible( true );
      return argumentFactoryMethod.invoke( null, valueAsStringSupplier.get() );
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
}