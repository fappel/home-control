package com.codeaffine.home.control.application.operation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusProvider;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class HomeControlOperationsTest {

  private static final StatusEvent EVENT = new StatusEvent( new MyStatusProvider() );

  static class SomeStatusProvider implements StatusProvider<String> {

    @Override
    public String getStatus() {
      return null;
    }
  }

  @Test
  public void isRelated() {
    boolean actual = HomeControlOperations.isRelated( EVENT, MyStatusProvider.class, SomeStatusProvider.class );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnUnrelatedProviderType() {
    boolean actual = HomeControlOperations.isRelated( EVENT, SomeStatusProvider.class );

    assertThat( actual ).isFalse();
  }

  @Test( expected = IllegalArgumentException.class )
  public void isRelatedWithNullAsEventArgument() {
    HomeControlOperations.isRelated( null, MyStatusProvider.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isRelatedWithNullAsStatusProviderTypesArgumentArray() {
    HomeControlOperations.isRelated( EVENT, ( Class<? extends StatusProvider<?>>[] )null );
  }

  @SuppressWarnings("unchecked")
  @Test( expected = IllegalArgumentException.class )
  public void isRelatedWithNullAsStatusProviderTypesArgumentArrayElement() {
    HomeControlOperations.isRelated( EVENT, ( Class<? extends StatusProvider<?>>[] )new Class<?>[ 1 ] );
  }
}