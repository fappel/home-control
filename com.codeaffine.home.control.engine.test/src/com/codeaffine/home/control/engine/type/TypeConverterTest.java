package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.engine.type.TypeConverter;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.OpenClosedType;
import com.codeaffine.home.control.type.PercentType;
import com.codeaffine.home.control.type.StringType;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class TypeConverterTest {

  public static class StatusToState {

    public final Status status;
    public final State state;

    public StatusToState( Status status, State state ) {
      this.status = status;
      this.state = state;
    }
  }

  public static class StatusToStateMappingProvider {

    private static final int DECIMAL_VALUE = 2;
    private static final int PERCENT_VALUE = 4;
    private static final String STRING_VALUE = "2";

    public static Object[] provideData() {
      return new Object[] {
        new StatusToState( OpenClosedType.OPEN,
                           org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN ),
        new StatusToState( OnOffType.ON,
                           org.eclipse.smarthome.core.library.types.OnOffType.ON ),
        new StatusToState( new DecimalType( DECIMAL_VALUE ),
                           new org.eclipse.smarthome.core.library.types.DecimalType( DECIMAL_VALUE ) ),
        new StatusToState( new PercentType( PERCENT_VALUE ),
                           new org.eclipse.smarthome.core.library.types.PercentType( PERCENT_VALUE ) ),
        new StatusToState( new StringType( STRING_VALUE ),
                           new org.eclipse.smarthome.core.library.types.StringType( STRING_VALUE ) )
      };
    }
  }

  static class UnknownStatus implements Status {}

  @Test
  @Parameters( source = StatusToStateMappingProvider.class )
  @SuppressWarnings("unchecked")
  public <T extends Status> void convertToStatusType( StatusToState mapEntry ) {
     Optional<T> actual = TypeConverter.convert( mapEntry.state, ( Class<T>)mapEntry.status.getClass() );

     assertThat( actual ).hasValue( ( T )mapEntry.status );
  }

  @Test
  @Parameters( source = StatusToStateMappingProvider.class )
  @SuppressWarnings("unchecked")
  public <T extends Status> void convertToStateType( StatusToState mapEntry ) {
    State actual = TypeConverter.convert( ( T )mapEntry.status, ( Class<T>)mapEntry.status.getClass() );

    assertThat( actual ).isEqualTo( mapEntry.state );
  }

  @Test
  public void convertToStatusTypeWithUnknownStatusType() {
    State state = mock( State.class );

    Throwable actual = thrownBy( () -> TypeConverter.convert( state, UnknownStatus.class ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATE_TYPE_TO_ADAPT, state.getClass().getName() ) );
  }

  @Test
  public void convertToStateTypeWithUnknownStatusType() {
    Throwable actual = thrownBy( () -> TypeConverter.convert( new UnknownStatus(), UnknownStatus.class ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNKNOWN_STATUS_TYPE_TO_ADAPT, UnknownStatus.class.getName() ) );
  }
}