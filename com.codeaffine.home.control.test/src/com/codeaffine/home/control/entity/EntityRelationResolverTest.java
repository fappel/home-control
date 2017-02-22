package com.codeaffine.home.control.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.TestContext;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.internal.entity.EntityRegistryImpl;
import com.codeaffine.home.control.internal.entity.EntityRelationProviderImpl;

public class EntityRelationResolverTest {

  private static final MyEntityDefinition PARENT_DEFINITION = MyEntityProvider.MY_ENTITY_DEFINITIONS.get( 0 );
  private static final MyEntityDefinition CHILD_DEFINITION = MyEntityProvider.MY_ENTITY_DEFINITIONS.get( 1 );

  private EntityRelationProviderImpl relationProvider;
  private EntityRegistryImpl entityRegistry;
  private EntityRelationResolver<MyEntityDefinition> resolver;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    entityRegistry = new EntityRegistryImpl( new TestContext() );
    entityRegistry.register( MyEntityProvider.class );
    relationProvider = new EntityRelationProviderImpl( entityRegistry );
    resolver = new EntityRelationResolver<>( PARENT_DEFINITION, relationProvider );
  }

  @Test
  public void getChildren() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT_DEFINITION ).with( CHILD_DEFINITION ) );

    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual )
      .hasSize( 1 )
      .contains( entityRegistry.findByDefinition( CHILD_DEFINITION ) );
  }

  @Test
  public void getChildrenOfEntityWithoutChildren() {
    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getChildren2() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT_DEFINITION ).with( CHILD_DEFINITION ) );

    Collection<MyEntity> actual = resolver.getChildren( MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( entityRegistry.findByDefinition( CHILD_DEFINITION ) );
  }

  @Test
  public void getChildren2WithNoMatchingFilterCriteria() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT_DEFINITION ).with( CHILD_DEFINITION ) );

    @SuppressWarnings("unchecked")
    Collection<Entity<?>> actual = resolver.getChildren( SomeEntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getChildren2OfEntityWithoutChildren() {
    Collection<MyEntity> actual = resolver.getChildren( MyEntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildren2WithNullAsChildTypeArgument() {
    resolver.getChildren( null );
  }

  @Test
  public void getDefinition() {
    MyEntityDefinition actual = resolver.getDefinition();

    assertThat( actual ).isSameAs( PARENT_DEFINITION );
  }
}