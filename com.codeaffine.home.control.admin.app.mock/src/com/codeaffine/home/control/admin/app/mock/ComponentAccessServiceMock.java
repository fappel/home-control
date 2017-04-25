package com.codeaffine.home.control.admin.app.mock;

import java.util.function.Consumer;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class ComponentAccessServiceMock implements ComponentAccessService {

  private final TestContext context;

  public ComponentAccessServiceMock() {
    context = new TestContext();
    context.set( PreferenceModel.class, new PreferenceModelImpl() );
  }

  @Override
  public void execute( Consumer<ComponentSupplier> command ) {
    command.accept( new ComponentSupplier() {
      @Override
      public <T> T get( Class<T> key ) {
        return context.get( key );
      }
    } );
  }
}