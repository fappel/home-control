package com.codeaffine.home.control.engine.util;

import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

public class ReflectionUtil {

  public static Object invoke( Method method, Object object, Object... arguments ) {
    try {
      method.setAccessible( true );
      return method.invoke( object, arguments );
    } catch( InvocationTargetException e ) {
      Throwable targetException = e.getTargetException();
      if( targetException instanceof RuntimeException ) {
        throw ( RuntimeException )targetException;
      }
      throw new IllegalStateException( targetException );
    } catch( IllegalAccessException | IllegalArgumentException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  public static Collection<Method> getAnnotatedMethods( Object object, Class<? extends Annotation> annotationType ) {
    return Stream.of( object.getClass().getDeclaredMethods() )
      .filter( method -> method.getAnnotation( annotationType ) != null )
      .map( method  -> method )
      .collect( toSet() );
  }
}