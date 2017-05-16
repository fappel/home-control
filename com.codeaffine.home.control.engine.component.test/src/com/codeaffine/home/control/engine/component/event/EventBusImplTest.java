package com.codeaffine.home.control.engine.component.event;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.component.event.EventBusImpl;
import com.codeaffine.home.control.event.Subscribe;

public class EventBusImplTest {

  private EventBusImpl eventBus;

  static class Event {}
  static class OtherEvent {}

  static class EventObserver {

    Event event;

    @Subscribe public void handle( Event event ) {
      this.event = event;
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
    eventBus = new EventBusImpl();
  }

  @Test
  public void process() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.post( expected );

    assertThat( observer.event ).isSameAs( expected );
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

    eventBus.unregister( observer );
    eventBus.post( expected );

    assertThat( observer.event ).isNull();
  }

  @Test
  public void unregisterTwice() {
    EventObserver observer = new EventObserver();
    Event expected = new Event();
    eventBus.register( observer );

    eventBus.unregister( observer );
    eventBus.unregister( observer );
    eventBus.post( expected );

    assertThat( observer.event ).isNull();
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

  @Test( expected = IllegalArgumentException.class )
  public void postWithNullArgument() {
    eventBus.post( null );
  }
}