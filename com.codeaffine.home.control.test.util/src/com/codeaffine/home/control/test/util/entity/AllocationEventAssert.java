package com.codeaffine.home.control.test.util.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class AllocationEventAssert extends AbstractAssert<AllocationEventAssert, AllocationEvent> {

  public AllocationEventAssert( AllocationEvent actual ) {
    super( actual, AllocationEventAssert.class );
  }

  public static AllocationEventAssert assertThat( AllocationEvent actual ) {
    return new AllocationEventAssert( actual );
  }

  public AllocationEventAssert hasSensor( Entity<EntityDefinition<?>> expected ) {
    isNotNull();
    if( actual.getSensor() != expected ) {
      failWithMessage( "Expected sensor <%s> but was <%s>",
                       expected.getDefinition(),
                       actual.getSensor().getDefinition() );
    }
    return this;
  }

  public AllocationEventAssert hasNoAllocated() {
    isNotNull();
    if( !actual.getAllocated().isEmpty() ) {
      failWithMessage( "Expected engaged zones to be empty but was <%s>", actual.getAllocated() );
    }
    return this;
  }

  @SafeVarargs
  public final AllocationEventAssert hasAllocated( Entity<EntityDefinition<?>> ... expected ) {
    isNotNull();
    String pattern = "Expected engaged zones to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getAllocated(), pattern );
    return this;
  }

  public AllocationEventAssert hasNoAdditions() {
    isNotNull();
    if( !actual.getAdditions().isEmpty() ) {
      failWithMessage( "Expected additions to be empty but was <%s>", actual.getAdditions() );
    }
    return this;
  }

  @SafeVarargs
  public final AllocationEventAssert hasAdditions( Entity<EntityDefinition<?>> ... expected ) {
    isNotNull();
    String pattern = "Expected additions to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getAdditions(), pattern );
    return this;
  }

  public AllocationEventAssert hasNoRemovals() {
    isNotNull();
    if( !actual.getRemovals().isEmpty() ) {
      failWithMessage( "Expected removals to be empty but was <%s>", actual.getRemovals() );
    }
    return this;
  }

  @SafeVarargs
  public final AllocationEventAssert hasRemovals( Entity<EntityDefinition<?>> ... expected ) {
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