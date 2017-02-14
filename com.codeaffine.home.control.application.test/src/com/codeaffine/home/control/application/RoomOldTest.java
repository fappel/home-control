package com.codeaffine.home.control.application;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.item.ContactItem;

@RunWith(Parameterized.class)
public class RoomOldTest {

  @Parameter
  public RoomOld room;

  @Parameters
  public static Collection<RoomOld> data() {
    return asList( RoomOld.values() );
  }

  @Test
  public void getLabel() {
    assertThat( room.getLabel() ).isNotNull();
  }

  @Test
  public void getVariblePrefix() {
    assertThat( room.getVariablePrefix() ).isNotNull();
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void registerSensorItems() {
    ContactItem contact = mock( ContactItem.class );
    ArgumentCaptor<ChangeListener> captor = forClass( ChangeListener.class );

    room.registerSensorItems( contact );

    verify( contact ).addChangeListener( captor.capture() );
    assertThat( captor.getValue() ).isNotNull();
  }
}