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

import com.codeaffine.home.control.engine.type.DecimalTypeConverter;
import com.codeaffine.home.control.type.DecimalType;

public class DecimalTypeConverterTest {

  private static final int VALUE = 10;

  @Test
  public void convertFromDecimalTypeState() {
    org.eclipse.smarthome.core.library.types.DecimalType state
      = new org.eclipse.smarthome.core.library.types.DecimalType( VALUE );

    Optional<DecimalType> actual = DecimalTypeConverter.convert( state );

    assertThat( actual ).hasValue( new DecimalType( VALUE ) );
  }

  @Test
  public void convertFromUnDefTypeNull() {
    Optional<DecimalType> actual = DecimalTypeConverter.convert( UnDefType.NULL );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnDefTypeUndef() {
    Optional<DecimalType> actual = DecimalTypeConverter.convert( UnDefType.UNDEF );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnknownState() {
    State unknownState = mock( State.class );

    Throwable actual = thrownBy( () -> DecimalTypeConverter.convert( unknownState ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_VALUE_FOR_DECIMAL_TYPE, unknownState ) );
  }

  @Test
  public void convertFromDecimalTypeStatus() {
    DecimalType status = new DecimalType( VALUE );

    org.eclipse.smarthome.core.library.types.DecimalType actual
      = ( org.eclipse.smarthome.core.library.types.DecimalType )DecimalTypeConverter.convert( status );

    assertThat( actual ).isEqualTo( new org.eclipse.smarthome.core.library.types.DecimalType( VALUE ) );
  }
}