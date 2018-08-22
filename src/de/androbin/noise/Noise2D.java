package de.androbin.noise;

public interface Noise2D extends Noise, Noise1D {
  @ Override
  default float[] noise1d( final float[] result, final int n, final float scale,
      final int sx, final float dx ) {
    return noise2d( result, n, scale, sx, dx, 1, 0f );
  }
  
  float[] noise2d( float[] result, int n, float scale, int sx, float dx, int sy, float dy );
}