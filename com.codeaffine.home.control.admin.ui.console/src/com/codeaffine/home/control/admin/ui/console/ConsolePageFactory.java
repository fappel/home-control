package com.codeaffine.home.control.admin.ui.console;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.api.PageFactory;

public class ConsolePageFactory implements PageFactory {

  @Override
  public Page create() {
    return new ConsolePage();
  }
}