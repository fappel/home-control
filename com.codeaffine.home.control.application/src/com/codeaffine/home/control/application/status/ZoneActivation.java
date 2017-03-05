package com.codeaffine.home.control.application.status;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface ZoneActivation {
  Entity<EntityDefinition<?>> getZone();
  Optional<LocalDateTime> getReleaseTime();
}