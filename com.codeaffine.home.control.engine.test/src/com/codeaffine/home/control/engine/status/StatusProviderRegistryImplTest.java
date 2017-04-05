package com.codeaffine.home.control.engine.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.engine.status.StatusProviderRegistryImpl;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatusSupplier;

public class StatusProviderRegistryImplTest {

  private StatusProviderRegistryImpl registry;
  private TestContext context;

  @Before
  public void setUp() {
    context = new TestContext();
    registry = new StatusProviderRegistryImpl( context );
  }

  @Test
  public void getContext() {
    Context actual = registry.getContext();

    assertThat( actual ).isSameAs( context );
  }

  @Test
  public void register() {
    registry.register( MyStatusSupplier.class, MyStatusSupplier.class );
    MyStatusSupplier actual = context.get( MyStatusSupplier.class );

    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsTypeArgument() {
    registry.register( null, MyStatusSupplier.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsImplementationArgument() {
    registry.register( MyStatusSupplier.class, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new StatusProviderRegistryImpl( null );
  }
}
