package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Test;

import com.codeaffine.home.control.engine.type.OpenClosedTypeConverter;
import com.codeaffine.home.control.type.OpenClosedType;

public class OpenClosedTypeConverterTest {

  @Test
  public void convertStateOPEN() {
    org.eclipse.smarthome.core.library.types.OpenClosedType state
      = org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN;

    Optional<OpenClosedType> actual = OpenClosedTypeConverter.convert( state );

    assertThat( actual ).hasValue( OpenClosedType.OPEN );
  }

  @Test
  public void convertStateCLOSED() {
    org.eclipse.smarthome.core.library.types.OpenClosedType state
      = org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED;

    Optional<OpenClosedType> actual = OpenClosedTypeConverter.convert( state );

    assertThat( actual ).hasValue( OpenClosedType.CLOSED );
  }

  @Test
  public void convertFromUnDefTypeNull() {
    Optional<OpenClosedType> actual = OpenClosedTypeConverter.convert( UnDefType.NULL );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnDefTypeUndef() {
    Optional<OpenClosedType> actual = OpenClosedTypeConverter.convert( UnDefType.UNDEF );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnknownState() {
    State unknownState = mock( State.class );

    Throwable actual = thrownBy( () -> OpenClosedTypeConverter.convert( unknownState ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_VALUE_FOR_OPEN_CLOSED_TYPE, unknownState ) );
  }


  @Test
  public void convertStatusOPEN() {
    org.eclipse.smarthome.core.library.types.OpenClosedType actual
      = ( org.eclipse.smarthome.core.library.types.OpenClosedType )
        OpenClosedTypeConverter.convert( OpenClosedType.OPEN );

    assertThat( actual ).isSameAs( org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );
  }

  @Test
  public void convertStatusCLOSED() {
    org.eclipse.smarthome.core.library.types.OpenClosedType actual
      = ( org.eclipse.smarthome.core.library.types.OpenClosedType )
        OpenClosedTypeConverter.convert( OpenClosedType.CLOSED );

    assertThat( actual ).isSameAs( org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );
  }
}