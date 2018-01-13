package arez.component;

import arez.Arez;
import arez.ArezContext;
import arez.ArezTestUtil;
import arez.ComputedValue;
import arez.Observer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.realityforge.guiceyloops.shared.ValueUtil;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@SuppressWarnings( "unchecked" )
public class MemoizeCacheTest
  extends AbstractArezComponentTest
{
  @Test
  public void basicOperation()
  {
    final AtomicInteger callCount = new AtomicInteger();
    final MemoizeCache.Function<String> function = args -> {
      callCount.incrementAndGet();
      return args[ 0 ] + "." + args[ 1 ];
    };
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();
    final MemoizeCache<String> cache =
      new MemoizeCache<>( context, name, function, 2 );

    assertEquals( cache.isDisposed(), false );
    assertEquals( cache.getNextIndex(), 0 );

    final Observer observer1 = context.autorun( () -> assertEquals( cache.get( "a", "b" ), "a.b" ) );
    assertEquals( callCount.get(), 1 );
    assertEquals( cache.getNextIndex(), 1 );
    assertEquals( cache.getCache().size(), 1 );
    assertEquals( ( (Map) cache.getCache().get( "a" ) ).size(), 1 );
    final ComputedValue<String> computedValue1 =
      (ComputedValue<String>) ( (Map) cache.getCache().get( "a" ) ).get( "b" );
    assertNotNull( computedValue1 );
    assertEquals( context.safeAction( computedValue1::get ), "a.b" );
    final Observer observer2 = context.autorun( () -> assertEquals( cache.get( "a", "b" ), "a.b" ) );
    assertEquals( callCount.get(), 1 );
    assertEquals( cache.getNextIndex(), 1 );
    assertEquals( cache.getCache().size(), 1 );
    assertEquals( ( (Map) cache.getCache().get( "a" ) ).size(), 1 );
    final Observer observer3 = context.autorun( () -> assertEquals( cache.get( "a", "c" ), "a.c" ) );
    assertEquals( callCount.get(), 2 );
    assertEquals( cache.getNextIndex(), 2 );
    assertEquals( cache.getCache().size(), 1 );
    assertEquals( ( (Map) cache.getCache().get( "a" ) ).size(), 2 );
    final ComputedValue<String> computedValue2 =
      (ComputedValue<String>) ( (Map) cache.getCache().get( "a" ) ).get( "c" );
    assertNotNull( computedValue2 );
    assertEquals( context.safeAction( computedValue2::get ), "a.c" );

    observer1.dispose();
    assertEquals( callCount.get(), 2 );
    assertEquals( cache.getNextIndex(), 2 );
    assertEquals( cache.getCache().size(), 1 );
    assertEquals( computedValue1.isDisposed(), false );
    assertEquals( computedValue2.isDisposed(), false );
    assertEquals( ( (Map) cache.getCache().get( "a" ) ).size(), 2 );

    observer2.dispose();
    assertEquals( callCount.get(), 2 );
    assertEquals( cache.getNextIndex(), 2 );
    assertEquals( cache.getCache().size(), 1 );
    assertEquals( computedValue1.isDisposed(), true );
    assertEquals( computedValue2.isDisposed(), false );
    assertEquals( ( (Map) cache.getCache().get( "a" ) ).size(), 1 );

    observer3.dispose();
    assertEquals( callCount.get(), 2 );
    assertEquals( cache.getNextIndex(), 2 );
    assertEquals( computedValue1.isDisposed(), true );
    assertEquals( computedValue2.isDisposed(), true );
    assertEquals( cache.getCache().size(), 0 );
  }

  @Test
  public void disposingCacheClearsOutComputedValues()
  {
    final AtomicInteger callCount = new AtomicInteger();
    final MemoizeCache.Function<String> function = args -> {
      callCount.incrementAndGet();
      return args[ 0 ] + "." + args[ 1 ];
    };
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();
    final MemoizeCache<String> cache =
      new MemoizeCache<>( context, name, function, 2 );

    assertEquals( cache.isDisposed(), false );
    assertEquals( cache.getNextIndex(), 0 );

    context.autorun( () -> assertEquals( cache.get( "a", "b" ), "a.b" ) );
    assertEquals( callCount.get(), 1 );
    assertEquals( cache.getNextIndex(), 1 );
    assertEquals( cache.getCache().size(), 1 );

    final ComputedValue<String> computedValue1 =
      (ComputedValue<String>) ( (Map) cache.getCache().get( "a" ) ).get( "b" );
    assertNotNull( computedValue1 );

    assertEquals( computedValue1.isDisposed(), false );

    cache.dispose();

    assertEquals( callCount.get(), 1 );
    assertEquals( cache.getNextIndex(), 1 );
    assertEquals( cache.getCache().size(), 0 );
    assertEquals( computedValue1.isDisposed(), true );
  }

  @Test
  public void basicOperation_inAction()
  {
    final AtomicInteger callCount = new AtomicInteger();
    final MemoizeCache.Function<String> function = args -> {
      callCount.incrementAndGet();
      return args[ 0 ] + "." + args[ 1 ];
    };
    final ArezContext context = Arez.context();
    final String name = ValueUtil.randomString();
    final MemoizeCache<String> cache =
      new MemoizeCache<>( context, name, function, 2 );

    assertEquals( cache.getNextIndex(), 0 );

    assertEquals( context.safeAction( () -> cache.get( "a", "b" ) ), "a.b" );

    assertEquals( callCount.get(), 1 );
    assertEquals( cache.getNextIndex(), 1 );

    // Not observed so should be cleaned up immediately
    assertEquals( cache.getCache().size(), 0 );
  }

  @Test
  public void disposeComputedValue_passedBadArgCounts()
  {
    final MemoizeCache<String> cache =
      new MemoizeCache<>( Arez.context(), ValueUtil.randomString(), args -> args[ 0 ] + "." + args[ 1 ], 2 );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> cache.disposeComputedValue( "a" ) );

    assertEquals( exception.getMessage(),
                  "MemoizeCache.disposeComputedValue called with 1 argument but expected 2 arguments." );
  }

  @Test
  public void get_passedBadArgCounts()
  {
    final MemoizeCache<String> cache =
      new MemoizeCache<>( Arez.context(), ValueUtil.randomString(), args -> args[ 0 ] + "." + args[ 1 ], 2 );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> cache.get( "a" ) );

    assertEquals( exception.getMessage(),
                  "MemoizeCache.getComputedValue called with 1 arguments but expected 2 arguments." );
  }

  @Test
  public void get_invokedOnDisposed()
  {
    final MemoizeCache<String> cache =
      new MemoizeCache<>( Arez.context(), "X", args -> args[ 0 ] + "." + args[ 1 ], 2 );

    cache.dispose();

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> cache.get( "a", "b" ) );

    assertEquals( exception.getMessage(), "MemoizeCache named 'X' had get() invoked when disposed." );
  }

  @Test
  public void constructorPassedName_whenNamesDisabled()
  {
    ArezTestUtil.disableNames();

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> new MemoizeCache<>( Arez.context(), "X", args -> args[ 0 ], 1 ) );

    assertEquals( exception.getMessage(),
                  "MemoizeCache passed a name 'X' but Arez.areNamesEnabled() is false" );
  }

  @Test
  public void constructorPassedBadArgCount()
  {
    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> new MemoizeCache<>( Arez.context(), "X", args -> args[ 0 ], 0 ) );

    assertEquals( exception.getMessage(),
                  "MemoizeCache constructed with invalid argCount: 0. Expected positive value." );
  }
}