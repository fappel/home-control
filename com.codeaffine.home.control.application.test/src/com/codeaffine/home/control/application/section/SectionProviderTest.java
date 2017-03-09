package com.codeaffine.home.control.application.section;

import static com.codeaffine.home.control.application.internal.section.EntityRelationHelper.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BedStand;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.section.SectionProvider;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class SectionProviderTest {

  private EntityRelationProvider entityRelationProvider;
  private SectionProvider sectionProvider;

  @Before
  public void setUp() {
    entityRelationProvider = mock( EntityRelationProvider.class );
    sectionProvider = new SectionProvider( entityRelationProvider );
  }

  @Test
  public void findAll() {
    Collection<Section> actual = sectionProvider.findAll();

    assertThat( actual ).hasSize( SectionDefinition.values().length );
  }

  @Test
  public void findByDefinition() {
    Section actual = sectionProvider.findByDefinition( BED );

    assertThat( actual.getDefinition() ).isSameAs( BED );
  }

  @Test
  public void dispose() {
    sectionProvider.dispose();

    assertThat( sectionProvider.findAll() ).isEmpty();
    assertThat( sectionProvider.findByDefinition( BED ) ).isNull();
  }

  @Test
  public void getChildrenOnProvidedSection() {
    Lamp expected = mock( Lamp.class );
    stubEntityRelation( entityRelationProvider, BED, BedStand );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BedStand, expected );

    Section section = sectionProvider.findByDefinition( BED );
    Collection<Lamp> actual = section.getChildren( LampDefinition.class );

    assertThat( actual ).contains( expected );
  }

  @Test
  public void getStreamOfDefinitions() {
    Stream<SectionDefinition> actual = sectionProvider.getStreamOfDefinitions();

    assertThat( actual.collect( toSet() ) )
      .hasSize( SectionDefinition.values().length )
      .contains( SectionDefinition.values() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new SectionProvider( null );
  }
}