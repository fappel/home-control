package com.codeaffine.home.control.admin.ui.preference.source;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.source.ValuePropertySource;

public class ValuePropertySourceTest {

  private static final String VALUE = "Value";

  private ValuePropertySource propertySource;

  @Before
  public void setUp() {
    propertySource = new ValuePropertySource( VALUE );
  }

  @Test
  public void getPropertyDescriptors() {
    IPropertyDescriptor[] actual = propertySource.getPropertyDescriptors();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getEditableValue() {
    Object actual = propertySource.getEditableValue();

    assertThat( actual ).isSameAs( VALUE );
  }
}