package com.codeaffine.home.control.engine.util;

import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ReflectionUtil {

  public static Object invoke( Method method, Object object, Object... arguments ) {
    return execute( () -> {
      method.setAccessible( true );
      return method.invoke( object, arguments );
    } );
  }

  public static Collection<Method> getAnnotatedMethods( Object object, Class<? extends Annotation> annotationType ) {
    return Stream.of( object.getClass().getDeclaredMethods() )
      .filter( method -> method.getAnnotation( annotationType ) != null )
      .map( method  -> method )
      .collect( toSet() );
  }

  public static Object execute( Callable<?> reflectiveCall ) {
    try {
      return reflectiveCall.call();
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( InvocationTargetException ite ) {
      if( ite.getCause() instanceof RuntimeException ) {
        throw ( RuntimeException )ite.getCause();
      }
      throw new IllegalStateException( ite.getCause() );
    } catch( Exception shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}