package de.androbin.noise;

public interface Noise3D extends Noise, Noise2D {
  @ Override
  default float[] noise2d( final float[] result, final int n, final float scale,
      final int sx, final float dx, final int sy, final float dy ) {
    return noise3d( result, n, scale, sx, dx, sy, dy, 1, 0f );
  }
  
  float[] noise3d( float[] result, int n, float scale,
      int sx, float x, int sy, float y, int sz, float z );
}