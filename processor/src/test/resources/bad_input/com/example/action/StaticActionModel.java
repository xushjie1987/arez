package com.example.action;

import arez.annotations.Action;
import arez.annotations.ArezComponent;

@ArezComponent
public abstract class StaticActionModel
{
  @Action
  static void setField()
  {
  }
}
