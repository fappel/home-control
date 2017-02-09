package com.codeaffine.home.control.internal.type;

import static com.codeaffine.home.control.internal.type.Messages.ERROR_UNKNOWN_STATE_VALUE_FOR_STRING_TYPE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Test;

import com.codeaffine.home.control.type.StringType;

public class StringTypeConverterTest {

  private static final String VALUE = "value";

  @Test
  public void convertFromStringTypeState() {
    org.eclipse.smarthome.core.library.types.StringType state
      = new org.eclipse.smarthome.core.library.types.StringType( VALUE );

    Optional<StringType> actual = StringTypeConverter.convert( state );

    assertThat( actual ).hasValue( new StringType( VALUE ) );
  }

  @Test
  public void convertFromUnDefTypeNull() {
    Optional<StringType> actual = StringTypeConverter.convert( UnDefType.NULL );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnDefTypeUndef() {
    Optional<StringType> actual = StringTypeConverter.convert( UnDefType.UNDEF );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnknownState() {
    State unknownState = mock( State.class );

    Throwable actual = thrownBy( () -> StringTypeConverter.convert( unknownState ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_VALUE_FOR_STRING_TYPE, unknownState ) );
  }

  @Test
  public void convertFromDateTimeTypeState() {
    org.eclipse.smarthome.core.library.types.DateTimeType state
      = new org.eclipse.smarthome.core.library.types.DateTimeType( Calendar.getInstance() );

    Throwable actual = thrownBy( () -> StringTypeConverter.convert( state ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( Messages.ERROR_UNSUPPORTED_CONVERSION_FROM_DATA_TIME_TYPE, state ) );
  }

  @Test
  public void convertFromStringTypeStatus() {
    StringType status = new StringType( VALUE );

    org.eclipse.smarthome.core.library.types.StringType actual
      = ( org.eclipse.smarthome.core.library.types.StringType )StringTypeConverter.convert( status );

    assertThat( actual ).isEqualTo( new org.eclipse.smarthome.core.library.types.StringType( VALUE ) );
  }
}