package arez.integration.computed_value;

import arez.Arez;
import arez.ArezContext;
import arez.annotations.ArezComponent;
import arez.annotations.Computed;
import arez.annotations.Observable;
import arez.integration.AbstractArezIntegrationTest;
import arez.integration.util.SpyEventRecorder;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;

public class ComputedKeepAliveTest
  extends AbstractArezIntegrationTest
{
  @Test
  public void scenario()
    throws Throwable
  {
    final ArezContext context = Arez.context();

    final SpyEventRecorder recorder = SpyEventRecorder.beginRecording();

    final PersonModel person = PersonModel.create( "Bill", "Smith" );

    context.action( "First Name Update", true, () -> person.setFirstName( "Fred" ) );
    context.action( "Last Name Update", true, () -> person.setLastName( "Donaldo" ) );

    assertMatchesFixture( recorder );
  }

  @SuppressWarnings( "WeakerAccess" )
  @ArezComponent
  public static abstract class PersonModel
  {
    @Nonnull
    private String _firstName;
    @Nonnull
    private String _lastName;

    @Nonnull
    public static PersonModel create( @Nonnull final String firstName, @Nonnull final String lastName )
    {
      return new ComputedKeepAliveTest_Arez_PersonModel( firstName, lastName );
    }

    PersonModel( @Nonnull final String firstName, @Nonnull final String lastName )
    {
      _firstName = firstName;
      _lastName = lastName;
    }

    @Observable
    @Nonnull
    public String getFirstName()
    {
      return _firstName;
    }

    public void setFirstName( @Nonnull final String firstName )
    {
      _firstName = firstName;
    }

    @Observable
    @Nonnull
    public String getLastName()
    {
      return _lastName;
    }

    public void setLastName( @Nonnull final String lastName )
    {
      _lastName = lastName;
    }

    @Computed( keepAlive = true )
    @Nonnull
    public String getFullName()
    {
      return getFirstName() + " " + getLastName();
    }
  }
}
