package org.realityforge.arez;

import org.realityforge.guiceyloops.shared.ValueUtil;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ComponentTest
  extends AbstractArezTest
{
  @Test
  public void basicOperation()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String type = ValueUtil.randomString();
    final String id = ValueUtil.randomString();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, type, id, name );
    assertEquals( component.getContext(), context );
    assertEquals( component.getType(), type );
    assertEquals( component.getId(), id );
    assertEquals( component.getName(), name );
    assertEquals( component.toString(), name );
  }

  @Test
  public void basicOperationIntegerId()
    throws Exception
  {
    final Integer id = ValueUtil.randomInt();
    final Component component = new Component( Arez.context(), ValueUtil.randomString(), id, ValueUtil.randomString() );
    assertEquals( component.getId(), id );
  }

  @Test
  public void basicOperationNullId()
    throws Exception
  {
    final Object id = null;
    final Component component = new Component( Arez.context(), ValueUtil.randomString(), id, ValueUtil.randomString() );
    assertEquals( component.getId(), id );
  }

  @Test
  public void noNameSuppliedWhenNamesDisabled()
    throws Exception
  {
    ArezTestUtil.disableNames();

    final ArezContext context = Arez.context();
    final String type = ValueUtil.randomString();
    final String id = ValueUtil.randomString();

    final Component component = new Component( context, type, id, null );
    assertEquals( component.getContext(), context );
    assertEquals( component.getType(), type );
    assertEquals( component.getId(), id );

    assertTrue( component.toString().startsWith( "org.realityforge.arez.Component@" ) );

    final IllegalStateException exception = expectThrows( IllegalStateException.class, component::getName );
    assertEquals( exception.getMessage(), "Component.getName() invoked when Arez.areNamesEnabled() is false" );
  }

  @Test
  public void nameSuppliedWhenNamesDisabled()
    throws Exception
  {
    ArezTestUtil.disableNames();

    final ArezContext context = Arez.context();
    final String type = ValueUtil.randomString();
    final String id = ValueUtil.randomString();
    final String name = ValueUtil.randomString();

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> new Component( context, type, id, name ) );

    assertEquals( exception.getMessage(),
                  "Component passed a name '" + name + "' but Arez.areNamesEnabled() is false" );
  }

  @Test
  public void observers()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observer observer1 = context.autorun( () -> {
    } );
    final Observer observer2 = context.autorun( () -> {
    } );

    assertEquals( component.getObservers().size(), 0 );

    component.addObserver( observer1 );

    assertEquals( component.getObservers().size(), 1 );
    assertEquals( component.getObservers().contains( observer1 ), true );

    component.addObserver( observer2 );

    assertEquals( component.getObservers().size(), 2 );
    assertEquals( component.getObservers().contains( observer1 ), true );
    assertEquals( component.getObservers().contains( observer2 ), true );

    component.removeObserver( observer1 );

    assertEquals( component.getObservers().size(), 1 );
    assertEquals( component.getObservers().contains( observer2 ), true );

    component.removeObserver( observer2 );

    assertEquals( component.getObservers().size(), 0 );
  }

  @Test
  public void addObserver_duplicate()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observer observer1 = context.autorun( () -> {
    } );

    component.addObserver( observer1 );

    assertEquals( component.getObservers().size(), 1 );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.addObserver( observer1 ) );
    assertEquals( exception.getMessage(), "Component.addObserver invoked on component '" + name +
                                          "' specifying observer named '" + observer1.getName() +
                                          "' when observer already exists for component." );

    assertEquals( component.getObservers().size(), 1 );
  }

  @Test
  public void removeObserver_noExist()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observer observer1 = context.autorun( () -> {
    } );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.removeObserver( observer1 ) );
    assertEquals( exception.getMessage(), "Component.removeObserver invoked on component '" + name +
                                          "' specifying observer named '" + observer1.getName() +
                                          "' when observer does not exist for component." );
  }

  @Test
  public void observables()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observable observable1 = context.createObservable();
    final Observable observable2 = context.createObservable();

    assertEquals( component.getObservables().size(), 0 );

    component.addObservable( observable1 );

    assertEquals( component.getObservables().size(), 1 );
    assertEquals( component.getObservables().contains( observable1 ), true );

    component.addObservable( observable2 );

    assertEquals( component.getObservables().size(), 2 );
    assertEquals( component.getObservables().contains( observable1 ), true );
    assertEquals( component.getObservables().contains( observable2 ), true );

    component.removeObservable( observable1 );

    assertEquals( component.getObservables().size(), 1 );
    assertEquals( component.getObservables().contains( observable2 ), true );

    component.removeObservable( observable2 );

    assertEquals( component.getObservables().size(), 0 );
  }

  @Test
  public void addObservable_duplicate()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observable observable1 = context.createObservable();

    component.addObservable( observable1 );

    assertEquals( component.getObservables().size(), 1 );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.addObservable( observable1 ) );
    assertEquals( exception.getMessage(), "Component.addObservable invoked on component '" + name +
                                          "' specifying observable named '" + observable1.getName() +
                                          "' when observable already exists for component." );

    assertEquals( component.getObservables().size(), 1 );
  }

  @Test
  public void removeObservable_noExist()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final Observable observable1 = context.createObservable();

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.removeObservable( observable1 ) );
    assertEquals( exception.getMessage(), "Component.removeObservable invoked on component '" + name +
                                          "' specifying observable named '" + observable1.getName() +
                                          "' when observable does not exist for component." );
  }

  @Test
  public void computedValues()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final ComputedValue computedValue1 = context.createComputedValue( () -> "" );
    final ComputedValue computedValue2 = context.createComputedValue( () -> "" );

    assertEquals( component.getComputedValues().size(), 0 );

    component.addComputedValue( computedValue1 );

    assertEquals( component.getComputedValues().size(), 1 );
    assertEquals( component.getComputedValues().contains( computedValue1 ), true );

    component.addComputedValue( computedValue2 );

    assertEquals( component.getComputedValues().size(), 2 );
    assertEquals( component.getComputedValues().contains( computedValue1 ), true );
    assertEquals( component.getComputedValues().contains( computedValue2 ), true );

    component.removeComputedValue( computedValue1 );

    assertEquals( component.getComputedValues().size(), 1 );
    assertEquals( component.getComputedValues().contains( computedValue2 ), true );

    component.removeComputedValue( computedValue2 );

    assertEquals( component.getComputedValues().size(), 0 );
  }

  @Test
  public void addComputedValue_duplicate()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final ComputedValue computedValue1 = context.createComputedValue( () -> "" );

    component.addComputedValue( computedValue1 );

    assertEquals( component.getComputedValues().size(), 1 );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.addComputedValue( computedValue1 ) );
    assertEquals( exception.getMessage(), "Component.addComputedValue invoked on component '" + name +
                                          "' specifying computedValue named '" + computedValue1.getName() +
                                          "' when computedValue already exists for component." );

    assertEquals( component.getComputedValues().size(), 1 );
  }

  @Test
  public void removeComputedValue_noExist()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = new Component( context, ValueUtil.randomString(), null, name );

    final ComputedValue computedValue1 = context.createComputedValue( () -> "" );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> component.removeComputedValue( computedValue1 ) );
    assertEquals( exception.getMessage(), "Component.removeComputedValue invoked on component '" + name +
                                          "' specifying computedValue named '" + computedValue1.getName() +
                                          "' when computedValue does not exist for component." );
  }

  @Test
  public void dispose()
    throws Exception
  {
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();

    final Component component = context.createComponent( ValueUtil.randomString(), null, name );

    final Observable observable1 = context.createObservable();
    final Observable observable2 = context.createObservable();
    final ComputedValue computedValue1 = context.createComputedValue( () -> "" );
    final ComputedValue computedValue2 = context.createComputedValue( () -> "" );
    final Observer observer1 = context.autorun( () -> {
    } );
    final Observer observer2 = context.autorun( () -> {
    } );

    component.addObservable( observable1 );
    component.addObservable( observable2 );
    component.addComputedValue( computedValue1 );
    component.addComputedValue( computedValue2 );
    component.addObserver( observer1 );
    component.addObserver( observer2 );

    assertEquals( component.getObservables().size(), 2 );
    assertEquals( component.getComputedValues().size(), 2 );
    assertEquals( component.getObservers().size(), 2 );

    assertFalse( Disposable.isDisposed( component ) );
    assertFalse( Disposable.isDisposed( observable1 ) );
    assertFalse( Disposable.isDisposed( observable2 ) );
    assertFalse( Disposable.isDisposed( observer1 ) );
    assertFalse( Disposable.isDisposed( observer2 ) );
    assertFalse( Disposable.isDisposed( computedValue1 ) );
    assertFalse( Disposable.isDisposed( computedValue2 ) );

    assertTrue( context.isComponentPresent( component.getType(), component.getId() ) );

    component.dispose();

    assertFalse( context.isComponentPresent( component.getType(), component.getId() ) );

    assertEquals( component.getObservables().size(), 0 );
    assertEquals( component.getComputedValues().size(), 0 );
    assertEquals( component.getObservers().size(), 0 );

    assertTrue( Disposable.isDisposed( component ) );
    assertTrue( Disposable.isDisposed( observable1 ) );
    assertTrue( Disposable.isDisposed( observable2 ) );
    assertTrue( Disposable.isDisposed( observer1 ) );
    assertTrue( Disposable.isDisposed( observer2 ) );
    assertTrue( Disposable.isDisposed( computedValue1 ) );
    assertTrue( Disposable.isDisposed( computedValue2 ) );
  }
}