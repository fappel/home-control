package com.codeaffine.home.control.engine.entity;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

class Sets {

  @SafeVarargs
  static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }
}