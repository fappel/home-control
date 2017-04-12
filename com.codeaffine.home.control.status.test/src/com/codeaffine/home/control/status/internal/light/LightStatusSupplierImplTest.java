package com.codeaffine.home.control.status.internal.light;

import static com.codeaffine.home.control.status.internal.light.Messages.INFO_MESSAGE_LIGHT_STATUS;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static com.codeaffine.home.control.test.util.event.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.test.util.logger.LoggerHelper.captureSingleInfoArgument;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.model.LightEvent;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.LightStatus;
import com.codeaffine.home.control.status.supplier.LightStatusSupplier;

public class LightStatusSupplierImplTest {

  private static final Entity<EntityDefinition<?>> SECTION = stubEntity( BED );
  private static final Integer LIGHT_VALUE = Integer.valueOf( 6 );

  private Logger logger;
  private EventBus eventBus;
  private LightStatusSupplierImpl supplier;

  @Before
  public void setUp() {
    logger = mock( Logger.class );
    eventBus = mock( EventBus.class );
    supplier = new LightStatusSupplierImpl( eventBus, logger );
  }

  @Test
  public void initialStatus() {
    LightStatus actual = supplier.getStatus();

    assertThat( actual ).isEqualTo( new LightStatus( emptyMap() ) );
  }

  @Test
  public void onLightStatusChanged() {
    LightStatus expected = new LightStatus( createMap( LIGHT_VALUE, SECTION.getDefinition() ) );

    supplier.onLightStatusChange( newEvent( LIGHT_VALUE, SECTION ) );

    assertThat( captureEvent( eventBus, LightStatusSupplier.class ) ).hasValue( supplier );
    assertThat( captureSingleInfoArgument( logger, INFO_MESSAGE_LIGHT_STATUS ) ).isEqualTo( expected );
    assertThat( supplier.getStatus() ).isEqualTo( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new LightStatusSupplierImpl( null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new LightStatusSupplierImpl( eventBus, null );
  }

  private static HashMap<SectionDefinition, Integer> createMap( Integer lightValue, EntityDefinition<?> definition ) {
    HashMap<SectionDefinition, Integer> result = new HashMap<>();
    result.put( ( SectionDefinition )definition, lightValue );
    return result;
  }

  private static LightEvent newEvent( Integer lightValue, Entity<EntityDefinition<?>> section ) {
    return new LightEvent( mock( Sensor.class ), lightValue, section );
  }
}