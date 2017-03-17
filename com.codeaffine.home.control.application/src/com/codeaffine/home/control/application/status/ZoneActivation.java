package com.codeaffine.home.control.application.status;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface ZoneActivation {
  Entity<?> getZone();
  Optional<LocalDateTime> getReleaseTime();
  boolean isAdjacentActivated();
}