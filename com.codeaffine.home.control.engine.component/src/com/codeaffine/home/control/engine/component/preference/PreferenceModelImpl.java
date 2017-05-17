package com.codeaffine.home.control.engine.component.preference;

import static com.codeaffine.home.control.engine.component.preference.Messages.ERROR_SAVING_MODEL;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.*;
import static com.codeaffine.home.control.util.reflection.PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.*;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.PreferenceEvent;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PreferenceModelImpl implements PreferenceModel {

  private final PreferenceTypeValidator preferenceTypeValidator;
  private final Map<String, Map<String,String>> attributes;
  private final TypeUnloadTracker typeUnloadTracker;
  private final Map<Class<?>, Object> preferences;
  private final EventBus eventBus;
  private final Gson gson;

  public PreferenceModelImpl( EventBus eventBus, TypeUnloadTracker typeUnloadTracker ) {
    verifyNotNull( typeUnloadTracker, "typeUnloadTracker" );
    verifyNotNull( eventBus, "eventBus" );

    this.preferenceTypeValidator = new PreferenceTypeValidator();
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    this.preferences = new HashMap<>();
    this.attributes = new HashMap<>();
    this.typeUnloadTracker = typeUnloadTracker;
    this.eventBus = eventBus;
  }

  public void load( InputStream in ) {
    verifyNotNull( in, "in" );

    InputStreamReader reader = new InputStreamReader( in );
    @SuppressWarnings("unchecked")
    Map<String, Map<String, String>> loaded = gson.fromJson( reader, HashMap.class );
    if( loaded != null ) {
      attributes.clear();
      attributes.putAll( loaded );
    }
  }

  public void save( OutputStream out ) {
    verifyNotNull( out, "out" );

    OutputStreamWriter writer = new OutputStreamWriter( out );
    try {
      writer.write( gson.toJson( attributes ) );
      writer.flush();
    } catch( IOException cause ) {
      throw new IllegalStateException( ERROR_SAVING_MODEL, cause );
    }
  }

  @Override
  public <T> T get( Class<T> preferenceType ) {
    verifyNotNull( preferenceType, "prefernceType" );

    typeUnloadTracker.registerUnloadHook( preferenceType, () -> preferences.remove( preferenceType ) );
    preferences.computeIfAbsent( preferenceType, type -> createInstance( preferenceType ) );
    return preferenceType.cast( preferences.get( preferenceType ) );
  }

  @Override
  public Set<Class<?>> getAllPreferenceTypes() {
    return new HashSet<>( preferences.keySet() );
  }

  private Object createInstance( Class<?> preferenceType ) {
    BeanInfo info = preferenceTypeValidator.validate( preferenceType );
    ClassLoader loader = preferenceType.getClassLoader();
    Class<?>[] interfaces = new Class[] { preferenceType };
    attributes.computeIfAbsent( preferenceType.getName(), typeName -> new HashMap<>() );
    Map<String, String> values = attributes.get( preferenceType.getName() );
    InvocationHandler invocationHandler = ( proxy, method, arx ) -> invoke( proxy, info, values, method, arx );
    return initialize( info, newProxyInstance( loader, interfaces, invocationHandler ) );
  }

  private Object invoke( Object proxy, BeanInfo info, Map<String, String> values, Method method, Object[] args )
    throws Throwable
  {
    if( isToStringCall( method ) ) {
      return createStringRepresentation( info, values );
    }
    return invokeAttributeAccessor( proxy, info, values, method, args );
  }

  private static boolean isToStringCall( Method method ) {
    return method.getName().equals( "toString" );
  }

  private static String createStringRepresentation( BeanInfo info, Map<String, String> values ) {
    return info.getBeanDescriptor().getBeanClass().getSimpleName() + " " + values;
  }

  private Object invokeAttributeAccessor(
    Object proxy, BeanInfo info, Map<String, String> values, Method method, Object[] args )
  {
    Object result;
    Class<?> type = info.getBeanDescriptor().getBeanClass();
    Optional<PropertyDescriptor> writeDescriptor = findWriteDescriptor( info, method );
    storeAttributeValueIfPresent( type, proxy, values, args, writeDescriptor );
    Optional<PropertyDescriptor> readDescriptor = findReadDescriptor( info, method );
    result = readValueIfReadDescriptorIsPresent( type, values, readDescriptor );
    return result;
  }

  private static Optional<PropertyDescriptor> findWriteDescriptor( BeanInfo info, Method method ) {
    return findDescriptor( info, descriptor -> descriptor.getWriteMethod().equals( method ) );
  }

  private static Optional<PropertyDescriptor> findReadDescriptor( BeanInfo info, Method method ) {
    return findDescriptor( info, descriptor -> descriptor.getReadMethod().equals( method ) );
  }

  private static Optional<PropertyDescriptor> findDescriptor( BeanInfo info, Predicate<PropertyDescriptor> predicate ) {
    return Stream.of( info.getPropertyDescriptors() ).filter( predicate ).findFirst();
  }

  private void storeAttributeValueIfPresent(
    Class<?> type, Object proxy, Map<String, String> values, Object[] args, Optional<PropertyDescriptor> descriptor )
  {
    if( descriptor.isPresent() ) {
      storeAttributeValue( type, proxy, values, args, descriptor );
    }
  }

  private void storeAttributeValue(
    Class<?> type, Object source, Map<String, String> values, Object[] args, Optional<PropertyDescriptor> descriptor )
  {
    String attributeName = descriptor.get().getName();
    Object oldValue = restoreOldValue( type, values, descriptor, attributeName );
    Object newValue = args[ 0 ];
    values.put( attributeName, verifyNotNull( newValue, attributeName ).toString() );
    if( !newValue.equals( oldValue ) ) {
      eventBus.post( new PreferenceEvent( source, attributeName, oldValue, newValue ) );
    }
  }

  private static Object restoreOldValue(
    Class<?> preferenceType, Map<String, String> values, Optional<PropertyDescriptor> descriptor, String attributeName )
  {
    if( values.containsKey( attributeName ) ) {
      return convertFromString( preferenceType, descriptor, () -> values.get( attributeName ) );
    }
    return null;
  }

  private static Object readValueIfReadDescriptorIsPresent(
    Class<?> preferenceType, Map<String, String> values, Optional<PropertyDescriptor> descriptor )
  {
    if( descriptor.isPresent() ) {
      return readValue( preferenceType, values, descriptor );
    }
    return null;
  }

  private static Object readValue(
    Class<?> preferenceType, Map<String, String> values, Optional<PropertyDescriptor> descriptor )
  {
    if( !values.containsKey( descriptor.get().getName() ) ) {
      return readDefaultValueFromAnnotation( preferenceType, descriptor );
    }
    return convertFromString( preferenceType, descriptor, () -> values.get( descriptor.get().getName() ) );
  }

  private static Object readDefaultValueFromAnnotation(
    Class<?> preferenceType, Optional<PropertyDescriptor> descriptor )
  {
    DefaultValue defaultValueAnnotation = descriptor.get().getReadMethod().getAnnotation( DefaultValue.class );
    return convertFromString( preferenceType, descriptor, () -> defaultValueAnnotation.value() );
  }

  private static Object convertFromString(
    Class<?> preferenceType, Optional<PropertyDescriptor> descriptor, Supplier<String> valueAsStringSupplier )
  {
    Class<?> returnType = descriptor.get().getReadMethod().getReturnType();
    if( SUPPORTED_COLLECTION_TYPES.contains( returnType ) ) {
      return convertToCollectionFromString( preferenceType, descriptor, valueAsStringSupplier );
    }
    return convertFromString( returnType, valueAsStringSupplier );
  }

  private static Object convertToCollectionFromString(
    Class<?> preferenceType, Optional<PropertyDescriptor> descriptor, Supplier<String> valueAsStringSupplier )
  {
    Class<?> returnType = descriptor.get().getReadMethod().getReturnType();
    if( Set.class == returnType ) {
      return Stream.of( parseEntries( valueAsStringSupplier ) )
       .map( entry -> convertToTypeArgumentFromString( 0, preferenceType, descriptor, entry ) )
       .collect( toSet() );
    }
    if( List.class == returnType ) {
      return Stream.of( parseEntries( valueAsStringSupplier ) )
       .map( entry -> convertToTypeArgumentFromString( 0, preferenceType, descriptor, entry ) )
       .collect( toList() );
    }
    return Stream.of( parseEntries( valueAsStringSupplier ) )
      .collect( toMap( entry -> convertToTypeArgumentFromString( 0, preferenceType, descriptor, entry ),
                       entry -> convertToTypeArgumentFromString( 1, preferenceType, descriptor, entry ) ) );
  }

  private static String[] parseEntries( Supplier<String> valueAsStringSupplier ) {
    String value = valueAsStringSupplier.get();
    value = value.substring( 1, value.length() - 1 );
    return value.isEmpty() ? new String[ 0 ] : value.split( "," );
  }

  private static Object convertToTypeArgumentFromString(
    int index, Class<?> preferenceType, Optional<PropertyDescriptor> descriptor, String entry )
  {
    List<Type> actualTypeArguments = getActualTypeArgumentsOfGenericAttributeType( descriptor.get() );
    return convertToTypeArgumentFromString( preferenceType, actualTypeArguments, index, entry );
  }

  private static Object convertToTypeArgumentFromString(
    Class<?> preferenceType, List<Type> actualTypeArguments, int index, String entry )
  {
    Supplier<String> entrySupplier = () -> entry.split( "=" )[ index ].trim();
    return convertFromString( loadTypeArgument( preferenceType, actualTypeArguments.get( index ) ), entrySupplier );
  }

  private static Object convertFromString( Class<?> type, Supplier<String> valueAsStringSupplier ) {
    if( String.class == type ) {
      return valueAsStringSupplier.get();
    }
    return invokeArgumentFactoryMethod( replacePrimitiveTypeByBoxedType( type ), valueAsStringSupplier );
  }

  private static Object initialize( BeanInfo info, Object bean ) {
    Stream.of( info.getPropertyDescriptors() ) .forEach( descriptor -> initializeAttribute( bean, descriptor ) );
    return bean;
  }
}