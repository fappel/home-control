package com.codeaffine.home.control.engine.type;

import static com.codeaffine.home.control.engine.type.Messages.*;
import static java.lang.String.format;
import static java.util.Optional.empty;

import java.util.Optional;

import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

import com.codeaffine.home.control.type.OnOffType;

public class OnOffTypeConverter {

  public static Optional<OnOffType> convert( State state ) {
    Optional<OnOffType> result = null;
    if( state == UnDefType.NULL ) {
      result = empty();
    } else if( state == UnDefType.UNDEF ) {
      result = empty();
    } else if( state == org.eclipse.smarthome.core.library.types.OnOffType.ON ) {
      result = Optional.of( OnOffType.ON );
    } else if( state == org.eclipse.smarthome.core.library.types.OnOffType.OFF ) {
      result = Optional.of( OnOffType.OFF );
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATE_VALUE_FOR_ON_OFF_TYPE, state ) );
    }
    return result;
  }

  public static State convert( OnOffType status ) {
    State result = null;
    if( status == OnOffType.ON ) {
      result = org.eclipse.smarthome.core.library.types.OnOffType.ON;
    } else if( status == OnOffType.OFF  ) {
      result = org.eclipse.smarthome.core.library.types.OnOffType.OFF;
    } else {
      throw new IllegalStateException( format( ERROR_UNKNOWN_STATUS_VALUE_FOR_ON_OFF_TYPE, status ) );
    }
    return result;
  }
}