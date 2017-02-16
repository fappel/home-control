package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.MY_ENTITY_DEFINITIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.MyEntity;
import com.codeaffine.home.control.entity.MyEntityDefinition;
import com.codeaffine.home.control.entity.MyEntityProvider;

public class EntityRegistryImplTest {

  private EntityRegistryImpl entityRegistry;
  private TestContext context;

  @Before
  public void setUp() {
    context = new TestContext();
    entityRegistry = new EntityRegistryImpl( context );
  }

  @Test
  public void name() {
    entityRegistry.register( MyEntityProvider.class );

    Collection<Entity<?>> actual = entityRegistry.findAll();

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void findByDefinition() {
    entityRegistry.register( MyEntityProvider.class );

    MyEntity actual = entityRegistry.findByDefinition( MY_ENTITY_DEFINITIONS.get( 0 ) );

    assertThat( actual ).isNotNull();
  }

  @Test( expected = NoSuchElementException.class )
  public void findByDefinitionWithUnknownEntity() {
    entityRegistry.register( MyEntityProvider.class );

    entityRegistry.findByDefinition( mock( MyEntityDefinition.class ) );
  }

  @Test( expected = NoSuchElementException.class )
  public void findByDefinitionIfNotRegistered() {
    entityRegistry.findByDefinition( MY_ENTITY_DEFINITIONS.get( 0 ) );
  }


  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsArgument() {
    entityRegistry.register( null );
  }
}