package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.MY_ENTITY_DEFINITIONS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityRelationProvider.EntityRelationConfiguration;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.entity.MyEntityDefinition;

public class EntityRelationProviderImplTest {

  private static final MyEntityDefinition PARENT_ENTITY = MY_ENTITY_DEFINITIONS.get( 0 );
  private static final MyEntityDefinition CHILD_ENTITY = MY_ENTITY_DEFINITIONS.get( 1 );

  static class Configuration implements EntityRelationConfiguration {

    @Override
    public void configureFacility( Facility facility ) {
      facility.equip( PARENT_ENTITY ).with( CHILD_ENTITY );
    }
  }

  private EntityRelationProviderImpl provider;

  @Before
  public void setUp() {
    provider = new EntityRelationProviderImpl();
    provider.establishRelations( new Configuration() );
  }

  @Test
  public void getChildren() {
    assertThat( provider.getChildren( PARENT_ENTITY, MyEntityDefinition.class ) )
      .hasSize( 1 )
      .contains( CHILD_ENTITY );
  }

  @Test
  public void getChildrenWithWrongKey() {
    assertThat( provider.getChildren( CHILD_ENTITY, MyEntityDefinition.class ) ).isEmpty();
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.getChildren( CHILD_ENTITY, MyEntityDefinition.class ) ).isEmpty();
  }
}