package com.codeaffine.home.control.application.operation;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusProvider;

public class HomeControlOperations {

  @SafeVarargs
  public static boolean isRelated( StatusEvent event, Class<? extends StatusProvider<?>> ... providerTypes ) {
    verifyNotNull( providerTypes, "providerTypes" );
    verifyNotNull( event, "event" );

    return Stream.of( providerTypes ).anyMatch( type -> event.getSource( type ).isPresent() );
  }
}