package com.codeaffine.home.control.application.control;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Test;

import com.codeaffine.home.control.application.test.MyStatusProvider;

public class StatusEventTest {

  @Test
  public void getSource() {
    MyStatusProvider expected = mock( MyStatusProvider.class );
    StatusEvent event = new StatusEvent( expected );

    Optional<MyStatusProvider> actual = event.getSource( MyStatusProvider.class );

    assertThat( actual ).hasValue( expected );
  }

  @Test
  public void getSourceIfTypeDoesNotMatch() {
    StatusEvent event = new StatusEvent( mock( StatusProvider.class ) );

    Optional<MyStatusProvider> actual = event.getSource( MyStatusProvider.class );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getSourceWithNullAsTypeArgument() {
    StatusEvent event = new StatusEvent( mock( StatusProvider.class ) );

    Throwable actual = thrownBy( () -> event.getSource( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSourceArgument() {
    new StatusEvent( null );
  }
}