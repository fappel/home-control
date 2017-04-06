package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.stubZone;
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
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class LampCollectorTest {

  private ActivationSupplier activationSupplier;
  private EntityRegistry registry;
  private LampCollector collector;

  @Before
  public void setUp() {
    registry = stubRegistry();
    activationSupplier = mock( ActivationSupplier.class );
    collector = new LampCollector( registry, activationSupplier );
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
    stubActivationSupplier( LIVING_AREA );

    Set<Lamp> actual = collector.collectActivatedZoneLamps();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void collectActivatedZoneLampsIfActivationIsEmpty() {
    stubActivationSupplier();

    Set<Lamp> actual = collector.collectActivatedZoneLamps();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void collectZoneLamps() {
    Set<Lamp> expected = asSet( findLamp( DeskUplight ), findLamp( WindowUplight ) );

    Set<Lamp> actual = collector.collectZoneLamps( LIVING_AREA );

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void collectZoneLampsWithNonCompositeZoneEntityDefinitionArgument() {
    Set<Lamp> actual = collector.collectZoneLamps( DeskUplight );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void collectWithZoneActivationStrategy() {
    Set<Lamp> expected = asSet( findLamp( DeskUplight ), findLamp( WindowUplight ) );
    stubActivationSupplier( LIVING_AREA );

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
    new LampCollector( null, activationSupplier );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationSupplierArgument() {
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
  public void collectZoneLampsWithNullAsZoneEntityDefinitionArgument() {
    collector.collectZoneLamps( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsDefinitionArgument() {
    collector.findByDefinition( null );
  }

  private Lamp findLamp( LampDefinition definition ) {
    return registry.findByDefinition( definition );
  }

  private ActivationSupplier stubActivationSupplier( SectionDefinition... zoneDefinitions ) {
    Activation activation = stubStatus( zoneDefinitions );
    when( activationSupplier.getStatus() ).thenReturn( activation );
    return activationSupplier;
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