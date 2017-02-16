package com.codeaffine.home.control.internal.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationEventAssert extends AbstractAssert<AllocationEventAssert, AllocationEvent> {

  public AllocationEventAssert( AllocationEvent actual ) {
    super( actual, AllocationEventAssert.class );
  }

  public static AllocationEventAssert assertThat( AllocationEvent actual ) {
    return new AllocationEventAssert( actual );
  }

  public AllocationEventAssert hasActor( Entity<?> expected ) {
    isNotNull();
    if( actual.getActor() != expected ) {
      failWithMessage( "Expected actor <%s> but was <%s>", expected.getDefinition(), actual.getActor().getDefinition());
    }
    return this;
  }

  public AllocationEventAssert hasNoAllocations() {
    isNotNull();
    if( !actual.getAllocations().isEmpty() ) {
      failWithMessage( "Expected allocations to be empty but was <%s>", actual.getAllocations() );
    }
    return this;
  }

  public AllocationEventAssert hasAllocations( Entity<?> ... expected ) {
    isNotNull();
    String pattern = "Expected allocations to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getAllocations(), pattern );
    return this;
  }

  public AllocationEventAssert hasNoAdditions() {
    isNotNull();
    if( !actual.getAdditions().isEmpty() ) {
      failWithMessage( "Expected additions to be empty but was <%s>", actual.getAdditions() );
    }
    return this;
  }

  public AllocationEventAssert hasAdditions( Entity<?> ... expected ) {
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

  public AllocationEventAssert hasRemovals( Entity<?> ... expected ) {
    isNotNull();
    String pattern = "Expected removals to be <%s> but they are <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getRemovals(), pattern );
    return this;
  }

  private void verifyContainment( List<Entity<?>> expected, Collection<Entity<?>> actual, String pattern ) {
    if( actual.size() != expected.size() ) {
      failWithMessage( pattern, expected, actual );
    }
    if( !actual.containsAll( expected ) ) {
      failWithMessage( pattern, expected, actual );
    }
  }
}