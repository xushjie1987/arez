package org.realityforge.arez.integration;

import org.realityforge.arez.Arez;
import org.realityforge.guiceyloops.shared.ValueUtil;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@SuppressWarnings( "ResultOfMethodCallIgnored" )
public class ObserveRequireActionTest
{
  @Test
  public void accessingObservableOutsideTransactionShouldThrowException()
  {
    final CodeModel component = CodeModel.create( ValueUtil.randomString(), ValueUtil.randomString() );
    assertThrows( component::getName );

    Arez.context().safeAction( component::getName );
  }

  @Test
  public void mutatingObservableOutsideTransactionShouldThrowException()
  {
    final CodeModel component = CodeModel.create( ValueUtil.randomString(), ValueUtil.randomString() );
    assertThrows( () -> component.setName( "X" ) );

    Arez.context().safeAction( () -> component.setName( "X" ) );
  }

  @Test
  public void accessingComputedOutsideTransactionShouldThrowException()
  {
    final CodeModel component = CodeModel.create( ValueUtil.randomString(), ValueUtil.randomString() );
    assertThrows( component::getQualifiedName );

    Arez.context().safeAction( component::getQualifiedName );
  }
}
