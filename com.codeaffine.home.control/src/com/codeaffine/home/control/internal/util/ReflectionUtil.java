package com.codeaffine.home.control.internal.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}