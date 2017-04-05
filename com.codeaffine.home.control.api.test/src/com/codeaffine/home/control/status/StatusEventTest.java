package com.codeaffine.home.control.status;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Test;

import com.codeaffine.home.control.test.util.status.MyStatusSupplier;

public class StatusEventTest {

  @Test
  public void getSource() {
    MyStatusSupplier expected = mock( MyStatusSupplier.class );
    StatusEvent event = new StatusEvent( expected );

    Optional<MyStatusSupplier> actual = event.getSource( MyStatusSupplier.class );

    assertThat( actual ).hasValue( expected );
  }

  @Test
  public void getSourceIfTypeDoesNotMatch() {
    StatusEvent event = new StatusEvent( mock( StatusSupplier.class ) );

    Optional<MyStatusSupplier> actual = event.getSource( MyStatusSupplier.class );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getSourceWithNullAsTypeArgument() {
    StatusEvent event = new StatusEvent( mock( StatusSupplier.class ) );

    Throwable actual = thrownBy( () -> event.getSource( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSourceArgument() {
    new StatusEvent( null );
  }
}