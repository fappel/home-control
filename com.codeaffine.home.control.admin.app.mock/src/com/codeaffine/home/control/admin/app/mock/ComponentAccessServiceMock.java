package com.codeaffine.home.control.admin.app.mock;

import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.engine.component.event.EventBusImpl;
import com.codeaffine.home.control.engine.component.preference.PreferenceModelImpl;
import com.codeaffine.home.control.engine.component.util.BundleDeactivationTracker;
import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.event.EventBus;
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
    context.set( TypeUnloadTracker.class, new BundleDeactivationTracker( getBundleContext() ) );
    context.set( EventBus.class, context.create( EventBusImpl.class ) );
    context.set( PreferenceModel.class, context.create( PreferenceModelImpl.class ) );
    supplier = new ComponentSupplierImplementation( context );
    context.get( PreferenceModel.class ).get( MockPreference.class );
  }

  @Override
  public void execute( Consumer<ComponentSupplier> command ) {
    command.accept( supplier );
  }

  @Override
  public <T> T submit( Function<ComponentSupplier, T> task ) {
    return task.apply( supplier );
  }

  private BundleContext getBundleContext() {
    return FrameworkUtil.getBundle( getClass() ).getBundleContext();
  }
}