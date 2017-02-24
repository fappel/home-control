package com.codeaffine.home.control;

public interface AdjustableItem<I extends Item<I, S>, S extends Status> extends Item<I, S>{
  void setStatus( S status );
  void updateStatus( S status );
}