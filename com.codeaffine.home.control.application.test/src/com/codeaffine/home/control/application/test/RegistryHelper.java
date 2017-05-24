package com.codeaffine.home.control.application.test;

import static com.codeaffine.home.control.status.type.OnOff.OFF;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.type.OnOff;

public class RegistryHelper {

  static final Integer DEFAULT_LIGHT_SENSOR_VALUE = Integer.valueOf( 50 );

  public static Set<Lamp> stubLamps( LampDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubLamp( definition ) ).collect( toSet() );
  }

  public static Lamp stubLamp( LampDefinition lampDefinition ) {
    Lamp result = mock( Lamp.class );
    when( result.getDefinition() ).thenReturn( lampDefinition );
    when( result.getOnOffStatus() ).thenReturn( OFF );
    when( result.toString() ).thenReturn( lampDefinition.toString() );
    doAnswer( invocation -> setOnOff( invocation, result ) )
      .when( result ).setOnOffStatus( any( OnOff.class ) );
    return result;
  }

  public static Set<Section> stubSections( SectionDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubSection( definition ) ).collect( toSet() );
  }

  public static Section stubSection( SectionDefinition sectionDefinition ) {
    Section result = mock( Section.class );
    when( result.getDefinition() ).thenReturn( sectionDefinition );
    when( result.toString() ).thenReturn( sectionDefinition.name() );
    return result;
  }

  public static Set<ActivationSensor> stubActivationSensors( ActivationSensorDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubActivationSensor( definition ) ).collect( toSet() );
  }

  public static ActivationSensor stubActivationSensor( ActivationSensorDefinition activationSensorDefinition ) {
    ActivationSensor result = mock( ActivationSensor.class );
    when( result.getDefinition() ).thenReturn( activationSensorDefinition );
    when( result.toString() ).thenReturn( activationSensorDefinition.name() );
    return result;
  }

  public static Set<LightSensor> stubLightSensors( LightSensorDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubLightSensor( definition ) ).collect( toSet() );
  }

  public static LightSensor stubLightSensor( LightSensorDefinition lightSensorDefinition ) {
    LightSensor result = mock( LightSensor.class );
    when( result.getDefinition() ).thenReturn( lightSensorDefinition );
    when( result.getLightValue() ).thenReturn( DEFAULT_LIGHT_SENSOR_VALUE );
    when( result.toString() ).thenReturn( lightSensorDefinition.name() );
    return result;
  }

  public static EntityRegistry stubRegistry(
    Collection<Section> sections,
    Collection<Lamp> lamps,
    Collection<ActivationSensor> activationSensors,
    Collection<LightSensor> lightSensors )
  {
    Set<Entity<?>> all = new HashSet<>( sections );
    all.addAll( lamps );
    all.addAll( activationSensors );
    all.addAll( lightSensors );
    EntityRegistry result = mock( EntityRegistry.class );
    when( result.findAll() ).thenReturn( all );
    when( result.findByDefinitionType( SectionDefinition.class ) ).thenReturn( sections );
    when( result.findByDefinitionType( LampDefinition.class ) ).thenReturn( lamps );
    when( result.findByDefinitionType( ActivationSensorDefinition.class ) ).thenReturn( activationSensors );
    when( result.findByDefinitionType( LightSensorDefinition.class ) ).thenReturn( lightSensors );
    when( result.findByDefinition( any( EntityDefinition.class ) ) )
      .thenAnswer( invocation -> doFindByDefinition( all, invocation.getArguments()[ 0 ] ) );
    return result;
  }

  public static void equipWithLightSensor(
    EntityRegistry registryStub, SectionDefinition sectionDefinition, LightSensorDefinition definition )
  {
    LightSensor sensor = getLightSensor( registryStub, definition );
    equipWithLightSensor( registryStub.findByDefinition( sectionDefinition ), sensor );
  }

  private static LightSensor getLightSensor( EntityRegistry registryStub, LightSensorDefinition definition ) {
    return registryStub.findByDefinition( definition );
  }

  public static void equipWithLightSensor( Section section, LightSensor lightSensor ) {
    when( section.getChildren() ).thenReturn( ( asList( lightSensor ) ) );
    when( section.getChildren( LightSensorDefinition.class ) ).thenReturn( asList( lightSensor ) );
  }

  public static void equipWithActivationSensor(
    EntityRegistry registryStub, SectionDefinition sectionDefinition, ActivationSensorDefinition definition )
  {
    ActivationSensor sensor = getActivationSensor( registryStub, definition );
    equipWithActivationSensor( registryStub.findByDefinition( sectionDefinition ), sensor );
  }

  private static ActivationSensor getActivationSensor(
    EntityRegistry registryStub, ActivationSensorDefinition definition )
  {
    return registryStub.findByDefinition( definition );
  }

  public static void equipWithActivationSensor( Section section, ActivationSensor activationSensor ) {
    when( section.getChildren() ).thenReturn( ( asList( activationSensor ) ) );
    when( section.getChildren( ActivationSensorDefinition.class ) ).thenReturn( asList( activationSensor ) );
  }

  public static void equipWithLamp(
    EntityRegistry registryStub, SectionDefinition sectionDefinition, LampDefinition ... lampDefinitions )
  {
    equipWithLamp( registryStub.findByDefinition( sectionDefinition ), collectLamps( registryStub, lampDefinitions ) );
  }

  private static Entity<?> doFindByDefinition( Set<Entity<?>> all, Object definition ) {
    return all.stream().filter( entity -> entity.getDefinition() == definition ).findFirst().get();
  }

  private static Set<Lamp> collectLamps( EntityRegistry registryStub, LampDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> registryStub.findByDefinition( definition ) ).collect( toSet() );
  }

  private static void equipWithLamp( Section section, Set<Lamp> lamps ) {
    when( section.getChildren() ).thenReturn( ( asList( lamps.toArray( new Lamp[ lamps.size() ] ) ) ) );
    when( section.getChildren( LampDefinition.class ) ).thenReturn( lamps );
  }

  private static Object setOnOff( InvocationOnMock invocation, Lamp lamp ) {
    OnOff onOff = ( OnOff )invocation.getArguments()[ 0 ];
    when( lamp.getOnOffStatus() ).thenReturn( onOff );
    return onOff;
  }
}