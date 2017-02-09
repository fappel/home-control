package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.adapter.ExecutorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.types.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.ItemStateChangeListener;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.type.OpenClosedType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StateChangeAdapterTest {

  private ItemStateChangeListener listener;
  private StateChangeAdapter adapter;
  private ItemAdapter itemAdapter;
  private SystemExecutor executor;

  @Before
  public void setUp() {
    listener = mock( ItemStateChangeListener.class );
    itemAdapter = stubItemAdapter( OpenClosedType.class );
    executor = stubInThreadExecutor();
    adapter = new StateChangeAdapter( itemAdapter, listener, executor );
  }

  @Test
  public void stateUpdated() {
    adapter.stateUpdated( null, org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );

    ArgumentCaptor<ItemAdapter> itemCaptor = forClass( ItemAdapter.class );
    ArgumentCaptor<Optional> stateCaptor = forClass( Optional.class );
    verify( listener ).stateUpdated( itemCaptor.capture(), stateCaptor.capture() );
    assertThat( itemCaptor.getValue() ).isSameAs( itemAdapter );
    assertThat( stateCaptor.getValue().get() ).isSameAs( OpenClosedType.OPEN );
  }

  @Test
  public void stateUpdatedIfExecutorIsBlocked() {
    blockExecutor( executor );

    adapter.stateUpdated( null, mock( State.class ) );

    verify( listener, never() ).stateUpdated( eq( itemAdapter ), any( Optional.class ) );
  }

  @Test
  public void stateChanged() {
    adapter.stateChanged( null,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );

    ArgumentCaptor<ItemAdapter> itemCaptor = forClass( ItemAdapter.class );
    ArgumentCaptor<Optional> oldStateCaptor = forClass( Optional.class );
    ArgumentCaptor<Optional> newStateCaptor = forClass( Optional.class );
    verify( listener ).stateChanged( itemCaptor.capture(), oldStateCaptor.capture(), newStateCaptor.capture() );
    assertThat( itemCaptor.getValue() ).isSameAs( itemAdapter );
    assertThat( oldStateCaptor.getValue().get() ).isSameAs( OpenClosedType.OPEN );
    assertThat( newStateCaptor.getValue().get() ).isSameAs( OpenClosedType.CLOSED );
  }

  @Test
  public void stateChangedIfExecutorIsBlocked() {
    blockExecutor( executor );

    adapter.stateChanged( mock( GenericItem.class ), mock( State.class ), mock( State.class ) );

    verify( listener, never() )
      .stateChanged( eq( itemAdapter ), any( Optional.class ), any( Optional.class ) );
  }

  @Test
  public void getListener() {
    ItemStateChangeListener actual = adapter.getListener();

    assertThat( actual ).isSameAs( listener );
  }

  private static ItemAdapter stubItemAdapter( Class statusType ) {
    ItemAdapter result = mock( ItemAdapter.class );
    when( result.getStatusType() ).thenReturn( statusType );
    return result;
  }
}