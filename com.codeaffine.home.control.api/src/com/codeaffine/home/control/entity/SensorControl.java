package com.codeaffine.home.control.entity;

public interface SensorControl extends Sensor {

  public interface SensorControlFactory {
    SensorControl create( Sensor sensor );
  }

  <S> void notifyAboutSensorStatusChange( S sensorStatus );
}