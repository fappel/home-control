package com.codeaffine.home.control.status.test.util.model;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;

public class ModelRegistryHelper {

  public static Set<Section> stubSections( SectionDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubSection( definition ) ).collect( toSet() );
  }

  public static Section stubSection( SectionDefinition sectionDefinition ) {
    Section result = mock( Section.class );
    when( result.getDefinition() ).thenReturn( sectionDefinition );
    return result;
  }

  public static ActivationSensor stubActivationSensor( ActivationSensorDefinition motionSensorDefinition ) {
    ActivationSensor result = mock( ActivationSensor.class );
    when( result.getDefinition() ).thenReturn( motionSensorDefinition );
    return result;
  }

  public static EntityRegistry stubRegistry(
    Collection<Section> sections, Collection<ActivationSensor> motionSensors )
  {
    Set<Entity<?>> all = new HashSet<>( sections );
    all.addAll( motionSensors );
    EntityRegistry result = mock( EntityRegistry.class );
    when( result.findAll() ).thenReturn( all );
    when( result.findByDefinitionType( SectionDefinition.class ) ).thenReturn( sections );
    when( result.findByDefinitionType( ActivationSensorDefinition.class ) ).thenReturn( motionSensors );
    when( result.findByDefinition( any( EntityDefinition.class ) ) )
      .thenAnswer( invocation -> doFindByDefinition( all, invocation.getArguments()[ 0 ] ) );
    return result;
  }

  public static void equipWithActivationSensor( Section section, ActivationSensor motionSensor ) {
    when( section.getChildren() ).thenReturn( ( asList( motionSensor ) ) );
    when( section.getChildren( ActivationSensorDefinition.class ) ).thenReturn( asList( motionSensor ) );
  }

  private static Entity<?> doFindByDefinition( Set<Entity<?>> all, Object definition ) {
    return all.stream().filter( entity -> entity.getDefinition() == definition ).findFirst().get();
  }
}