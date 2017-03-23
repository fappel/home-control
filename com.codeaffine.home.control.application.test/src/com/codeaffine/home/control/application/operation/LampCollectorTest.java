package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.stubZone;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class LampCollectorTest {

  private ActivationProvider activationProvider;
  private EntityRegistry registry;
  private LampCollector collector;

  @Before
  public void setUp() {
    registry = stubRegistry();
    activationProvider = mock( ActivationProvider.class );
    collector = new LampCollector( registry, activationProvider );
  }

  @Test
  public void collectAllLamps() {
    Set<Lamp> actual = collector.collectAllLamps();

    assertThat( actual )
      .isNotEmpty()
      .isEqualTo( registry.findByDefinitionType( LampDefinition.class ) );
  }

  @Test
  public void collectWithSameZone() {
    Set<Lamp> expected = asSet( findLamp( DeskUplight ), findLamp( WindowUplight ) );

    Set<Lamp> actual = collector.collectWithSameZone( findLamp( DeskUplight ) );

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void collectActivatedZoneLamps() {
    Set<Lamp> expected = asSet( findLamp( DeskUplight ), findLamp( WindowUplight ) );
    stubActivationProvider( LIVING_AREA );

    Set<Lamp> actual = collector.collectActivatedZoneLamps();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void collectActivatedZoneLampsIfActivationIsEmpty() {
    stubActivationProvider();

    Set<Lamp> actual = collector.collectActivatedZoneLamps();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void collectWithZoneActivationStrategy() {
    Set<Lamp> expected = asSet( findLamp( DeskUplight ), findLamp( WindowUplight ) );
    stubActivationProvider( LIVING_AREA );

    Set<Lamp> actual = collector.collect( ZONE_ACTIVATION );

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void collectWithAllStrategy() {
    Set<Lamp> actual = collector.collect( ALL );

    assertThat( actual )
      .isNotEmpty()
      .isEqualTo( registry.findByDefinitionType( LampDefinition.class ) );
  }

  @Test
  public void collectWithNoneStrategy() {
    Set<Lamp> actual = collector.collect( NONE );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void findByDefinition() {
    Lamp actual = collector.findByDefinition( DeskUplight );

    assertThat( actual ).isSameAs( findLamp( DeskUplight ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new LampCollector( null, activationProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationProviderArgument() {
    new LampCollector( registry, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void collectWithNullAsStrategyArgument() {
    collector.collect( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void collectWithSameZoneWithNullAsLampArgument() {
    collector.collectWithSameZone( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsDefinitionArgument() {
    collector.findByDefinition( null );
  }

  private Lamp findLamp( LampDefinition definition ) {
    return registry.findByDefinition( definition );
  }

  private ActivationProvider stubActivationProvider( SectionDefinition... zoneDefinitions ) {
    Activation activation = stubStatus( zoneDefinitions );
    when( activationProvider.getStatus() ).thenReturn( activation );
    return activationProvider;
  }

  private Activation stubStatus( SectionDefinition... zoneDefinitions ) {
    return new Activation( Stream.of( zoneDefinitions )
      .map( zoneDefinition -> registry.findByDefinition( zoneDefinition ) )
      .map( zoneEntity -> stubZone( zoneEntity ) )
      .collect( toSet() ) );
  }

  static EntityRegistry stubRegistry() {
    Set<Lamp> lamps = stubLamps( KitchenCeiling, HallCeiling, DeskUplight, WindowUplight );
    Set<Section> rooms = stubSections( DINING_AREA, HALL, LIVING_AREA );
    EntityRegistry result = RegistryHelper.stubRegistry( rooms, lamps, emptySet() );
    equipWithLamp( result, DINING_AREA, KitchenCeiling );
    equipWithLamp( result, HALL, HallCeiling );
    equipWithLamp( result, LIVING_AREA, DeskUplight, WindowUplight );
    return result;
  }
}