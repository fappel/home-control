package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.home.control.internal.util.ReflectionUtil.invoke;
import static com.codeaffine.home.control.internal.wiring.Messages.ERROR_SCHEDULE_METHOD_WITH_ARGUMENT;
import static com.codeaffine.util.ArgumentVerification.verifyCondition;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.internal.util.SystemExecutor;

public class TimerWiring {

  private final Set<ScheduledFuture<?>> schedules;
  private final SystemExecutor executor;

  public TimerWiring( SystemExecutor executor ) {
    this.schedules = new HashSet<>();
    this.executor = executor;
  }

  public void schedule( Object managedObject ) {
    schedules.addAll( scheduleCommands( managedObject ) );
  }

  private Collection<? extends ScheduledFuture<?>> scheduleCommands( Object managedObject ) {
    return Stream.of( managedObject.getClass().getDeclaredMethods() )
      .filter( method -> method.getAnnotation( Schedule.class ) != null )
      .map( method  -> scheduleCommand( managedObject, method ) )
      .collect( toSet() );
  }

  private ScheduledFuture<?> scheduleCommand( Object managedObject, Method method ) {
    verifyMethodIsParameterLess( managedObject, method );

    Schedule schedule = method.getAnnotation( Schedule.class );
    long initialDelay = schedule.initialDelay();
    long period = schedule.period();
    TimeUnit timeUnit = schedule.timeUnit();
    method.setAccessible( true );
    return executor.scheduleAtFixedRate( createCommand( method, managedObject ), initialDelay, period, timeUnit );
  }

  public void reset() {
    schedules.forEach( schedule -> schedule.cancel( true ) );
  }

  private static Runnable createCommand( Method method, Object managedObject ) {
    return () -> invoke( method, managedObject );
  }


  private static void verifyMethodIsParameterLess( Object managedObject, Method method ) {
    String className = managedObject.getClass().getName();
    String methodName = method.getName();
    verifyCondition( method.getParameterCount() == 0, ERROR_SCHEDULE_METHOD_WITH_ARGUMENT, className, methodName );
  }
}