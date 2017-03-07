package com.codeaffine.home.control.engine.adapter;

import static com.codeaffine.home.control.engine.adapter.ExecutorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.types.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.engine.adapter.ItemAdapter;
import com.codeaffine.home.control.engine.adapter.StateChangeAdapter;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.event.UpdateListener;
import com.codeaffine.home.control.type.OpenClosedType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StateChangeAdapterTest {

  private ChangeListener changeListener;
  private UpdateListener updateListener;
  private ItemAdapter itemAdapter;
  private SystemExecutorImpl executor;

  @Before
  public void setUp() {
    changeListener = mock( ChangeListener.class );
    updateListener = mock( UpdateListener.class );
    itemAdapter = stubItemAdapter( OpenClosedType.class );
    executor = stubInThreadExecutor();
  }

  @Test
  public void stateUpdated() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, updateListener, executor );

    adapter.stateUpdated( null, org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );

    ArgumentCaptor<UpdateEvent> evt = forClass( UpdateEvent.class );
    verify( updateListener ).itemUpdated( evt.capture() );
    assertThat( evt.getValue().getSource() ).isSameAs( itemAdapter );
    assertThat( evt.getValue().getUpdatedStatus().get() ).isSameAs( OpenClosedType.OPEN );
  }

  @Test
  public void stateUpdatedOnChangeListener() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, changeListener, executor );

    adapter.stateUpdated( null, org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );

    verify( changeListener, never() ).itemChanged( any( ChangeEvent.class ) );
    verify( updateListener, never() ).itemUpdated( any( UpdateEvent.class ) );
  }

  @Test
  public void stateUpdatedIfExecutorIsBlocked() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, updateListener, executor );
    blockExecutor( executor );

    adapter.stateUpdated( null, mock( State.class ) );

    verify( updateListener, never() ).itemUpdated( any( UpdateEvent.class ) );
  }

  @Test
  public void stateChanged() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, changeListener, executor );

    adapter.stateChanged( null,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );

    ArgumentCaptor<ChangeEvent> evt = forClass( ChangeEvent.class );
    verify( changeListener ).itemChanged( evt.capture() );
    assertThat( evt.getValue().getSource() ).isSameAs( itemAdapter );
    assertThat( evt.getValue().getOldStatus().get() ).isSameAs( OpenClosedType.OPEN );
    assertThat( evt.getValue().getNewStatus().get() ).isSameAs( OpenClosedType.CLOSED );
  }

  @Test
  public void stateChangedIfExecutorIsBlocked() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, changeListener, executor );
    blockExecutor( executor );

    adapter.stateChanged( mock( GenericItem.class ), mock( State.class ), mock( State.class ) );

    verify( changeListener, never() ).itemChanged( any( ChangeEvent.class ) );
    blockExecutor( executor );
  }

  @Test
  public void stateChangedOnUpdateListener() {
    StateChangeAdapter adapter = new StateChangeAdapter( itemAdapter, updateListener, executor );

    adapter.stateChanged( null,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN,
                          org.eclipse.smarthome.core.library.types.OpenClosedType.CLOSED );

    verify( changeListener, never() ).itemChanged( any( ChangeEvent.class ) );
    verify( updateListener, never() ).itemUpdated( any( UpdateEvent.class ) );
  }

  @Test
  public void getListener() {
    StateChangeAdapter changeAdapter = new StateChangeAdapter( itemAdapter, changeListener, executor );
    StateChangeAdapter updateAdapter = new StateChangeAdapter( itemAdapter, updateListener, executor );

    ChangeListener actualChangeListener = changeAdapter.getChangeListener();
    UpdateListener actualUpdateListener = updateAdapter.getUpdateListener();

    assertThat( actualChangeListener ).isSameAs( changeListener );
    assertThat( actualUpdateListener ).isSameAs( updateListener );
  }

  private static ItemAdapter stubItemAdapter( Class statusType ) {
    ItemAdapter result = mock( ItemAdapter.class );
    when( result.getStatusType() ).thenReturn( statusType );
    return result;
  }
}