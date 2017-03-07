package com.codeaffine.home.control.engine.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.engine.adapter.ItemAdapter;
import com.codeaffine.home.control.engine.adapter.UpdateEventImpl;
import com.codeaffine.home.control.type.OpenClosedType;

@SuppressWarnings({ "rawtypes","unchecked" })
public class UpdateEventImplTest {

  private UpdateEventImpl updateEvent;
  private ItemAdapter adapter;

  @Before
  public void setUp() {
    adapter = mock( ItemAdapter.class );
    when( adapter.getStatusType() ).thenReturn( OpenClosedType.class );
    updateEvent = new UpdateEventImpl<>( adapter, org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );
  }

  @Test
  public void getStatus() {
    Item actual = updateEvent.getSource();

    assertThat( actual ).isSameAs( adapter );
  }

  @Test
  public void getUpdatedStatus() {
    Optional actual = updateEvent.getUpdatedStatus();

    assertThat( actual ).hasValue( OpenClosedType.OPEN );
  }
}