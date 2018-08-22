package de.androbin.noise;

import static de.androbin.lwjgl.util.BufferUtil.*;
import static de.androbin.opencl.CLBufferUtil.*;
import static org.lwjgl.opencl.CL10.*;
import de.androbin.opencl.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;
import org.lwjgl.opencl.*;

public class CLSimplexNoise extends CLNoise {
  public final CLExecutor executor;
  public CLMem perm;
  
  public CLSimplexNoise( final int n ) {
    this.executor = createExecutor( n );
  }
  
  @ Override
  public void cleanup() {
    clReleaseMemObject( perm );
    super.cleanup();
    executor.cleanup();
  }
  
  private static CLExecutor createExecutor( final int n ) {
    final String name = "simplex" + n + "d";
    
    try {
      return new CLExecutor( name, "-cl-fast-relaxed-math" );
    } catch ( final IOException e ) {
      e.printStackTrace();
      return null;
    }
  }
  
  private static CLMem createPerm( final IntUnaryOperator function ) {
    final int[] perm = new int[ 512 ];
    
    for ( int i = 0; i < 256; i++ ) {
      final int n = function.applyAsInt( i );
      perm[ i + 256 ] = n;
      perm[ i ] = n;
    }
    
    final IntBuffer buffer = wrapIntBuffer( perm );
    return createReadOnlyBuffer( buffer );
  }
  
  @ Override
  public void seed( final Random random ) {
    seed( i -> random.nextInt( 256 ) );
  }
  
  public void seed( final IntUnaryOperator function ) {
    if ( perm != null ) {
      clReleaseMemObject( perm );
    }
    
    perm = createPerm( function );
  }
}