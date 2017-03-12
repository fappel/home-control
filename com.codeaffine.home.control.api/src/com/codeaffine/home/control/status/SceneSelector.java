package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import java.util.function.Function;
import java.util.function.Predicate;

import com.codeaffine.home.control.Context;

public interface SceneSelector {

  public interface NodeCondition<S> {
    NodeDefinition matches( Predicate<S> predicate );
  }

  public interface NodeDefinition {
    <S, T extends StatusProvider<S>> NodeCondition<S> and( Class<T> statusProviderType );
    <S, T extends StatusProvider<S>> NodeCondition<S> or( Class<T> statusProviderType );
    <T extends Scene> Branch thenSelect( Class<T> sceneType );
    <S, T extends StatusProvider<S>, U extends Scene> Branch
      thenSelect( Class<T> statusProviderType, Function<S, Class<U>> sceneProvider );
    <S, T extends StatusProvider<S>> NodeCondition<S> whenStatusOf( Class<T> statusProviderType );
  }

  public interface Branch {
    <S, T extends StatusProvider<S>>  NodeCondition<S> otherwiseWhenStatusOf( Class<T> statusProviderType );
    <T extends Scene> Branch otherwiseSelect( Class<T> sceneType );
    <S, T extends StatusProvider<S>, U extends Scene> Branch
      otherwiseSelect( Class<T> statusProviderType, Function<S, Class<U>> sceneProvider );
  }

  public interface Scope extends Comparable<Scope> {
    String getName();
    int getOrdinal();
  }

  <S> NodeCondition<S> whenStatusOf( Scope scope, Class<? extends StatusProvider<S>> statusProviderType );

  static <T extends Scene> T loadScene( Context context, Class<T> sceneType ) {
    verifyNotNull( sceneType, "sceneType" );
    verifyNotNull( context, "context" );

    T result = context.get( sceneType );
    if( result == null ) {
      result = context.create( sceneType );
      context.set( sceneType, result );
    }
    return result;
  }
}