package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.application.test.ZoneActivationHelper.stubZoneActivation;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSelectionStrategy;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class LampSwitchOperationTest {

  private ZoneActivationProvider zoneActivationProvider;
  private LampSwitchOperation operation;
  private EntityRegistry registry;
  private FollowUpTimer timer;

  @Before
  public void setUp() {
    registry = stubRegistry();
    timer = mock( FollowUpTimer.class );
    zoneActivationProvider = mock( ZoneActivationProvider.class );
    operation = new LampSwitchOperation( registry, zoneActivationProvider, timer );
  }

  @Test
  public void operateOnSingleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSingleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilterAndExplicitOnOffSettings() {
    operation.reset();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.setLampsToSwitchOff( KitchenCeiling );
    operation.setLampsToSwitchOn( DeskUplight );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyALL() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnLampSelectionStrategyALLWithLampFilter() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.setLampFilter( lamp -> lamp.getDefinition() == KitchenCeiling );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyNONE() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.NONE );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSubsequentEvents() {
    operation.reset();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA ) ) );

    operation.reset();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnZoneEngagementWithLampDelayTimer() {
    stubZoneActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementIfDelayedLampIsSwitchedOn() {
    stubZoneActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.setDelayed();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    stubZoneActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionThatFinallyActivatesDelayedLamp() {
    stubZoneActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( HALL );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffOnOnlyZoneActivation ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionWithSubsequentActivation() {
    stubZoneActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWithTransitionEndAsOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayedSecondTime = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand( 2 ).run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerActivation = findLamp( HallCeiling ).getOnOffStatus();

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffWithTransitionEndAsOnlyZoneActivation ).isSameAs( OFF );
    assertThat( hallOnOffWhenDelayedSecondTime ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerActivation ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithDelayedLampIsOnlyAffected() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( HALL ) ) );

    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithMultiLampDelayesOnDifferentZones() {
    stubZoneActivationProvider( DINING_AREA, HALL );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffWhenDelayed = findLamp( DeskUplight ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( HALL, LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();
    OnOff desktopOnOffOnOnlyZoneActivation = findLamp( DeskUplight ).getOnOffStatus();

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( deskOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( deskOnOffAfterTimerRun ).isSameAs( OFF );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( deskOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffOnOnlyZoneActivation ).isSameAs( OFF );
    assertThat( desktopOnOffOnOnlyZoneActivation ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithMultiLampDelayesOnDifferentZonesThatFinallyActivatesZoneWithoutDelayedLamp() {
    stubZoneActivationProvider( LIVING_AREA );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff deskOnOffAtBeginning = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( LIVING_AREA, HALL );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStart = findLamp( DeskUplight ).getOnOffStatus();
    Runnable command = captureTimerCommand();

    stubZoneActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffOnTransit = findLamp( DeskUplight ).getOnOffStatus();

    command.run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();
    OnOff desktopOnOffOnOnlyZoneActivation = findLamp( DeskUplight ).getOnOffStatus();

    assertThat( deskOnOffAtBeginning ).isSameAs( ON );
    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( deskOnOffAfterTransitionStart ).isSameAs( ON );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( deskOnOffOnTransit ).isSameAs( ON );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( deskOnOffAfterTimerRun ).isSameAs( ON );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( deskOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffOnOnlyZoneActivation ).isSameAs( OFF );
    assertThat( desktopOnOffOnOnlyZoneActivation ).isSameAs( OFF );
  }

  @Test
  public void operateOnZoneEngagementWithLampDelayTimerAndDelayedExplicitlySwitchedOn() {
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.setLampsToSwitchOn( HallCeiling );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnNameSceneSelectionEvent() {
    stubZoneActivationProvider( LIVING_AREA );

    operation.reset();
    operation.executeOn( new StatusEvent( mock( NamedSceneProvider.class ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnUnrelatedStatusEvent() {
    stubZoneActivationProvider( LIVING_AREA );

    operation.reset();
    operation.executeOn( new StatusEvent( new MyStatusProvider() ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test( expected = IllegalArgumentException.class )
  public void executeOnWithNullAsArgument() {
    operation.executeOn( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampFilterWithNullAsArgument() {
    operation.setLampFilter( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampSelectionStrategyWithNullArgument() {
    operation.setLampSelectionStrategy( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setDelayedWithNullAsArgument() {
    operation.setDelayed( ( LampDefinition[] )null );
  }
  @Test( expected = IllegalArgumentException.class )
  public void setDelayedWithNullAsElementOfArgumentArray() {
    operation.setDelayed( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampsToSwitchOnWithNullAsArgument() {
    operation.setLampsToSwitchOn( ( LampDefinition[] )null );
  }
  @Test( expected = IllegalArgumentException.class )
  public void setLampsToSwitchOnWithNullAsElementOfArgumentArray() {
    operation.setLampsToSwitchOn( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampsToSwitchOffWithNullAsArgument() {
    operation.setLampsToSwitchOff( ( LampDefinition[] )null );
  }
  @Test( expected = IllegalArgumentException.class )
  public void setLampsToSwitchOffWithNullAsElementOfArgumentArray() {
    operation.setLampsToSwitchOff( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new LampSwitchOperation( null, zoneActivationProvider, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneActivationProviderArgument() {
    new LampSwitchOperation( registry, null, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsFollowUpTimerArgument() {
    new LampSwitchOperation( registry, zoneActivationProvider, null );
  }

  private Runnable captureTimerCommand() {
    return captureTimerCommand( 1 );
  }

  private Runnable captureTimerCommand( int calls ) {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( timer, times( calls ) ).schedule( eq( LAMP_DELAY_TIME ), eq( LAMP_DELAY_TIMEUNIT ), captor.capture() );
    return captor.getValue();
  }

  private Lamp findLamp( LampDefinition definition ) {
    return registry.findByDefinition( definition );
  }

  private ZoneActivationProvider stubZoneActivationProvider( SectionDefinition... zoneDefinitions ) {
    Set<ZoneActivation> status = stubStatus( zoneDefinitions );
    when( zoneActivationProvider.getStatus() ).thenReturn( status );
    return zoneActivationProvider;
  }

  private Set<ZoneActivation> stubStatus( SectionDefinition... zoneDefinitions ) {
    return Stream.of( zoneDefinitions )
      .map( zoneDefinition -> registry.findByDefinition( zoneDefinition ) )
      .map( zone -> stubZoneActivation( zone ) )
      .collect( toSet() );
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