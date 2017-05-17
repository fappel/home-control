package com.codeaffine.home.control.engine.component.event;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.event.Subscribe;

public class EventBusImplTest {

  private TypeUnloadTracker typeUnloadTracker;
  private EventBusImpl eventBus;

  static class Event {}
  static class OtherEvent {}

  static class EventObserver {

    Event event;
    int eventCount;

    @Subscribe public void handle( Event event ) {
      this.event = event;
      this.eventCount++;
    }
  }

  static class EventObserverWithMultipleObserverMethods {

    Event event1;
    Event event2;

    @Subscribe public void handle1( Event event ) {
      this.event1 = event;
    }

    @Subscribe public void handle2( Event event ) {
      this.event2 = event;
    }
  }

  static class MultiEventObserver {

    OtherEvent otherEvent;
    Event event;

    @Subscribe public void handleEvent( Event event ) {
      this.event = event;
    }

    @Subscribe public void handleOtherEvent( OtherEvent otherEvent ) {
      this.otherEvent = otherEvent;
    }
  }

  static class EventObserverWithWrongSignature {
    @Subscribe public void handleEvent( @SuppressWarnings("unused") Event event,
                                        @SuppressWarnings("unused") Runnable illegalArgument ) {
    }
  }

  @Before
  public void setUp() {
    typeUnloadTracker = mock( TypeUnloadTracker.class );
    eventBus = new EventBusImpl( typeUnloadTracker );
  }

  @Test
  public void process() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.post( expected );

    assertThat( observer.event ).isSameAs( expected );
    assertThat( observer.eventCount ).isEqualTo( 1 );
  }

  @Test
  public void processWithRedundantObserverRegistration() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.register( observer );
    eventBus.post( expected );

    assertThat( observer.event ).isSameAs( expected );
    assertThat( observer.eventCount ).isEqualTo( 1 );
  }

  @Test
  public void processObserverWithMultipleHandlerMethods() {
    EventObserverWithMultipleObserverMethods observer = new EventObserverWithMultipleObserverMethods();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.post( expected );

    assertThat( observer.event1 )
      .isSameAs( observer.event2 )
      .isSameAs( expected );
  }

  @Test
  public void processMultiEventObserver() {
    MultiEventObserver observer = new MultiEventObserver();
    OtherEvent expected = new OtherEvent();
    eventBus.register( observer );

    eventBus.post( expected );

    assertThat( observer.otherEvent ).isSameAs( expected );
    assertThat( observer.event ).isNull();
  }

  @Test
  public void processEventObserverWithWrongSignature() {
    Throwable actual = thrownBy( () -> eventBus.register( new EventObserverWithWrongSignature() ) );

    assertThat( actual )
      .hasMessageContaining( EventObserverWithWrongSignature.class.getDeclaredMethods()[ 0 ].getName() )
      .hasMessageContaining( EventObserverWithWrongSignature.class.getName() )
      .isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullArgument() {
    eventBus.register( null );
  }

  @Test
  public void unregister() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );
    Runnable hook = captureTypeUnloadHook();

    eventBus.unregister( observer );
    eventBus.post( expected );

    assertThat( observer.event ).isNull();
    verify( typeUnloadTracker ).unregisterUnloadHook( EventObserver.class, hook );
  }

  @Test
  public void unregisterTwice() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );
    Runnable hook = captureTypeUnloadHook();

    eventBus.unregister( observer );
    eventBus.unregister( observer );
    eventBus.post( expected );

    assertThat( observer.event ).isNull();
    verify( typeUnloadTracker ).unregisterUnloadHook( EventObserver.class, hook );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterWithNullArgument() {
    eventBus.unregister( null );
  }

  @Test
  public void dispose() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.dispose();
    eventBus.post( expected );

    assertThat( observer.event ).isNull();
  }

  @Test
  public void processWithNonEventObserver() {
    Throwable actual = thrownBy( () -> eventBus.register( new Object() ) );

    assertThat( actual ).isNull();
  }

  @Test
  public void deactivationOfEventObserverClassProvidingBundle() {
    EventObserver observer = new EventObserver();
    eventBus.register( observer );

    captureTypeUnloadHook().run();
    eventBus.post( new Event() );

    assertThat( observer.event ).isNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void postWithNullArgument() {
    eventBus.post( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTypeUnloadTracker() {
    new EventBusImpl( null );
  }

  private Runnable captureTypeUnloadHook() {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( typeUnloadTracker ).registerUnloadHook( eq( EventObserver.class ), captor.capture() );
    return captor.getValue();
  }
}