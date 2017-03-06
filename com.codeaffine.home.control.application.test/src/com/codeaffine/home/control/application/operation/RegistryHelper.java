package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.type.OnOff.OFF;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.room.RoomProvider.Room;
import com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

class RegistryHelper {

  static Set<Lamp> stubLamps( LampDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubLamp( definition ) ).collect( toSet() );
  }

  static Set<Room> stubZones( RoomDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> stubZone( definition ) ).collect( toSet() );
  }

  static EntityRegistry stubRegistry( Set<Room> zones, Set<Lamp> lamps ) {
    Set<Entity<?>> all = new HashSet<>( zones );
    all.addAll( lamps );
    EntityRegistry result = mock( EntityRegistry.class );
    when( result.findAll() ).thenReturn( all );
    when( result.findByDefinitionType( RoomDefinition.class ) ).thenReturn( zones );
    when( result.findByDefinitionType( LampDefinition.class ) ).thenReturn( lamps );
    when( result.findByDefinition( any( EntityDefinition.class ) ) )
      .thenAnswer( invocation -> doFindByDefinition( all, invocation.getArguments()[ 0 ] ) );
    return result;
  }

  private static Entity<?> doFindByDefinition( Set<Entity<?>> all, Object definition ) {
    return all.stream().filter( entity -> entity.getDefinition() == definition ).findFirst().get();
  }

  static void equipWithLamp(
    EntityRegistry registryStub, RoomDefinition zoneDefinition, LampDefinition ... lampDefinitions )
  {
    equipWithLamp( registryStub.findByDefinition( zoneDefinition ), collectLamps( registryStub, lampDefinitions ) );
  }

  private static Set<Lamp> collectLamps( EntityRegistry registryStub, LampDefinition ... definitions ) {
    return Stream.of( definitions ).map( definition -> registryStub.findByDefinition( definition ) ).collect( toSet() );
  }

  private static void equipWithLamp( Room zone, Set<Lamp> lamps ) {
    when( zone.getChildren() ).thenReturn( ( asList( lamps.toArray( new Lamp[ lamps.size() ] ) ) ) );
    when( zone.getChildren( LampDefinition.class ) ).thenReturn( lamps );
  }

  private static Room stubZone( RoomDefinition zoneDefinition ) {
    Room result = mock( Room.class );
    when( result.getDefinition() ).thenReturn( zoneDefinition );
    return result;
  }

  private static Lamp stubLamp( LampDefinition lampDefinition ) {
    Lamp result = mock( Lamp.class );
    when( result.getDefinition() ).thenReturn( lampDefinition );
    when( result.getOnOffStatus() ).thenReturn( OFF );
    when( result.toString() ).thenReturn( lampDefinition.toString() );
    doAnswer( invocation -> setOnOff( invocation, result ) )
      .when( result ).setOnOffStatus( any( OnOff.class ) );
    return result;
  }

  private static Object setOnOff( InvocationOnMock invocation, Lamp lamp ) {
    OnOff onOff = ( OnOff )invocation.getArguments()[ 0 ];
    when( lamp.getOnOffStatus() ).thenReturn( onOff );
    return onOff;
  }
}