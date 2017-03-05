package com.codeaffine.home.control.application.sence;

import java.util.concurrent.TimeUnit;

public interface FollowUpTimer {

  void schedule( long delay, TimeUnit unit, Runnable handler );
}