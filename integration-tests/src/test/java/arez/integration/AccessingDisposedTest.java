package arez.integration;

import arez.Disposable;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class AccessingDisposedTest
{
  @ArezComponent
  static class TestComponent
  {
    int invokeCount;

    @Action
    void myAction()
    {
      invokeCount++;
    }
  }

  @ArezComponent( nameIncludesId = false )
  static class TestSingletonComponent
  {
    int invokeCount;

    @Action
    void myAction()
    {
      invokeCount++;
    }
  }

  @Test
  public void accessingDisposedComponentResultsInError()
  {
    final TestComponent component = new AccessingDisposedTest_Arez_TestComponent();

    assertEquals( component.invokeCount, 0 );

    component.myAction();

    assertEquals( component.invokeCount, 1 );

    Disposable.dispose( component );

    assertTrue( Disposable.isDisposed( component ) );
    final IllegalStateException exception = expectThrows( IllegalStateException.class, component::myAction );

    assertEquals( exception.getMessage(), "Method invoked on invalid component 'TestComponent.0'" );
  }

  @Test
  public void accessingDisposedSingletonComponentResultsInError()
  {
    final TestSingletonComponent component = new AccessingDisposedTest_Arez_TestSingletonComponent();

    assertEquals( component.invokeCount, 0 );

    component.myAction();

    assertEquals( component.invokeCount, 1 );

    Disposable.dispose( component );

    assertTrue( Disposable.isDisposed( component ) );
    final IllegalStateException exception = expectThrows( IllegalStateException.class, component::myAction );

    assertEquals( exception.getMessage(), "Method invoked on invalid component 'TestSingletonComponent'" );
  }
}