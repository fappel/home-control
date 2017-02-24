package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.internal.bulb.BulbFactory.*;
import static java.util.Optional.empty;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.AdjustableItem;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;

public class BulbItemHelper {

  public static Registry stubRegistry(
    SwitchItem onOffItem, DimmerItem brightnessItem, DimmerItem colorTemperatureItem )
  {
    Registry result = mock( Registry.class );
    when( result.getItem( startsWith( PREFIX_SWITCH ), eq( SwitchItem.class ) ) ).thenReturn( onOffItem );
    when( result.getItem( startsWith( PREFIX_BRIGHTNESS ), eq( DimmerItem.class ) ) ).thenReturn( brightnessItem );
    when( result.getItem( startsWith( PREFIX_COLOR_TEMPERATURE ), eq( DimmerItem.class ) ) )
    .thenReturn( colorTemperatureItem );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T extends AdjustableItem> T stubItem( Class<T> itemType ) {
    T result = mock( itemType );
    when( result.getStatus() ).thenReturn( empty() );
    doAnswer( invocation -> stubStatus( invocation, result ) ).when( result ).updateStatus( any( Status.class ) );
    return result;
  }

  @SuppressWarnings({ "rawtypes" })
  private static <T extends AdjustableItem> Object stubStatus( InvocationOnMock invocation, T result ) {
    Status status = ( Status )invocation.getArguments()[ 0 ];
    when( result.getStatus() ).thenReturn( Optional.of( status ) );
    return null;
  }
}