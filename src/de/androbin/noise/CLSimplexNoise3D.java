package de.androbin.noise;

import org.lwjgl.opencl.*;

public final class CLSimplexNoise3D extends CLSimplexNoise implements Noise3D {
  public CLSimplexNoise3D() {
    super( 3 );
  }
  
  @ Override
  public float[] noise3d( final float[] result, final int n, final float scale,
      final int sx, final float dx, final int sy, final float dy, final int sz, final float dz ) {
    final CLKernel kernel = executor.kernel;
    kernel.setArg( 1, perm );
    kernel.setArg( 2, n );
    kernel.setArg( 3, scale );
    kernel.setArg( 4, dx );
    kernel.setArg( 5, dy );
    kernel.setArg( 6, dz );
    
    return noise( executor, result, sx, sy, sz );
  }
}