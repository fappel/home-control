package com.codeaffine.home.control.engine.entity;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;

class SensorEventCaptor {

  @SuppressWarnings("rawtypes")
  static SensorEvent<?> captureSensorEvent( EventBus eventBus ) {
    ArgumentCaptor<SensorEvent> captor = forClass( SensorEvent.class );
    verify( eventBus ).post( captor.capture() );
    return captor.getValue();
  }
}