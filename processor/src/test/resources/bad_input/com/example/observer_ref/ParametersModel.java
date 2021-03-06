package com.example.observer_ref;

import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.Autorun;
import arez.annotations.ObserverRef;

@ArezComponent
public abstract class ParametersModel
{
  @Autorun
  protected void doStuff()
  {
  }

  @ObserverRef
  abstract Observer getDoStuffObserver( int i );
}
