package com.codeaffine.home.control.status.internal.section;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.WORK_AREA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.status.internal.section.SectionFactory;
import com.codeaffine.home.control.status.model.SectionProvider.Section;

public class SectionFactoryTest {

  private SectionFactory factory;

  @Before
  public void setUp() {
    factory = new SectionFactory( mock( EntityRelationProvider.class ) );
  }

  @Test
  public void create() {
    Section actual = factory.create( WORK_AREA );

    assertThat( actual.getDefinition() ).isSameAs( WORK_AREA );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    factory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new SectionFactory( null );
  }
}