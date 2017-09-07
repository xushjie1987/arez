package org.realityforge.arez.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;
import static com.google.common.truth.Truth.assert_;

abstract class AbstractArezProcessorTest
{
  void assertSuccessfulCompile( @Nonnull final String classname )
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    final StringBuilder expected = new StringBuilder();
    input.append( "input" );
    expected.append( "expected" );
    for ( int i = 0; i < elements.length; i++ )
    {
      input.append( '/' );
      input.append( elements[ i ] );
      expected.append( '/' );
      if ( i == elements.length - 1 )
      {
        expected.append( "Arez_" );
      }
      expected.append( elements[ i ] );
    }
    input.append( ".java" );
    expected.append( ".java" );
    assertSuccessfulCompile( input.toString(), expected.toString() );
  }

  void assertSuccessfulCompile( @Nonnull final String inputResource, @Nonnull final String expectedOutputResource )
  {
    final JavaFileObject source = JavaFileObjects.forResource( inputResource );
    assert_().about( JavaSourceSubjectFactory.javaSource() ).
      that( source ).
      processedWith( new ArezProcessor() ).
      compilesWithoutError().
      and().generatesSources( JavaFileObjects.forResource( expectedOutputResource ) );
  }

  void assertFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    input.append( "bad_input" );
    for ( final String element : elements )
    {
      input.append( '/' );
      input.append( element );
    }
    input.append( ".java" );
    assertFailedCompileResource( input.toString(), errorMessageFragment );
  }

  private void assertFailedCompileResource( @Nonnull final String inputResource,
                                            @Nonnull final String errorMessageFragment )
  {
    final JavaFileObject source = JavaFileObjects.forResource( inputResource );
    assert_().about( JavaSourceSubjectFactory.javaSource() ).
      that( source ).
      processedWith( new ArezProcessor() ).
      failsToCompile().
      withErrorContaining( errorMessageFragment );
  }
}
