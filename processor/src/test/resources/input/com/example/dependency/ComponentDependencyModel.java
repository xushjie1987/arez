package com.example.dependency;

import arez.annotations.ArezComponent;
import arez.annotations.Dependency;

@ArezComponent( allowEmpty = true )
public abstract class ComponentDependencyModel
{
  @ArezComponent( allowEmpty = true )
  static abstract class Foo
  {
  }

  @Dependency
  final Foo getFoo()
  {
    return null;
  }
}
