package com.codeaffine.home.control.engine.component.event;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.engine.component.event.EventAdapter;
import com.codeaffine.home.control.engine.component.event.ObserverAdapter;
import com.codeaffine.home.control.logger.Logger;

public class ObserverAdapterTest {

  private Logger logger;

  static class Event {}

  static class Handler {

    Event event;

    void handle( Event event ) {
      this.event = event;
    }
  }

  static class HandlerWithWrongSignature {

    void handle( @SuppressWarnings("unused") Event event, @SuppressWarnings("unused") Runnable oddParameter ) {}
  }

  static class HandlerWithProblem {

    private RuntimeException problem;

    void handle( @SuppressWarnings("unused") Event event ) {
      problem = new RuntimeException( "problem-message" );
      throw problem;
    }
  }

  @Before
  public void setUp() {
    logger = mock( Logger.class );
  }

  @Test
  public void handle() {
    Method method = Handler.class.getDeclaredMethods()[ 0 ];
    Handler handler = new Handler();
    Event expected = new Event();
    ObserverAdapter adapter = new ObserverAdapter( method, handler, logger );

    adapter.handle( new EventAdapter( expected ) );

    assertThat( handler.event ).isSameAs( expected );
  }

  @Test
  public void handleWithNonMatchingEventType() {
    Method method = Handler.class.getDeclaredMethods()[ 0 ];
    Handler handler = new Handler();
    ObserverAdapter adapter = new ObserverAdapter( method, handler, logger );

    adapter.handle( new EventAdapter( new Object() ) );

    assertThat( handler.event ).isNull();
  }

  @Test
  public void handleWithWrongSignature() {
    Method method = HandlerWithWrongSignature.class.getDeclaredMethods()[ 0 ];
    HandlerWithWrongSignature handler = new HandlerWithWrongSignature();

    Throwable actual = thrownBy( () -> new ObserverAdapter( method, handler, logger ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void handleWithProblem() {
    Method method = HandlerWithProblem.class.getDeclaredMethods()[ 0 ];
    HandlerWithProblem handler = new HandlerWithProblem();
    ObserverAdapter adapter = new ObserverAdapter( method, handler, logger );

    adapter.handle( new EventAdapter( new Event() ) );

    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).error( captor.capture(), eq( handler.problem ) );
    assertThat( captor.getValue() ).contains( HandlerWithProblem.class.getName(), method.getName() );
  }
}
