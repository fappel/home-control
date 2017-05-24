package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.ZONE_ACTIVATION;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.stubZone;
import static com.codeaffine.home.control.status.type.OnOff.*;
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
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.status.type.OnOff;

public class LampSwitchOperationTest {

  private ActivationSupplier activationSupplier;
  private LampSwitchOperation operation;
  private LampCollector lampCollector;
  private EntityRegistry registry;
  private FollowUpTimer timer;

  @Before
  public void setUp() {
    registry = stubRegistry();
    timer = mock( FollowUpTimer.class );
    activationSupplier = mock( ActivationSupplier.class );
    lampCollector = new LampCollector( registry, activationSupplier );
    operation = new LampSwitchOperation( lampCollector, activationSupplier, timer );
  }

  @Test
  public void operateOnSingleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnSingleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> lamp.getDefinition() == DeskUplight );
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagement() {
    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilter() {
    operation.reset();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilterAndFilterableLampAdditions() {
    operation.reset();
    operation.setLampSelectionStrategy( ZONE_ACTIVATION );
    operation.setLampFilter( lamp -> asList( KitchenCeiling, DeskUplight ).contains( lamp.getDefinition() ) );
    operation.addFilterableLamps( DeskUplight );
    operation.executeOn( new StatusEvent( stubActivationSupplier( DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnMultipleZoneEngagementWithLampFilterAndExplicitOnOffSettings() {
    operation.reset();
    operation.setLampFilter( lamp -> asList( WindowUplight, KitchenCeiling ).contains( lamp.getDefinition() ) );
    operation.addLampsToSwitchOff( KitchenCeiling );
    operation.addLampsToSwitchOn( DeskUplight );
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyALL() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.ALL );
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

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
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnLampSelectionStrategyNONE() {
    operation.reset();
    operation.setLampSelectionStrategy( LampSelectionStrategy.NONE );
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

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
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA ) ) );

    operation.reset();
    operation.executeOn( new StatusEvent( stubActivationSupplier( LIVING_AREA, DINING_AREA ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateOnZoneEngagementWithLampDelayTimer() {
    stubActivationSupplier( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationSupplier ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementIfDelayedLampIsSwitchedOn() {
    stubActivationSupplier( LIVING_AREA, HALL );

    operation.reset();
    operation.setDelayed();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    stubActivationSupplier( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( ON );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionThatFinallyActivatesDelayedLamp() {
    stubActivationSupplier( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( HALL );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffOnOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( hallOnOffOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTimerRunOnTransit ).isSameAs( OFF );
    assertThat( hallOnOffAfterTransitionStartVanished ).isSameAs( OFF );
    assertThat( hallOnOffOnOnlyZoneActivation ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithTransitionWithSubsequentActivation() {
    stubActivationSupplier( LIVING_AREA, HALL );

    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWithTransitionEndAsOnlyZoneActivation = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( HALL, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayedSecondTime = findLamp( HallCeiling ).getOnOffStatus();

    captureTimerCommand( 2 ).run();
    operation.executeOn( new StatusEvent( activationSupplier ) );
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
    operation.executeOn( new StatusEvent( stubActivationSupplier( HALL ) ) );

    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnZoneEngagementWithMultiLampDelayesOnDifferentZones() {
    stubActivationSupplier( DINING_AREA, HALL );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();

    stubActivationSupplier( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffWhenDelayed = findLamp( DeskUplight ).getOnOffStatus();

    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationSupplier( HALL, LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationSupplier( LIVING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
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
    stubActivationSupplier( LIVING_AREA );

    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff deskOnOffAtBeginning = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationSupplier( LIVING_AREA, HALL );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStart = findLamp( DeskUplight ).getOnOffStatus();
    Runnable command = captureTimerCommand();

    stubActivationSupplier( LIVING_AREA, HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffOnTransit = findLamp( DeskUplight ).getOnOffStatus();

    command.run();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTimerRunOnTransit = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTimerRun = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationSupplier( HALL, DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff hallOnOffAfterTransitionStartVanished = findLamp( HallCeiling ).getOnOffStatus();
    OnOff deskOnOffAfterTransitionStartVanished = findLamp( DeskUplight ).getOnOffStatus();

    stubActivationSupplier( DINING_AREA );
    operation.reset();
    operation.setDelayed( HallCeiling, DeskUplight );
    operation.executeOn( new StatusEvent( activationSupplier ) );
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
    ActivationSupplier zoneActivationSupplier = stubActivationSupplier( LIVING_AREA, HALL );

    operation.reset();
    operation.addLampsToSwitchOn( HallCeiling );
    operation.executeOn( new StatusEvent( zoneActivationSupplier ) );
    OnOff hallOnOffWhenDelayed = findLamp( HallCeiling ).getOnOffStatus();
    captureTimerCommand().run();
    operation.executeOn( new StatusEvent( zoneActivationSupplier ) );

    assertThat( hallOnOffWhenDelayed ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( ON );
  }

  @Test
  public void operateOnNameSceneSelectionEvent() {
    stubActivationSupplier( LIVING_AREA );

    operation.reset();
    operation.executeOn( new StatusEvent( mock( NamedSceneSupplier.class ) ) );

    assertThat( findLamp( DeskUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( WindowUplight ).getOnOffStatus() ).isSameAs( ON );
    assertThat( findLamp( KitchenCeiling ).getOnOffStatus() ).isSameAs( OFF );
    assertThat( findLamp( HallCeiling ).getOnOffStatus() ).isSameAs( OFF );
  }

  @Test
  public void operateWithLampTimeoutModusSwitchedOn() {
    operation.setLampTimeoutModus( LampTimeoutModus.ON );

    stubActivationSupplier( LIVING_AREA, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    stubActivationSupplier( LIVING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff kitchenCeiling = findLamp( KitchenCeiling ).getOnOffStatus();

    assertThat( kitchenCeiling ).isSameAs( ON );
  }

  @Test
  public void operateWithLampTimeoutModusSwitchedOnAndGroupOfRelatedSectionsConfigured() {
    operation.setLampTimeoutModus( LampTimeoutModus.ON );
    stubActivationSupplier( LIVING_AREA, DINING_AREA );
    operation.reset();
    operation.executeOn( new StatusEvent( activationSupplier ) );
    stubActivationSupplier( LIVING_AREA );
    operation.reset();

    operation.addGroupOfRelatedSections( LIVING_AREA, DINING_AREA );
    operation.executeOn( new StatusEvent( activationSupplier ) );
    OnOff kitchenCeiling = findLamp( KitchenCeiling ).getOnOffStatus();

    assertThat( kitchenCeiling ).isSameAs( OFF );
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
  public void addLampsToSwitchOnWithNullAsArgument() {
    operation.addLampsToSwitchOn( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addLampsToSwitchOnWithNullAsElementOfArgumentArray() {
    operation.addLampsToSwitchOn( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addLampsToSwitchOffWithNullAsArgumentArray() {
    operation.addLampsToSwitchOff( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addLampsToSwitchOffWithNullAsElementOfArgumentArray() {
    operation.addLampsToSwitchOff( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addFilterableLampsWithNullAsArgument() {
    operation.addFilterableLamps( ( LampDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addFilterableLampsWithNullAsElementOfArgumentArray() {
    operation.addFilterableLamps( new LampDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setLampTimeoutModusWithNullAsTimeoutModusArgument() {
    operation.setLampTimeoutModus( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addGroupOfRelatedSectionsWithNullArgumentArray() {
    operation.addGroupOfRelatedSections( ( SectionDefinition[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void addGroupOfRelatedSectionsWithNullAsElementOfArgumentArray() {
    operation.addGroupOfRelatedSections( new SectionDefinition[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLampCollectorArgument() {
    new LampSwitchOperation( null, activationSupplier, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneActivationSupplierArgument() {
    new LampSwitchOperation( lampCollector, null, timer );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsFollowUpTimerArgument() {
    new LampSwitchOperation( lampCollector, activationSupplier, null );
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
    EntityRegistry result = RegistryHelper.stubRegistry( rooms, lamps, emptySet(), emptySet() );
    equipWithLamp( result, DINING_AREA, KitchenCeiling );
    equipWithLamp( result, HALL, HallCeiling );
    equipWithLamp( result, LIVING_AREA, DeskUplight, WindowUplight );
    return result;
  }
}