package com.codeaffine.home.control.internal.wiring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.util.inject.Context;

public class InjectionStrategy implements BiFunction<Constructor<?>, Context, Object[]> {

  @Override
  public Object[] apply( Constructor<?> constructor, Context context ) {
    return Stream.of( constructor.getParameters() )
      .map( parameter -> getParameter( context, parameter ) )
      .toArray();
  }

  private static Object getParameter( Context context, Parameter parameter ) {
    ByName annotation = parameter.getAnnotation( ByName.class );
    if( annotation != null ) {
      return getFromRegistry( context, parameter, annotation );
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