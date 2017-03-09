package com.codeaffine.home.control.entity;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface Sensor {
  void registerAffected( Entity<?> affected );
  void unregisterAffected( Entity<?> affected );
}