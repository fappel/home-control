package com.codeaffine.home.control.application.section;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import com.codeaffine.home.control.application.internal.section.SectionFactory;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class SectionProvider extends BaseEntityProvider<Section, SectionDefinition> {

  public enum SectionDefinition implements EntityDefinition<Section> {
    KITCHEN,
    COOKING_AREA,
    DINING_AREA,
    BEDROOM,
    BED,
    DRESSING_AREA,
    LIVING_ROOM,
    WORK_AREA,
    LIVING_AREA,
    HALL,
    BATH_ROOM
  }

  public interface Section extends CompositeEntity<SectionDefinition> {}

  public SectionProvider( EntityRelationProvider entityRelationProvider ) {
    super( new SectionFactory( verifyNotNull( entityRelationProvider, "entityRelationProvider" ) ) );
  }

  @Override
  protected Stream<SectionDefinition> getStreamOfDefinitions() {
    return Stream.of( SectionDefinition.values() );
  }
}
