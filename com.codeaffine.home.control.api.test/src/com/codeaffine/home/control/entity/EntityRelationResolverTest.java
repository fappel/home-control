package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.*;
import static com.codeaffine.home.control.test.util.entity.SensorControlFactoryHelper.stubSensorControlFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;
import com.codeaffine.home.control.internal.entity.EntityRegistryImpl;
import com.codeaffine.home.control.internal.entity.EntityRelationProviderImpl;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;
import com.codeaffine.home.control.test.util.entity.MyEntityProvider;

public class EntityRelationResolverTest {

  private EntityRelationResolver<MyEntityDefinition> resolver;
  private EntityRelationProviderImpl relationProvider;
  private EntityRegistryImpl entityRegistry;

  static class SomeEntityDefinition<T extends Entity<?>> implements EntityDefinition<T> {}

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    SensorControl sensorControl = mock( SensorControl.class );
    context.set( SensorControlFactory.class, stubSensorControlFactory( sensorControl ) );
    entityRegistry = new EntityRegistryImpl( context );
    entityRegistry.register( MyEntityProvider.class );
    relationProvider = new EntityRelationProviderImpl( entityRegistry );
    resolver = new EntityRelationResolver<>( PARENT, relationProvider );
  }

  @Test
  public void getChildren() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual )
      .hasSize( 1 )
      .contains( entityRegistry.findByDefinition( CHILD ) );
  }

  @Test
  public void getChildrenOfEntityWithoutChildren() {
    Collection<Entity<?>> actual = resolver.getChildren();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getChildren2() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

    Collection<MyEntity> actual = resolver.getChildren( MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( entityRegistry.findByDefinition( CHILD ) );
  }

  @Test
  public void getChildren2WithNoMatchingFilterCriteria() {
    relationProvider.establishRelations( facility -> facility.equip( PARENT ).with( CHILD ) );

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
}