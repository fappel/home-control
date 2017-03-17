package com.codeaffine.home.control.engine.entity;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

public class Sets {

  @SafeVarargs
  public static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }
}