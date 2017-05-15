package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.util.reflection.ReflectionUtil.invoke;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.codeaffine.home.control.ComponentAccessService;

class PreferenceProxyFactory {

  <T> T create( T delegate, Class<T> type, ComponentAccessService componentAccessService ) {
    verifyNotNull( componentAccessService, "componentAccessService" );
    verifyNotNull( delegate, "delegate" );
    verifyNotNull( type, "type" );

    ClassLoader loader = type.getClassLoader();
    Class<?>[] interfaces = new Class<?>[] { type };
    return type.cast( newProxyInstance( loader, interfaces, newHandler( delegate, componentAccessService ) ) );
  }

  private static <T> InvocationHandler newHandler( T delegate, ComponentAccessService componentAccessService ) {
    return ( proxy, method, arx ) -> accessAttribute( componentAccessService, delegate, method, arx );
  }

  private static <T> Object accessAttribute(
    ComponentAccessService componentAccessService, T delegate, Method method, Object[] arx )
  {
    return componentAccessService.submit( context -> invoke( method, delegate, arx ) );
  }
}