package com.codeaffine.home.control.engine.component.event;

import static com.codeaffine.home.control.engine.component.event.Messages.*;
import static com.codeaffine.home.control.util.reflection.ReflectionUtil.invoke;
import static com.codeaffine.util.ArgumentVerification.verifyCondition;
import static java.lang.String.format;

import java.lang.reflect.Method;

import com.codeaffine.home.control.engine.component.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.logger.Logger;
import com.google.common.eventbus.Subscribe;

class ObserverAdapter {

  private final Object observer;
  private final Method method;
  private final Logger logger;

  public ObserverAdapter( Method method, Object observer ) {
    this( method, observer, new LoggerFactoryAdapter().getLogger( ObserverAdapter.class ) );
  }

  public ObserverAdapter( Method method, Object observer, Logger logger ) {
    verifyCondition( method.getParameterCount() == 1,
                     ERROR_INVALID_PARAMETER_DECLARATION,
                     method.getName(),
                     method.getDeclaringClass().getName() );

    this.observer = observer;
    this.method = method;
    this.logger = logger;
  }

  @Subscribe public void handle( EventAdapter eventAdapter ) {
    if( isMatchingEventType( eventAdapter ) ) {
      try {
        invoke( method, observer, eventAdapter.getEventObject() );
      } catch( RuntimeException rte ) {
        logger.error( formatErrorMessage(), rte );
      }
    }
  }

  private boolean isMatchingEventType( EventAdapter eventAdapter ) {
    return method.getParameterTypes()[ 0 ].isInstance( eventAdapter.getEventObject() );
  }

  private String formatErrorMessage() {
    return format( ERROR_DURING_EVENT_HANDLING_OF_OBSERVER, method.getDeclaringClass(), method.getName() );
  }
}