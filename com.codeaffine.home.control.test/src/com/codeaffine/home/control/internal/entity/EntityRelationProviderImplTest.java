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
import com.codeaffine.home.control.entity.EntityRelationProvider.EntityRelationConfiguration;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.entity.MyEntity;
import com.codeaffine.home.control.entity.MyEntityDefinition;
import com.codeaffine.home.control.entity.MyEntityProvider;

public class EntityRelationProviderImplTest {

  private static final MyEntityDefinition PARENT_ENTITY = MY_ENTITY_DEFINITIONS.get( 0 );
  private static final MyEntityDefinition CHILD_ENTITY = MY_ENTITY_DEFINITIONS.get( 1 );

  private EntityRelationProviderImpl provider;
  private EntityRegistry registry;

  static class Configuration implements EntityRelationConfiguration {

    @Override
    public void configureFacility( Facility facility ) {
      facility.equip( PARENT_ENTITY ).with( CHILD_ENTITY );
    }
  }

  @Before
  public void setUp() {
    registry = mock( EntityRegistry.class );
    provider = new EntityRelationProviderImpl( registry );
    provider.establishRelations( new Configuration() );
  }

  @Test
  public void getChildren() {
    assertThat( provider.getChildren( PARENT_ENTITY ) )
      .hasSize( 1 )
      .contains( CHILD_ENTITY );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithNullAsParentDefinitionArgument() {
    provider.getChildren( null );
  }

  @Test
  public void getChildren2() {
    assertThat( provider.getChildren( PARENT_ENTITY, MyEntityDefinition.class ) )
      .hasSize( 1 )
      .contains( CHILD_ENTITY );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildren2WithNullAsParentDefinitionArgument() {
    provider.getChildren( null, MyEntityDefinition.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildren2WithNullAsChildDefinitionTypeArgument() {
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