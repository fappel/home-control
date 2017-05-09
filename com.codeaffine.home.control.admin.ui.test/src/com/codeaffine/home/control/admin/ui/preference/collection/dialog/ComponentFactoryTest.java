package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.*;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.CellEditorControlUtil.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.swt.SWT.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;
import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class ComponentFactoryTest {

  private AttributeInfo attributeInfo;
  private ComponentFactory factory;

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  @Before
  public void setUp() {
    attributeInfo = mock( AttributeInfo.class );
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    factory = new ComponentFactory( new CollectionValue( objectInfo, attributeInfo, new HashMap<>() ) );
  }

  @Test
  public void createAdditionalInfoKeys() {
    Collection<String> actual = factory.createAdditionalInfoKeys();

    assertThat( actual ).containsExactly( ADDITION_INFO_VALUE );
  }

  @Test
  public void createAdditionalInfoKeysForMaps() {
    stubAttributeInfo( asList( String.class, String.class ) );

    Collection<String> actual = factory.createAdditionalInfoKeys();

    assertThat( actual ).containsExactly( ADDITION_INFO_KEY, ADDITION_INFO_VALUE );
  }

  @Test
  @SuppressWarnings( "rawtypes" )
  public void createElementGroupContent() {
    stubAttributeInfo( asList( String.class, Boolean.class ) );
    Shell parent = displayHelper.createShell();
    parent.open();

    List<Control> actual = factory.createElementGroupContent( parent );

    assertThat( actual.stream().map( control -> ( Class )control.getClass() ).collect( toList() ) )
      .containsExactly( Text.class, CCombo.class );
    assertThat( actual.stream().map( control -> getElementPartKey( control ) ).collect( toList() ) )
      .containsExactly( ADDITION_INFO_KEY, ADDITION_INFO_VALUE );
    assertThat( actual )
      .allMatch( control -> hasNoEventHandler( control, FocusOut, DefaultSelection, KeyDown, Traverse ) )
      .allMatch( control -> control.isVisible() )
      .allMatch( control -> getAttributeDescriptor( control ) != null )
      .allMatch( control -> getCellEditor( control ) != null );
  }

  private static boolean hasNoEventHandler( Control control, int ... eventTypes ) {
    return IntStream.of( eventTypes ).allMatch( eventType -> control.getListeners( eventType ).length == 0 );
  }

  private void stubAttributeInfo( List<Class<?>> genericTypeInfo ) {
    when( attributeInfo.getGenericTypeParametersOfAttributeType() ).thenReturn( genericTypeInfo );
  }
}