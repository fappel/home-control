package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class TimerWiringTest {

  private static final String PROBLEM_MESSAGE = "Problem-Message";
  private static final long INITIAL_DELAY = 0L;
  private static final long PERIOD = 10L;

  private SystemExecutor executor;
  private TimerWiring timer;

  private static class Bean {

    private boolean invoked;

    @Schedule(period = PERIOD)
    private void scheduledInvocation() {
      invoked = true;
    }

    boolean isInvoked() {
      return invoked;
    }
  }

  private static class BeanWithStaticMethodSchedule {

    private static boolean invoked;

    @Schedule(period = PERIOD)
    private static void scheduledInvocation() {
      invoked = true;
    }

    static boolean isInvoked() {
      return invoked;
    }

    static void setInvoked( boolean invoked ) {
      BeanWithStaticMethodSchedule.invoked = invoked;
    }
  }

  private static class BeanWithInvalidParamsOnScheduleDeclaration {

    @Schedule(period = PERIOD)
    private void invalidScheduleMethod(
      @SuppressWarnings("unused") boolean paramNotAllowed ) {}
  }

  private static class BeanWithRuntimeProblemOnScheduledCall {

    @SuppressWarnings("static-method")
    @Schedule(period = PERIOD)
    private void scheduledInvocationWithRuntimeProblem() {
      throw new RuntimeException( PROBLEM_MESSAGE );
    }
  }

  private static class BeanWithCheckedExceptionOnScheduledCall {

    @SuppressWarnings("static-method")
    @Schedule(period = PERIOD)
    private void scheduledInvocationWithRuntimeProblem() throws Exception {
      throw new Exception( PROBLEM_MESSAGE );
    }
  }

  @Before
  public void setUp() {
    executor = mock( SystemExecutor.class );
    timer = new TimerWiring( executor );
  }

  @Test
  public void schedule() {
    Bean bean = new Bean();

    timer.schedule( bean );

    verify( executor ).scheduleAtFixedRate( any( Runnable.class ), eq( INITIAL_DELAY ), eq( PERIOD ), eq( SECONDS ) );
  }

  @Test
  public void scheduleMethodWithParam() {
    Object invalid = new BeanWithInvalidParamsOnScheduleDeclaration();

    Throwable actual = thrownBy( () -> timer.schedule( invalid ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessageContaining( BeanWithInvalidParamsOnScheduleDeclaration.class.getName() + ".invalidScheduleMethod" );
  }

  @Test
  public void executeScheduledCommand() {
    Bean bean = new Bean();
    timer.schedule( bean );
    Runnable command = captureScheduledCommand();

    command.run();

    assertThat( bean.isInvoked() ).isTrue();
  }

  @Test
  public void executeScheduleCommandOfStaticMethod() {
    BeanWithStaticMethodSchedule.setInvoked( false );
    BeanWithStaticMethodSchedule bean = new BeanWithStaticMethodSchedule();
    timer.schedule( bean );

    Runnable command = captureScheduledCommand();

    command.run();
    assertThat( BeanWithStaticMethodSchedule.isInvoked() ).isTrue();
  }

  @Test
  public void executeScheduledCommandWithRuntimeProblem() {
    Object bean = new BeanWithRuntimeProblemOnScheduledCall();
    timer.schedule( bean );
    Runnable scheduledCommand = captureScheduledCommand();

    Throwable actual = thrownBy( () -> scheduledCommand.run() );

    assertThat( actual )
      .isInstanceOf( RuntimeException.class )
      .hasMessage( PROBLEM_MESSAGE );
  }

  @Test
  public void executeScheduledCommandWithCheckedException() {
    Object bean = new BeanWithCheckedExceptionOnScheduledCall();
    timer.schedule( bean );
    Runnable scheduledCommand = captureScheduledCommand();

    Throwable actual = thrownBy( () -> scheduledCommand.run() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCauseInstanceOf( Exception.class )
      .hasMessageContaining( PROBLEM_MESSAGE );
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void reset() {
    Bean bean = new Bean();
    ScheduledFuture future = mock( ScheduledFuture.class );
    when( executor.scheduleAtFixedRate( any( Runnable.class ), eq( INITIAL_DELAY ), eq( PERIOD ), eq( SECONDS ) ) )
      .thenReturn( future );
    timer.schedule( bean );

    timer.reset();

    verify( future ).cancel( true );
  }

  private Runnable captureScheduledCommand() {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( executor ).scheduleAtFixedRate( captor.capture(), eq( INITIAL_DELAY ), eq( PERIOD ), eq( SECONDS ) );
    return captor.getValue();
  }
}
