package com.codeaffine.home.control.application.test;

import static com.codeaffine.home.control.application.type.OnOff.OFF;
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
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class RegistryHelper {

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
    return result;
  }

  public static MotionSensor stubMotionSensor( MotionSensorDefinition motionSensorDefinition ) {
    MotionSensor result = mock( MotionSensor.class );
    when( result.getDefinition() ).thenReturn( motionSensorDefinition );
    return result;
  }

  public static EntityRegistry stubRegistry(
    Collection<Section> sections, Collection<Lamp> lamps, Collection<MotionSensor> motionSensors )
  {
    Set<Entity<?>> all = new HashSet<>( sections );
    all.addAll( lamps );
    all.addAll( motionSensors );
    EntityRegistry result = mock( EntityRegistry.class );
    when( result.findAll() ).thenReturn( all );
    when( result.findByDefinitionType( SectionDefinition.class ) ).thenReturn( sections );
    when( result.findByDefinitionType( LampDefinition.class ) ).thenReturn( lamps );
    when( result.findByDefinitionType( MotionSensorDefinition.class ) ).thenReturn( motionSensors );
    when( result.findByDefinition( any( EntityDefinition.class ) ) )
      .thenAnswer( invocation -> doFindByDefinition( all, invocation.getArguments()[ 0 ] ) );
    return result;
  }

  public static void equipWithMotionSensor( Section section, MotionSensor motionSensor ) {
    when( section.getChildren() ).thenReturn( ( asList( motionSensor ) ) );
    when( section.getChildren( MotionSensorDefinition.class ) ).thenReturn( asList( motionSensor ) );
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