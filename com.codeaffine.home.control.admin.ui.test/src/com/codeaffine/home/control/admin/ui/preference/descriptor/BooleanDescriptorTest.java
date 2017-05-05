package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.ComboBoxPropertyDescriptorHelper.getSelectionValues;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;

public class BooleanDescriptorTest {

  private BooleanDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new BooleanDescriptor( stubAttributeInfo() );
  }

  @Test
  public void createPropertyDescriptor() {
    IPropertyDescriptor actual = descriptor.createPropertyDescriptor();

    assertThat( actual )
      .matches( descriptor -> getSelectionValues( descriptor ).equals( BooleanDescriptor.SELECTION_LABELS ) )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToLabelOfTrue() {
    Object actual = descriptor.convertToLabel( Boolean.TRUE );

    assertThat( actual ).isEqualTo( BooleanDescriptor.LABEL_INDEX_TRUE );
  }

  @Test
  public void convertToLabelOfFalse() {
    Object actual = descriptor.convertToLabel( Boolean.FALSE );

    assertThat( actual ).isEqualTo( BooleanDescriptor.LABEL_INDEX_FALSE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    descriptor.convertToLabel( null );
  }

  @Test
    public void convertToValueOfFalseSelection() {
      Object actual = descriptor.convertToValue( Integer.valueOf( BooleanDescriptor.LABEL_INDEX_FALSE ) );

      assertThat( actual ).isSameAs( Boolean.FALSE );
    }

  @Test
    public void convertToValueOfTrueSelection() {
      Object actual = descriptor.convertToValue( Integer.valueOf( BooleanDescriptor.LABEL_INDEX_TRUE ) );

      assertThat( actual ).isSameAs( Boolean.TRUE );
    }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeWithNullAsAttributeValueLabelArgument() {
    descriptor.convertToValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new BooleanDescriptor( null );
  }
}