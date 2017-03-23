package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.stubZone;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
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
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.StatusEvent;

public class LampSwitchOperationTest {

  private ActivationProvider activationProvider;
  private LampSwitchOperation operation;
  private LampCollector lampCollector;
  private EntityRegistry registry;
  private FollowUpTimer timer;

  @Before
  public void setUp() {
    registry = stubRegistry();
    timer = mock( FollowUpTimer.class );
    activationProvider = mock( ActivationProvider.class );
    lampCollector = new LampCollector( registry, activationProvider );
    operation = new LampSwitchOperation( lampCollector, activationProvider, timer );
  }

  @Test
  public void operateOnSingleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSingleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA, DINING_AREA ) ) );

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
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyALL() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

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
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyNONE() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.NONE );
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

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
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA ) ) );

    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationProvider( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnZoneEngagementWithLampDelayTimer() {
    stubActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementIfDelayedLampIsSwitchedOn() {
    stubActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.setDelayed();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    stubActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionThatFinallyActivatesDelayedLamp() {
    stubActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( HALL );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffOnOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffOnOnlyZoneActivation ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionWithSubsequentActivation() {
    stubActivationProvider( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWithTransitionEndAsOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayedSecondTime = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand( 2 ).run();
    operation.executeOn( new StatusEvent( activationProvider ) );
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
    operation.executeOn( new StatusEvent( stubActivationProvider( HALL ) ) );

    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithMultiLampDelayesOnDifferentZones() {
    stubActivationProvider( DINING_AREA, HALL );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffWhenDelayed = findLamp( DeskUplight ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationProvider( HALL, LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationProvider( LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
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
    stubActivationProvider( LIVING_AREA );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff deskOnOffAtBeginning = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationProvider( LIVING_AREA, HALL );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStart = findLamp( DeskUplight ).getOnOffStatus();
    Runnable command = captureTimerCommand();

    stubActivationProvider( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffOnTransit = findLamp( DeskUplight ).getOnOffStatus();

    command.run();
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationProvider( HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationProvider( DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationProvider ) );
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
    ActivationProvider zoneActivationProvider = stubActivationProvider( LIVING_AREA, HALL );

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
    stubActivationProvider( LIVING_AREA );

    operation.reset();
    operation.executeOn( new StatusEvent( mock( NamedSceneProvider.class ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
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
  public void constructWithNullAsLampCollectorArgument() {
    new LampSwitchOperation( null, activationProvider, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneActivationProviderArgument() {
    new LampSwitchOperation( lampCollector, null, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsFollowUpTimerArgument() {
    new LampSwitchOperation( lampCollector, activationProvider, null );
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