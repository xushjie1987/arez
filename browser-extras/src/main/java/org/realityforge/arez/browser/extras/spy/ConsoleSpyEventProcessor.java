package org.realityforge.arez.browser.extras.spy;

import elemental2.dom.DomGlobal;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.realityforge.arez.Unsupported;
import org.realityforge.arez.extras.spy.AbstractSpyEventProcessor;
import org.realityforge.arez.extras.spy.SpyUtil;
import org.realityforge.arez.spy.ActionCompletedEvent;
import org.realityforge.arez.spy.ActionStartedEvent;
import org.realityforge.arez.spy.ComputeCompletedEvent;
import org.realityforge.arez.spy.ComputeStartedEvent;
import org.realityforge.arez.spy.ComputedValueActivatedEvent;
import org.realityforge.arez.spy.ComputedValueCreatedEvent;
import org.realityforge.arez.spy.ComputedValueDeactivatedEvent;
import org.realityforge.arez.spy.ComputedValueDisposedEvent;
import org.realityforge.arez.spy.ObservableChangedEvent;
import org.realityforge.arez.spy.ObservableCreatedEvent;
import org.realityforge.arez.spy.ObservableDisposedEvent;
import org.realityforge.arez.spy.ObserverCreatedEvent;
import org.realityforge.arez.spy.ObserverDisposedEvent;
import org.realityforge.arez.spy.ObserverErrorEvent;
import org.realityforge.arez.spy.ReactionCompletedEvent;
import org.realityforge.arez.spy.ReactionScheduledEvent;
import org.realityforge.arez.spy.ReactionStartedEvent;
import org.realityforge.arez.spy.TransactionCompletedEvent;
import org.realityforge.arez.spy.TransactionStartedEvent;

/**
 * A SpyEventHandler that prints spy events to the browser console.
 * The events are grouped according to their nesting levels and are colored to make them easy
 * to digest. This class is designed to be easy to sub-class.
 */
