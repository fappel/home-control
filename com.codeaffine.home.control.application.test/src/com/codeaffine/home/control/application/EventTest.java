package com.codeaffine.home.control.application;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.junit.Test;

public class EventTest {

  @Test
  public void getSource() {
    Runnable expected = mock( Runnable.class );
    Event event = new Event( expected );

    Optional<Runnable> actual = event.getSource( Runnable.class );

    assertThat( actual ).hasValue( expected );
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void getSourceIfTypeDoesNotMatch() {
    Event event = new Event( mock( Runnable.class ) );

    Optional<Callable> actual = event.getSource( Callable.class );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getSourceWithNullAsTypeArgument() {
    Event event = new Event( mock( Runnable.class ) );

    Throwable actual = thrownBy( () -> event.getSource( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSourceArgument() {
    new Event( null );
  }
}