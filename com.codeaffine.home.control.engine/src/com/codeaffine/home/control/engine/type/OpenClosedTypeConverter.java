package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.*;
import static java.lang.String.format;
import static java.util.Optional.empty;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

import com.codeaffine.home.control.type.OpenClosedType;

public class OpenClosedTypeConverter {

  public static Optional<OpenClosedType> convert( State state ) {
    Optional<OpenClosedType> result = null;
    if( state == UnDefType.NULL ) {
      result = empty();
    } else if( state == UnDefType.UNDEF ) {
      result = empty();
    } else if( state == org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED ) {
      result = Optional.of( OpenClosedType.CLOSED );
    } else if( state == org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN ) {
      result = Optional.of( OpenClosedType.OPEN );
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATE_VALUE_FOR_OPEN_CLOSED_TYPE, state ) );
    }
    return result;
  }

  public static State convert( OpenClosedType status ) {
    State result = null;
    if( status == OpenClosedType.CLOSED ) {
      result = org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED;
    } else if( status == OpenClosedType.OPEN  ) {
      result = org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN;
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATUS_VALUE_FOR_OPEN_CLOSED_TYPE, status ) );
    }
    return result;
  }
}