@Unsupported( "This class relies on unstable spy API and will likely evolve as the api evolves" )
public class ConsoleSpyEventProcessor
  extends AbstractSpyEventProcessor
{
  private static final String OBSERVABLE_COLOR = "color: #CF8A3B; font-weight: normal;";
  private static final String OBSERVER_COLOR = "color: #0FA13B; font-weight: normal;";
  private static final String REACTION_COLOR = "color: #10a210; font-weight: normal;";
  private static final String ACTION_COLOR = "color: #006AEB; font-weight: normal;";
  private static final String TRANSACTION_COLOR = "color: #A18008; font-weight: normal;";
  private static final String ERROR_COLOR = "color: #A10001; font-weight: normal;";

  /**
   * Create the processor.
   */
  public ConsoleSpyEventProcessor()
  {
    on( ObserverCreatedEvent.class, this::onObserverCreated );
    on( ObserverDisposedEvent.class, this::onObserverDisposed );
    on( ObserverErrorEvent.class, this::onObserverError );

    on( ObservableCreatedEvent.class, this::onObservableCreated );
    on( ObservableDisposedEvent.class, this::onObservableDisposed );
    on( ObservableChangedEvent.class, this::onObservableChanged );

    on( ComputedValueCreatedEvent.class, this::onComputedValueCreated );
    on( ComputedValueActivatedEvent.class, this::onComputedValueActivated );
    on( ComputeStartedEvent.class, this::onComputeStarted );
    on( ComputeCompletedEvent.class, this::onComputeCompleted );
    on( ComputedValueDeactivatedEvent.class, this::onComputedValueDeactivated );
    on( ComputedValueDisposedEvent.class, this::onComputedValueDisposed );

    on( ReactionScheduledEvent.class, this::onReactionScheduled );
    on( ReactionStartedEvent.class, this::onReactionStarted );
    on( ReactionCompletedEvent.class, this::onReactionCompleted );

    on( TransactionStartedEvent.class, this::TransactionStarted );
    on( TransactionCompletedEvent.class, this::onTransactionCompleted );

    on( ActionStartedEvent.class, this::onActionStarted );
    on( ActionCompletedEvent.class, this::onActionCompleted );
  }

  /**
   * Handle the ObserverCreatedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObserverCreated( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObserverCreatedEvent e )
  {
    log( d, "%cObserver Created " + e.getObserver().getName(), "color: #0FA13B" );
  }

  /**
   * Handle the ObserverDisposedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObserverDisposed( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObserverDisposedEvent e )
  {
    log( d, "%cObserver Disposed " + e.getObserver().getName(), OBSERVER_COLOR );
  }

  /**
   * Handle the ObserverErrorEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObserverError( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObserverErrorEvent e )
  {
    log( d,
         "%cObserver Error " + e.getObserver().getName() + " " + e.getError() + " " + e.getThrowable(),
         ERROR_COLOR );
  }

  /**
   * Handle the ObservableCreatedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObservableCreated( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObservableCreatedEvent e )
  {
    log( d, "%cObservable Created " + e.getObservable().getName(), OBSERVABLE_COLOR );
  }

  /**
   * Handle the ObservableDisposedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObservableDisposed( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObservableDisposedEvent e )
  {
    log( d, "%cObservable Disposed " + e.getObservable().getName(), OBSERVABLE_COLOR );
  }

  /**
   * Handle the ObservableChangedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onObservableChanged( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ObservableChangedEvent e )
  {
    log( d, "%cObservable Changed " + e.getObservable().getName(), OBSERVABLE_COLOR );
  }

  /**
   * Handle the ComputedValueCreatedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputedValueCreated( @Nonnull final SpyUtil.NestingDelta d,
                                         @Nonnull final ComputedValueCreatedEvent e )
  {
    log( d, "%cComputed Value Created " + e.getComputedValue().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ComputedValueActivatedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputedValueActivated( @Nonnull final SpyUtil.NestingDelta d,
                                           @Nonnull final ComputedValueActivatedEvent e )
  {
    log( d, "%cComputed Value Activate " + e.getComputedValue().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ComputeStartedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputeStarted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ComputeStartedEvent e )
  {
    log( d, "%cCompute Started " + e.getComputedValue().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ComputeCompletedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputeCompleted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ComputeCompletedEvent e )
  {
    log( d, "%cCompute Completed " + e.getComputedValue().getName() + " [" + e.getDuration() + "]", REACTION_COLOR );
  }

  /**
   * Handle the ComputedValueDeactivatedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputedValueDeactivated( @Nonnull final SpyUtil.NestingDelta d,
                                             @Nonnull final ComputedValueDeactivatedEvent e )
  {
    log( d, "%cComputed Value Deactivate " + e.getComputedValue().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ComputedValueDisposedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onComputedValueDisposed( @Nonnull final SpyUtil.NestingDelta d,
                                          @Nonnull final ComputedValueDisposedEvent e )
  {
    log( d, "%cComputed Value Disposed " + e.getComputedValue().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ReactionScheduledEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onReactionScheduled( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ReactionScheduledEvent e )
  {
    log( d, "%cReaction Scheduled " + e.getObserver().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ReactionStartedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onReactionStarted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ReactionStartedEvent e )
  {
    log( d, "%cReaction Started " + e.getObserver().getName(), REACTION_COLOR );
  }

  /**
   * Handle the ReactionCompletedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onReactionCompleted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ReactionCompletedEvent e )
  {
    log( d,
         "%cReaction Completed " + e.getObserver().getName() + " [" + e.getDuration() + "]",
         REACTION_COLOR );
  }

  /**
   * Handle the TransactionStartedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void TransactionStarted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final TransactionStartedEvent e )
  {
    log( d, "%cTransaction Started " +
            e.getName() +
            " Mutation=" +
            e.isMutation() +
            " Tracker=" +
            ( e.getTracker() == null ? null : e.getTracker().getName() ),
         TRANSACTION_COLOR );
  }

  /**
   * Handle the TransactionCompletedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onTransactionCompleted( @Nonnull final SpyUtil.NestingDelta d,
                                         @Nonnull final TransactionCompletedEvent e )
  {
    log( d, "%cTransaction Completed " +
            e.getName() +
            " Mutation=" +
            e.isMutation() +
            " Tracker=" +
            ( e.getTracker() == null ? null : e.getTracker().getName() ) +
            " [" +
            e.getDuration() +
            "]",
         TRANSACTION_COLOR );
  }

  /**
   * Handle the ActionStartedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onActionStarted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ActionStartedEvent e )
  {
    final String message =
      ( e.isTracked() ? "Tracked " : "" ) + "Action Started " + e.getName() +
      "(" + Arrays.toString( e.getParameters() ) + ")";
    log( d, "%c" + message, ACTION_COLOR );
  }

  /**
   * Handle the ActionCompletedEvent.
   *
   * @param d the change in nesting level.
   * @param e the event.
   */
  protected void onActionCompleted( @Nonnull final SpyUtil.NestingDelta d, @Nonnull final ActionCompletedEvent e )
  {
    final String message = ( e.isTracked() ? "Tracked " : "" ) + "Action Completed " +
                           e.getName() +
                           "(" +
                           Arrays.toString( e.getParameters() ) +
                           ")" + ( e.isExpectsResult() && null == e.getThrowable() ? " = " + e.getResult() : "" ) +
                           ( null != e.getThrowable() ? "threw " + e.getThrowable() : "" ) +
                           " Duration [" +
                           e.getDuration() +
                           "]";
    log( d, "%c" + message, ACTION_COLOR );
  }

  /**
   * Log specified message with parameters
   *
   * @param delta   the nesting delta.
   * @param message the message.
   * @param styling the styling parameter. It is assumed that the message has a %c somewhere in it to identify the start of the styling.
   */
  protected void log( @Nonnull final SpyUtil.NestingDelta delta,
                      @Nonnull final String message,
                      @Nonnull final String styling )
  {
    if ( SpyUtil.NestingDelta.INCREASE == delta )
    {
      DomGlobal.console.groupCollapsed( message, styling );
    }
    else if ( SpyUtil.NestingDelta.DECREASE == delta )
    {
      DomGlobal.console.log( message, styling );
      DomGlobal.console.groupEnd();
    }
    else
    {
      DomGlobal.console.log( message, styling );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleUnhandledEvent( @Nonnull final Object event )
  {
    DomGlobal.console.log( event );
  }
}