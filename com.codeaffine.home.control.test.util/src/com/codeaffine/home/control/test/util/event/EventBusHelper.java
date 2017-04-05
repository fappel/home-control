package com.codeaffine.home.control.test.util.event;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusSupplier;

public class EventBusHelper {

  public static <T extends StatusSupplier<?>> Optional<T> captureEvent( EventBus eventBus, Class<T> type ) {
    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    verify( eventBus ).post( captor.capture() );
    return captor.getValue().getSource( type );
  }
}