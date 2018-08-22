package de.androbin.noise;

import org.lwjgl.opencl.*;

public final class CLSimplexNoise2D extends CLSimplexNoise implements Noise2D {
  public CLSimplexNoise2D() {
    super( 2 );
  }
  
  @ Override
  public float[] noise2d( final float[] result, final int n, final float scale,
      final int sx, final float dx, final int sy, final float dy ) {
    final CLKernel kernel = executor.kernel;
    kernel.setArg( 1, perm );
    kernel.setArg( 2, n );
    kernel.setArg( 3, scale );
    kernel.setArg( 4, dx );
    kernel.setArg( 5, dy );
    
    return noise( executor, result, sx, sy );
  }
}