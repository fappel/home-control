package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;

public class EntityRelationResolverTest {

  private EntityRelationResolver<MyEntityDefinition> resolver;
  private EntityRelationProvider relationProvider;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    relationProvider = mock( EntityRelationProvider.class );
    resolver = new EntityRelationResolver<>( PARENT, relationProvider );
  }

  @Test
  public void getChildren() {
    MyEntity expected = mock( MyEntity.class );
    stubGetChildrenRelationLookup( expected, PARENT, CHILD );

    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test
  public void getChildrenOfEntityWithoutChildren() {
    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getChildren2() {
    MyEntity expected = mock( MyEntity.class );
    stubRelationLookup( expected, PARENT, CHILD, MyEntityDefinition.class );


    Collection<MyEntity> actual = resolver.getChildren( MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test
  public void getChildren2WithNoMatchingFilterCriteria() {
    stubRelationLookup( mock( MyEntity.class ), PARENT, CHILD, MyEntityDefinition.class );


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

    assertThat( actual ).isSameAs( PARENT );
  }

  private <R extends Entity<C>, C extends EntityDefinition<R>> void stubRelationLookup(
    R expected, EntityDefinition<?> parent, C child, Class<C> childType )
  {
    when( relationProvider.getChildren( parent, childType ) ).thenReturn( asList( child ) );
    when( relationProvider.findByDefinition( child ) ).thenReturn( expected );
  }

  private <R extends Entity<C>, C extends EntityDefinition<R>> void stubGetChildrenRelationLookup(
    R expected, EntityDefinition<?> parent, C child )
  {
    when( relationProvider.getChildren( parent, EntityDefinition.class ) ).thenReturn( asList( child ) );
    when( relationProvider.findByDefinition( child ) ).thenReturn( expected );
  }
}