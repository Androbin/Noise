package de.androbin.noise;

public interface Noise4D extends Noise, Noise3D {
  @ Override
  default float[] noise3d( final float[] result, final int n, final float scale,
      final int sx, final float dx, final int sy, final float dy, final int sz, final float dz ) {
    return noise4d( result, n, scale, sx, dx, sy, dy, sz, dz, 1, 0f );
  }
  
  float[] noise4d( float[] result, int n, float scale,
      int sx, float dx, int sy, float dy, int sz, float dz, int sw, float dw );
}