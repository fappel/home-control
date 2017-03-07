package com.codeaffine.home.control.internal.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

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
    registry.register( MyStatusProvider.class, MyStatusProvider.class );
    MyStatusProvider actual = context.get( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsTypeArgument() {
    registry.register( null, MyStatusProvider.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsImplementationArgument() {
    registry.register( MyStatusProvider.class, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new StatusProviderRegistryImpl( null );
  }
}
