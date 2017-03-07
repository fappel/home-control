package com.codeaffine.home.control.status;

import java.util.concurrent.TimeUnit;

public interface FollowUpTimer {

  void schedule( long delay, TimeUnit unit, Runnable handler );
}