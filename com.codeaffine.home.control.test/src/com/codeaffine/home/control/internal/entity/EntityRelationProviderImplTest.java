package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.TestContext;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.MyEntity;
import com.codeaffine.home.control.entity.MyEntityDefinition;
import com.codeaffine.home.control.entity.MyEntityProvider;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class EntityRelationProviderImplTest {

  private EntityRelationProviderImpl provider;
  private ZoneProviderImpl zoneProvider;
  private EntityRegistry registry;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    zoneProvider = new ZoneProviderImpl( new EventBusImpl() );
    context.set( ZoneProviderImpl.class, zoneProvider );
    registry = new EntityRegistryImpl( context );
    registry.register( MyEntityProvider.class );
    provider = new EntityRelationProviderImpl( registry );
  }

  @Test
  public void getChildren() {
    provider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

    Collection<MyEntityDefinition> actual = provider.getChildren( PARENT, MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( CHILD );
  }

  @Test
  public void getChildrenWithSuperType() {
    provider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

    @SuppressWarnings("unchecked")
    Collection<EntityDefinition<?>> actual = provider.getChildren( PARENT, EntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( CHILD );
  }

  @Test
  public void getChildrenForUnknownType() {
    provider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

    @SuppressWarnings({ "rawtypes", "unchecked" })
    Collection<SomeEntityDefinition> actual = provider.getChildren( PARENT, SomeEntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getChildrenForUnknownRelations() {
    Collection<MyEntityDefinition> actual = provider.getChildren( PARENT, MyEntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithNullAsParentDefinitionArgument() {
    provider.getChildren( null, MyEntityDefinition.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithNullAsChildDefinitionTypeArgument() {
    provider.getChildren( PARENT, null );
  }

  @Test
  public void getChildrenWithWrongKey() {
    assertThat( provider.getChildren( CHILD, MyEntityDefinition.class ) ).isEmpty();
  }

  @Test
  public void findEntityByDefinition() {
    MyEntity actual = provider.findByDefinition( CHILD );

    assertThat( actual.getDefinition() ).isSameAs( CHILD );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsChildArgument() {
    provider.findByDefinition( null );
  }

  @Test
  public void findAll() {
    Collection<Entity<?>> actual = provider.findAll();

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.getChildren( CHILD, MyEntityDefinition.class ) ).isEmpty();
  }

  @Test
  public void establishRelations() {
    provider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );
    MyEntity parent = provider.findByDefinition( PARENT );
    MyEntity child = provider.findByDefinition( CHILD );
    child.engage();
    Set<Entity<?>> afterEngage = zoneProvider.getEngagedZones();
    child.release();
    Set<Entity<?>> afterRelease = zoneProvider.getEngagedZones();

    assertThat( afterEngage ).contains( parent );
    assertThat( afterRelease ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void establishRelationsWithNullAsConfigurationArgument() {
    provider.establishRelations( null );
  }
}