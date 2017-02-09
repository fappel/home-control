package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.home.control.internal.wiring.Messages.ERROR_SCHEDULE_METHOD_WITH_ARGUMENT;
import static com.codeaffine.util.ArgumentVerification.verifyCondition;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class Timer {

  private final Set<ScheduledFuture<?>> schedules;
  private final SystemExecutor executor;

  public Timer( SystemExecutor executor ) {
    this.schedules = new HashSet<>();
    this.executor = executor;
  }

  public void schedule( Object bean ) {
    schedules.addAll( scheduleCommands( bean ) );
  }

  private Collection<? extends ScheduledFuture<?>> scheduleCommands( Object bean ) {
    return Stream.of( bean.getClass().getDeclaredMethods() )
      .filter( method -> method.getAnnotation( Schedule.class ) != null )
      .map( method  -> scheduleCommand( bean, method ) )
      .collect( toSet() );
  }

  private ScheduledFuture<?> scheduleCommand( Object bean, Method method ) {
    verifyMethodIsParameterLess( bean, method );

    Schedule schedule = method.getAnnotation( Schedule.class );
    long initialDelay = schedule.initialDelay();
    long period = schedule.period();
    TimeUnit timeUnit = schedule.timeUnit();
    method.setAccessible( true );
    return executor.scheduleAtFixedRate( createCommand( bean, method ), initialDelay, period, timeUnit );
  }

  public void reset() {
    schedules.forEach( schedule -> schedule.cancel( true ) );
  }

  private static Runnable createCommand( Object bean, Method method ) {
    return () -> {
      try {
        method.invoke( bean );
      } catch( InvocationTargetException e ) {
        Throwable targetException = e.getTargetException();
        if( targetException instanceof RuntimeException ) {
          throw ( RuntimeException )targetException;
        }
        throw new IllegalStateException( targetException );
      } catch( IllegalAccessException | IllegalArgumentException shouldNotHappen ) {
        throw new IllegalStateException( shouldNotHappen );
      }
    };
  }

  private static void verifyMethodIsParameterLess( Object bean, Method method ) {
    String beanName = bean.getClass().getName();
    String methodName = method.getName();
    verifyCondition( method.getParameterCount() == 0, ERROR_SCHEDULE_METHOD_WITH_ARGUMENT, beanName, methodName );
  }
}