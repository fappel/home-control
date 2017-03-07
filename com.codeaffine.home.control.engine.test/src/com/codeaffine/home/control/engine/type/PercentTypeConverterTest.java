package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.ERROR_UNKNOWN_STATE_VALUE_FOR_DECIMAL_TYPE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Test;

import com.codeaffine.home.control.engine.type.PercentTypeConverter;
import com.codeaffine.home.control.type.PercentType;

public class PercentTypeConverterTest {

  private static final int VALUE = 10;

  @Test
  public void convertFromPercentTypeState() {
    org.eclipse.smarthome.core.library.types.PercentType state
      = new org.eclipse.smarthome.core.library.types.PercentType( VALUE );

    Optional<PercentType> actual = PercentTypeConverter.convert( state );

    assertThat( actual ).hasValue( new PercentType( VALUE ) );
  }

  @Test
  public void convertFromUnDefTypeNull() {
    Optional<PercentType> actual = PercentTypeConverter.convert( UnDefType.NULL );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnDefTypeUndef() {
    Optional<PercentType> actual = PercentTypeConverter.convert( UnDefType.UNDEF );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnknownState() {
    State unknownState = mock( State.class );

    Throwable actual = thrownBy( () -> PercentTypeConverter.convert( unknownState ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_VALUE_FOR_DECIMAL_TYPE, unknownState ) );
  }

  @Test
  public void convertFromPercentTypeStatus() {
    PercentType status = new PercentType( VALUE );

    org.eclipse.smarthome.core.library.types.PercentType actual
      = ( org.eclipse.smarthome.core.library.types.PercentType )PercentTypeConverter.convert( status );

    assertThat( actual ).isEqualTo( new org.eclipse.smarthome.core.library.types.PercentType( VALUE ) );
  }
}