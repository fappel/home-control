package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.home.control.internal.util.ReflectionUtil.invoke;
import static com.codeaffine.home.control.internal.wiring.Messages.ERROR_INVALID_PARAMETER_DECLARATION;
import static java.lang.String.format;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.UpdateEvent;

@SuppressWarnings({ "unchecked", "rawtypes" })
class EventWiring {

  private final Registry registry;

  EventWiring( Registry registry ) {
    this.registry = registry;
  }

  void wire( Object managedObject ) throws WiringException {
    Stream.of( managedObject.getClass().getDeclaredMethods() )
      .filter( method -> isEventMethod( method ) )
      .forEach( method -> wireObserver( managedObject, method ) );
  }

  private static boolean isEventMethod( Method method ) {
    return method.getAnnotation( Observe.class ) != null;
  }

  private void wireObserver( Object managedObject, Method method ) {
    verifyParameterCount( method );
    verifyParameterType( method );

    Class itemType = determineItemType( managedObject, method );
    registerEventListener( managedObject, method, itemType );
  }

  private static Class determineItemType( Object managedObject, Method method ) {
    return Stream.of( method.getGenericParameterTypes() )
      .filter( paramType -> paramType instanceof ParameterizedType )
      .map( paramType -> ( ( ParameterizedType )paramType ).getActualTypeArguments()[ 0 ] )
      .map( type -> loadType( managedObject, type ) )
      .findFirst()
      .get();
  }

  private static Class<?> loadType( Object managedObject, Type type ) {
    try {
      return managedObject.getClass().getClassLoader().loadClass( type.getTypeName() );
    } catch( ClassNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private void registerEventListener( Object managedObject, Method method, Class itemType ) {
    Parameter parameter = method.getParameters()[ 0 ];
    String key = method.getAnnotation( Observe.class ).value();
    Item<?, ?> item = registry.getItem( key, itemType );
    if( parameter.getType().isAssignableFrom( ChangeEvent.class ) ) {
      item.addChangeListener( evt -> invoke( method, managedObject, evt ) );
    } else {
      item.addUpdateListener( evt -> invoke( method, managedObject, evt ) );
    }
  }

  private static void verifyParameterType( Method method ) {
    if(    !method.getParameters()[ 0 ].getType().isAssignableFrom( ChangeEvent.class )
        && !method.getParameters()[ 0 ].getType().isAssignableFrom( UpdateEvent.class ) )
    {
      throwInvalidArgumentDeclaration( method );
    }
}

  private static void verifyParameterCount( Method method ) {
    if( method.getParameterCount() != 1 ) {
      throwInvalidArgumentDeclaration( method );
    }
  }

  private static void throwInvalidArgumentDeclaration( Method method ) {
    String clazz = method.getDeclaringClass().getName();
    String name = method.getName();
    String change = ChangeEvent.class.getName();
    String update = UpdateEvent.class.getName();
    throw new WiringException( format( ERROR_INVALID_PARAMETER_DECLARATION, name, clazz, change, update ) );
  }
}