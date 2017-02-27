package com.codeaffine.home.control.application.test;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.Event;
import com.codeaffine.home.control.event.EventBus;

public class EventBusHelper {
  
  public static <T> Optional<T> captureEvent( EventBus eventBus, Class<T> type ) {
    ArgumentCaptor<Event> captor = forClass( Event.class );
    verify( eventBus ).post( captor.capture() );
    return captor.getValue().getSource( type );
  }
}