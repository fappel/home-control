package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.MY_ENTITY_DEFINITIONS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.MyEntity;
import com.codeaffine.home.control.entity.MyEntityDefinition;
import com.codeaffine.home.control.entity.MyEntityProvider;

public class EntityRelationProviderImplTest {

  private static final MyEntityDefinition PARENT_ENTITY = MY_ENTITY_DEFINITIONS.get( 0 );
  private static final MyEntityDefinition CHILD_ENTITY = MY_ENTITY_DEFINITIONS.get( 1 );

  private EntityRelationProviderImpl provider;
  private EntityRegistry registry;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    registry = mock( EntityRegistry.class );
    provider = new EntityRelationProviderImpl( registry );
    provider.establishRelations( facility -> facility.equip( PARENT_ENTITY ).with( CHILD_ENTITY ) );
  }

  @Test
  public void getChildren() {
    Collection<MyEntityDefinition> actual = provider.getChildren( PARENT_ENTITY, MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( CHILD_ENTITY );
  }

  @Test
  public void getChildrenWithSuperType() {
    @SuppressWarnings("unchecked")
    Collection<EntityDefinition<?>> actual = provider.getChildren( PARENT_ENTITY, EntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( CHILD_ENTITY );
  }

  @Test
  public void getChildrenForUnknownType() {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    Collection<SomeEntityDefinition> actual = provider.getChildren( PARENT_ENTITY, SomeEntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithNullAsParentDefinitionArgument() {
    provider.getChildren( null, MyEntityDefinition.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithNullAsChildDefinitionTypeArgument() {
    provider.getChildren( PARENT_ENTITY, null );
  }

  @Test
  public void getChildrenWithWrongKey() {
    assertThat( provider.getChildren( CHILD_ENTITY, MyEntityDefinition.class ) ).isEmpty();
  }

  @Test
  public void findEntityByDefinition() {
    MyEntity expected = mock( MyEntity.class );
    MyEntityDefinition definition = MyEntityProvider.MY_ENTITY_DEFINITIONS.get( 0 );
    when( registry.findByDefinition( definition ) ).thenReturn( expected );

    MyEntity actual = provider.findByDefinition( definition );

    assertThat( actual ).isSameAs( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsChildArgument() {
    provider.findByDefinition( null );
  }

  @Test
  public void findAll() {
    List<Entity<?>> expected = asList( mock( MyEntity.class ) );
    when( registry.findAll() ).thenReturn( expected );

    Collection<Entity<?>> actual = provider.findAll();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.getChildren( CHILD_ENTITY, MyEntityDefinition.class ) ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void establishRelationsWithNullAsConfigurationArgument() {
    provider.establishRelations( null );
  }
}