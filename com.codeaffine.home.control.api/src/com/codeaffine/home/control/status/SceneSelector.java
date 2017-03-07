package com.codeaffine.home.control.status;

import java.util.function.Predicate;

public interface SceneSelector {

  public interface NodeCondition<S> {
    NodeDefinition matches( Predicate<S> predicate );
  }

  public interface NodeDefinition {
    <S, T extends StatusProvider<S>> NodeCondition<S> and( Class<T> statusProviderType );
    <S, T extends StatusProvider<S>> NodeCondition<S> or( Class<T> statusProviderType );
    <T extends Scene> Branch thenSelect( Class<T> sceneType );
    <S, T extends StatusProvider<S>> NodeCondition<S> whenStatusOf( Class<T> statusProviderType );
  }

  public interface Branch {
    <S, T extends StatusProvider<S>>  NodeCondition<S> otherwiseWhenStatusOf( Class<T> statusProviderType );
    <T extends Scene> Branch otherwiseSelect( Class<T> sceneType );
  }

  <S> NodeCondition<S> whenStatusOf( Class<? extends StatusProvider<S>> statusProviderType );
}