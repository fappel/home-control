package com.codeaffine.home.control.internal.type;

import static com.codeaffine.home.control.internal.type.Messages.ERROR_UNKNOWN_STATE_VALUE_FOR_ON_OFF_TYPE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Test;

import com.codeaffine.home.control.type.OnOffType;

public class OnOffTypeConverterTest {

  @Test
  public void convertStateON() {
    org.eclipse.smarthome.core.library.types.OnOffType state
      = org.eclipse.smarthome.core.library.types.OnOffType.ON;

    Optional<OnOffType> actual = OnOffTypeConverter.convert( state );

    assertThat( actual ).hasValue( OnOffType.ON );
  }

  @Test
  public void convertStateOFF() {
    org.eclipse.smarthome.core.library.types.OnOffType state
      = org.eclipse.smarthome.core.library.types.OnOffType.OFF;

    Optional<OnOffType> actual = OnOffTypeConverter.convert( state );

    assertThat( actual ).hasValue( OnOffType.OFF );
  }

  @Test
  public void convertFromUnDefTypeNull() {
    Optional<OnOffType> actual = OnOffTypeConverter.convert( UnDefType.NULL );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnDefTypeUndef() {
    Optional<OnOffType> actual = OnOffTypeConverter.convert( UnDefType.UNDEF );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void convertFromUnknownState() {
    State unknownState = mock( State.class );

    Throwable actual = thrownBy( () -> OnOffTypeConverter.convert( unknownState ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_VALUE_FOR_ON_OFF_TYPE, unknownState ) );
  }


  @Test
  public void convertStatusON() {
    org.eclipse.smarthome.core.library.types.OnOffType actual
      = ( org.eclipse.smarthome.core.library.types.OnOffType )OnOffTypeConverter.convert( OnOffType.ON );

    assertThat( actual ).isSameAs( org.eclipse.smarthome.core.library.types.OnOffType.ON );
  }

  @Test
  public void convertStatusOFF() {
    org.eclipse.smarthome.core.library.types.OnOffType actual
      = ( org.eclipse.smarthome.core.library.types.OnOffType )OnOffTypeConverter.convert( OnOffType.OFF );

    assertThat( actual ).isSameAs( org.eclipse.smarthome.core.library.types.OnOffType.OFF );
  }
}