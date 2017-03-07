package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.*;
import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.application.test.ZoneActivationHelper.stubZoneActivation;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newLinkedHashSet;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSelectionStrategy;
import com.codeaffine.home.control.application.room.RoomProvider.Room;
import com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.StatusEvent;

public class LampSwitchOperationTest {

  private LampSwitchOperation operation;
  private EntityRegistry registry;
  private FollowUpTimer timer;

  @Before
  public void setUp() {
    registry = stubRegistry();
    timer = mock( FollowUpTimer.class );
    operation = new LampSwitchOperation( registry, timer );
  }

  @Test
  public void operateOnSingleZoneEngagement() {
    operation.prepare();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSingleZoneEngagementWithLampFilter() {
    operation.prepare();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagement() {
    operation.prepare();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom, Kitchen ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilter() {
    operation.prepare();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom, Kitchen ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyALL() {
    operation.prepare();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnLampSelectionStrategyALLWithLampFilter() {
    operation.prepare();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.setLampFilter( lamp -> lamp.getDefinition() == KitchenCeiling );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyNONE() {
    operation.prepare();
    operation.setLampSelectionStrategy( LampSelectionStrategy.NONE );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSubsequentEvents() {
    operation.prepare();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom ) ) );

    operation.prepare();
    operation.executeOn( new StatusEvent( stubZoneActivationProvider( LivingRoom, Kitchen ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnZoneEngagementWithLampDelayTimer() {
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LivingRoom, Hall );

    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementIfDelayedLampIsSwitchedOn() {
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LivingRoom, Hall );

    operation.prepare();
    operation.setDelayed( emptySet() );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionThatFinallyActivatesDelayedLamp() {
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LivingRoom, Hall );

    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall );
    operation.prepare();
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
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LivingRoom, Hall );

    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Kitchen );
    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWithTransitionEndAsOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall, Kitchen );
    operation.prepare();
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
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( Hall );

    operation.prepare();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );

    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithMultiLampDelayesOnDifferentZones() {
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( Kitchen, Hall );
    LinkedHashSet<Lamp> delayed = newLinkedHashSet( findLamp( HallCeiling ), findLamp( DeskUplight ) );

    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall, Kitchen );
    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffWhenDelayed = findLamp( DeskUplight ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall, LivingRoom );
    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom );
    operation.prepare();
    operation.setDelayed( delayed );
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
    ZoneActivationProvider zoneActivationProvider = stubZoneActivationProvider( LivingRoom );
    LinkedHashSet<Lamp> delayed = newLinkedHashSet( findLamp( HallCeiling ), findLamp( DeskUplight ) );

    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff deskOnOffAtBeginning = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall );
    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStart = findLamp( DeskUplight ).getOnOffStatus();
    Runnable command = captureTimerCommand();

    stubZoneActivationProvider( zoneActivationProvider, LivingRoom, Hall, Kitchen );
    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffOnTransit = findLamp( DeskUplight ).getOnOffStatus();

    command.run();
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Hall, Kitchen );
    operation.prepare();
    operation.setDelayed( delayed );
    operation.executeOn( new StatusEvent( zoneActivationProvider ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubZoneActivationProvider( zoneActivationProvider, Kitchen );
    operation.prepare();
    operation.setDelayed( delayed );
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
    operation.setDelayed( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new LampSwitchOperation( null, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsFollowUpTimerArgument() {
    new LampSwitchOperation( registry, null );
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

  private ZoneActivationProvider stubZoneActivationProvider( RoomDefinition ... zoneDefinitions ) {
    ZoneActivationProvider result = mock( ZoneActivationProvider.class );
    stubZoneActivationProvider( result, zoneDefinitions );
    return result;
  }

  void stubZoneActivationProvider( ZoneActivationProvider provider, RoomDefinition... zoneDefinitions ) {
    Set<ZoneActivation> status = stubStatus( zoneDefinitions );
    when( provider.getStatus() ).thenReturn( status );
  }

  Set<ZoneActivation> stubStatus( RoomDefinition... zoneDefinitions ) {
    return Stream.of( zoneDefinitions )
      .map( zoneDefinition -> registry.findByDefinition( zoneDefinition ) )
      .map( zone -> stubZoneActivation( zone ) )
      .collect( toSet() );
  }

  static EntityRegistry stubRegistry() {
    Set<Lamp> lamps = stubLamps( KitchenCeiling, HallCeiling, DeskUplight, WindowUplight );
    Set<Room> rooms = stubZones( Kitchen, Hall, LivingRoom );
    EntityRegistry result = RegistryHelper.stubRegistry( rooms, lamps );
    equipWithLamp( result, Kitchen, KitchenCeiling );
    equipWithLamp( result, Hall, HallCeiling );
    equipWithLamp( result, LivingRoom, DeskUplight, WindowUplight );
    return result;
  }
}