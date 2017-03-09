package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;
import com.codeaffine.home.control.test.util.entity.MyEntityProvider;

public class EntityRelationProviderImplTest {

  private EntityRelationProviderImpl provider;
  private AllocationTrackerImpl zoneProvider;
  private EntityRegistry registry;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    zoneProvider = new AllocationTrackerImpl( new EventBusImpl() );
    context.set( AllocationTrackerImpl.class, zoneProvider );
    context.set( SensorControlFactory.class, new SensorControlFactoryImpl( zoneProvider ) );
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
  public void findByDefinition() {
    MyEntity actual = provider.findByDefinition( CHILD );

    assertThat( actual.getDefinition() ).isSameAs( CHILD );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsChildArgument() {
    provider.findByDefinition( null );
  }

  @Test
  public void findByDefinitionType() {
    Collection<MyEntity> actual = provider.findByDefinitionType( MyEntityDefinition.class );

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionTypeWithNullAsChildArgument() {
    provider.findByDefinitionType( null );
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
    child.allocate();
    Set<Entity<?>> afterEngage = zoneProvider.getAllocated();
    child.release();
    Set<Entity<?>> afterRelease = zoneProvider.getAllocated();

    assertThat( afterEngage ).contains( parent );
    assertThat( afterRelease ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void establishRelationsWithNullAsConfigurationArgument() {
    provider.establishRelations( null );
  }
}