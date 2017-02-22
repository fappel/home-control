package com.codeaffine.home.control.internal.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;

public class ZoneEventAssert extends AbstractAssert<ZoneEventAssert, ZoneEvent> {

  public ZoneEventAssert( ZoneEvent actual ) {
    super( actual, ZoneEventAssert.class );
  }

  public static ZoneEventAssert assertThat( ZoneEvent actual ) {
    return new ZoneEventAssert( actual );
  }

  public ZoneEventAssert hasSensor( Entity<EntityDefinition<?>> expected ) {
    isNotNull();
    if( actual.getSensor() != expected ) {
      failWithMessage( "Expected sensor <%s> but was <%s>",
                       expected.getDefinition(),
                       actual.getSensor().getDefinition() );
    }
    return this;
  }

  public ZoneEventAssert hasNoEngagedZones() {
    isNotNull();
    if( !actual.getEngagedZones().isEmpty() ) {
      failWithMessage( "Expected engaged zones to be empty but was <%s>", actual.getEngagedZones() );
    }
    return this;
  }

  @SafeVarargs
  public final ZoneEventAssert hasEngagedZones( Entity<EntityDefinition<?>> ... expected ) {
    isNotNull();
    String pattern = "Expected engaged zones to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getEngagedZones(), pattern );
    return this;
  }

  public ZoneEventAssert hasNoAdditions() {
    isNotNull();
    if( !actual.getAdditions().isEmpty() ) {
      failWithMessage( "Expected additions to be empty but was <%s>", actual.getAdditions() );
    }
    return this;
  }

  @SafeVarargs
  public final ZoneEventAssert hasAdditions( Entity<EntityDefinition<?>> ... expected ) {
    isNotNull();
    String pattern = "Expected additions to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getAdditions(), pattern );
    return this;
  }

  public ZoneEventAssert hasNoRemovals() {
    isNotNull();
    if( !actual.getRemovals().isEmpty() ) {
      failWithMessage( "Expected removals to be empty but was <%s>", actual.getRemovals() );
    }
    return this;
  }

  @SafeVarargs
  public final ZoneEventAssert hasRemovals( Entity<EntityDefinition<?>> ... expected ) {
    isNotNull();
    String pattern = "Expected removals to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getRemovals(), pattern );
    return this;
  }

  private void verifyContainment( List<Entity<EntityDefinition<?>>> expected,
                                  Collection<Entity<EntityDefinition<?>>> actual,
                                  String pattern ) {
    if( actual.size() != expected.size() ) {
      failWithMessage( pattern, expected, actual );
    }
    if( !actual.containsAll( expected ) ) {
      failWithMessage( pattern, expected, actual );
    }
  }
}