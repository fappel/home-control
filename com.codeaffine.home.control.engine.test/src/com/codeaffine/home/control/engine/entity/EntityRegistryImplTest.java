package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.*;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.stubSensorControlFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;
import com.codeaffine.home.control.test.util.entity.MyEntityProvider;

public class EntityRegistryImplTest {

  private EntityRegistryImpl entityRegistry;
  private TestContext context;

  @Before
  public void setUp() {
    context = new TestContext();
    SensorControl sensorControl = mock( SensorControl.class );
    context.set( SensorControlFactory.class, stubSensorControlFactory( sensorControl ) );
    entityRegistry = new EntityRegistryImpl( context );
  }

  @Test
  public void findAll() {
    entityRegistry.register( MyEntityProvider.class );

    Collection<Entity<?>> actual = entityRegistry.findAll();

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void findByDefinitionType() {
    entityRegistry.register( MyEntityProvider.class );

    Collection<MyEntity> actual = entityRegistry.findByDefinitionType( MyEntityDefinition.class );

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void findByDefinitionTypeWithUnregisteredType() {
    entityRegistry.register( MyEntityProvider.class );

    @SuppressWarnings("unchecked")
    Collection<Entity<?>> actual = entityRegistry.findByDefinitionType( EntityDefinition.class );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionTypeWithNullAsArgument() {
    entityRegistry.findByDefinitionType( null );
  }

  @Test
  public void findByDefinition() {
    entityRegistry.register( MyEntityProvider.class );

    MyEntity actual = entityRegistry.findByDefinition( PARENT );

    assertThat( actual.getDefinition() ).isSameAs( PARENT );
  }

  @Test( expected = NoSuchElementException.class )
  public void findByDefinitionWithUnknownEntity() {
    entityRegistry.register( MyEntityProvider.class );

    entityRegistry.findByDefinition( mock( MyEntityDefinition.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void findByDefinitionWithNullAsArgument() {
    entityRegistry.findByDefinition( null );
  }

  @Test( expected = NoSuchElementException.class )
  public void findByDefinitionIfNotRegistered() {
    entityRegistry.findByDefinition( PARENT );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsArgument() {
    entityRegistry.register( null );
  }
}