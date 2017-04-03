package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.DeskUplight;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.WORK_AREA;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class LampControlTest {

  private static final SectionDefinition ZONE = WORK_AREA;
  private static final Lamp LAMP = stubLamp( DeskUplight );

  private LampControl control;
  private LampSwitchOperation operation;

  @Before
  public void setUp() {
    operation = mock( LampSwitchOperation.class );
    control = new LampControl( operation, stubCollector( ZONE, LAMP ) );
  }

  @Test
  public void switchOnZoneLamps() {
    control.switchOnZoneLamps( ZONE );

    verify( operation ).setLampsToSwitchOn( LAMP.getDefinition() );
  }

  @Test
  public void switchOffZoneLamps() {
    control.switchOffZoneLamps( ZONE );

    verify( operation ).setLampsToSwitchOff( LAMP.getDefinition() );
  }

  @Test
  public void setZoneLampsForFiltering() {
    control.setZoneLampsForFiltering( ZONE );

    verify( operation ).addFilterableLamps( LAMP.getDefinition() );
  }

  @Test
  public void switchOnLamps() {
    control.switchOnLamps( LAMP.getDefinition() );

    verify( operation ).setLampsToSwitchOn( LAMP.getDefinition() );
  }

  @Test
  public void switchOffLamps() {
    control.switchOffLamps( LAMP.getDefinition() );

    verify( operation ).setLampsToSwitchOff( LAMP.getDefinition() );
  }

  @Test
  public void setLampsForFiltering() {
    control.setLampsForFiltering( LAMP.getDefinition() );

    verify( operation ).addFilterableLamps( LAMP.getDefinition() );
  }

  @Test
  public void toDefinitions() {
    LampDefinition[] actual = LampControl.toDefinitions( asSet( LAMP ) );

    assertThat( actual ).containsExactly( LAMP.getDefinition() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOnZoneLampsWithNullAsZoneDefinitionsArgument() {
    control.switchOnZoneLamps( ( SectionDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOnZoneLampsWithNullElementInZoneDefinitionsArgumentArray() {
    control.switchOnZoneLamps( new SectionDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOffZoneLampsWithNullAsZoneDefinitionsArgument() {
    control.switchOffZoneLamps( ( SectionDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOffZoneLampsWithNullElementInZoneDefinitionsArgumentArray() {
    control.switchOffZoneLamps( new SectionDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setZoneLampsForFilteringWithNullAsZoneDefinitionsArgument() {
    control.setZoneLampsForFiltering( ( SectionDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setZoneLampsForFilteringWithNullElementInZoneDefinitionsArgumentArray() {
    control.setZoneLampsForFiltering( new SectionDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOnLampsWithNullAsLampDefinitionsArgument() {
    control.switchOnLamps( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOnLampsWithNullElementInLampDefinitionsArgumentArray() {
    control.switchOnLamps( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOffLampsWithNullAsLampDefinitionsArgument() {
    control.switchOffLamps( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void switchOffLampsWithNullElementInLampDefinitionsArgumentArray() {
    control.switchOffLamps( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampsForFilteringWithNullAsLampDefinitionsArgument() {
    control.setLampsForFiltering( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampsForFilteringWithNullElementInLampDefinitionsArgumentArray() {
    control.setLampsForFiltering( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void toDefinitionsWithNullAsLampsArgument() {
    LampControl.toDefinitions( null );
  }

  private static Lamp stubLamp( LampDefinition lampDefinition ) {
    Lamp result = mock( Lamp.class );
    when( result.getDefinition() ).thenReturn( lampDefinition );
    return result;
  }

  private static LampCollector stubCollector( EntityDefinition<?> zone, Lamp lamp ) {
    LampCollector result = mock( LampCollector.class );
    when( result.collectZoneLamps( zone ) ).thenReturn( asSet( lamp ) );
    return result;
  }
}