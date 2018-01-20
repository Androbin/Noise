package de.androbin.noise;

public interface Noise4D extends Noise3D {
  @ Override
  default float noise( final float x, final float y, float z ) {
    return noise( x, y, z, 0f );
  }
  
  float noise( float x, float y, float z, float w );
}