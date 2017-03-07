package com.codeaffine.home.control.engine.wiring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.engine.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.logger.LoggerFactory;
import com.codeaffine.util.inject.Context;

public class InjectionStrategy implements BiFunction<Constructor<?>, Context, Object[]> {

  private final LoggerFactory loggerFactory;

  public InjectionStrategy() {
    loggerFactory = new LoggerFactoryAdapter();
  }

  @Override
  public Object[] apply( Constructor<?> constructor, Context context ) {
    return Stream.of( constructor.getParameters() )
      .map( parameter -> getParameter( context, parameter, constructor.getDeclaringClass() ) )
      .toArray();
  }

  private Object getParameter( Context context, Parameter parameter, Class<?> declaringClass ) {
    ByName annotation = parameter.getAnnotation( ByName.class );
    if( annotation != null ) {
      return getFromRegistry( context, parameter, annotation );
    }
    if( parameter.getType().isAssignableFrom( Logger.class ) ) {
      return loggerFactory.getLogger( declaringClass );
    }
    return context.get( parameter.getType() );
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static Object getFromRegistry( Context context, Parameter parameter, ByName itemByName ) {
    String itemName = itemByName.value();
    Class itemType = parameter.getType();
    return context.get( Registry.class ).getItem( itemName, itemType );
  }
}