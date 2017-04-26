package com.codeaffine.home.control.admin.app.mock;

import java.util.function.Consumer;
import java.util.function.Function;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

public class ComponentAccessServiceMock implements ComponentAccessService {

  private final ComponentSupplierImplementation supplier;
  private final TestContext context;

  static class ComponentSupplierImplementation implements ComponentSupplier {

    private final Context context;

    public ComponentSupplierImplementation( Context context ) {
      this.context = context;
    }

    @Override
    public <T> T get( Class<T> key ) {
      return context.get( key );
    }
  }

  public ComponentAccessServiceMock() {
    context = new TestContext();
    context.set( PreferenceModel.class, new PreferenceModelImpl() );
    supplier = new ComponentSupplierImplementation( context );
  }

  @Override
  public void execute( Consumer<ComponentSupplier> command ) {
    command.accept( supplier );
  }

  @Override
  public <T> T submit( Function<ComponentSupplier, T> task ) {
    return task.apply( supplier );
  }
}