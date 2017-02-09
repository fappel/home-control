package com.codeaffine.home.control.internal.wiring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.ItemByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Status;
import com.codeaffine.util.inject.Context;

public class InjectionStrategy implements BiFunction<Constructor<?>, Context, Object[]> {

  @Override
  public Object[] apply( Constructor<?> constructor, Context context ) {
    return Stream.of( constructor.getParameters() )
      .map( parameter -> getParameter( context, parameter ) )
      .toArray();
  }

  private static Object getParameter( Context context, Parameter parameter ) {
    ItemByName itemByNameAnnotation = parameter.getAnnotation( ItemByName.class );
    if( itemByNameAnnotation != null ) {
      return getFromRegistry( context, parameter, itemByNameAnnotation );
    }
    return context.get( parameter.getType() );
  }

  @SuppressWarnings("unchecked")
  private static Object getFromRegistry( Context context, Parameter parameter, ItemByName itemByName ) {
    String itemName = itemByName.value();
    Class<Item<? extends Status>> itemType = ( Class<Item<? extends Status>> ) parameter.getType();
    return context.get( Registry.class ).getItem( itemName, itemType );
  }
}