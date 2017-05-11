package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StateVerificationTest {

  @Test
  public void verifyStateIfValid() {
    Throwable actual = thrownBy( () -> StateVerification.verifyState( true, "", new Object() ) );

    assertThat( actual ).isNull();
  }

  @Test
  public void verifyStateIfNotValid() {
    Object expected = new Object();

    Throwable actual = thrownBy( () -> StateVerification.verifyState( false, "%s", expected ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( expected.toString() );
  }
}
