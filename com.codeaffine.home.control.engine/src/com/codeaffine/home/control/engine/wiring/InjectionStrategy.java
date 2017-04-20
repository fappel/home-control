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
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.preference.PreferenceModel;
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
    Preference preferenceAnnotation = parameter.getType().getAnnotation( Preference.class );
    if( preferenceAnnotation != null ) {
      return getFromPreferernceModel( context, parameter.getType() );
    }
    ByName byNameAnnotation = parameter.getAnnotation( ByName.class );
    if( byNameAnnotation != null ) {
      return getFromRegistry( context, parameter, byNameAnnotation );
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

  private static Object getFromPreferernceModel( Context context, Class<?> type ) {
    return context.get( PreferenceModel.class ).get( type );
  }
}