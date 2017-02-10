package com.codeaffine.home.control.internal.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.type.OpenClosedType;

@SuppressWarnings({ "rawtypes","unchecked" })
public class ChangeEventImplTest {

  private ChangeEventImpl changeEvent;
  private ItemAdapter adapter;

  @Before
  public void setUp() {
    adapter = mock( ItemAdapter.class );
    when( adapter.getStatusType() ).thenReturn( OpenClosedType.class );
    changeEvent = new ChangeEventImpl<>( adapter,
                                         org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN,
                                         org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );
  }

  @Test
  public void getStatus() {
    Item actual = changeEvent.getSource();

    assertThat( actual ).isSameAs( adapter );
  }

  @Test
  public void getOldStatus() {
    Optional actual = changeEvent.getOldStatus();

    assertThat( actual ).hasValue( OpenClosedType.OPEN );
  }

  @Test
  public void getNewStatus() {
    Optional actual = changeEvent.getNewStatus();

    assertThat( actual ).hasValue( OpenClosedType.CLOSED );
  }
}