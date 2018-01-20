package de.androbin.noise;

public interface Noise3D extends Noise2D {
  @ Override
  default float noise( final float x, final float y ) {
    return noise( x, y, 0f );
  }
  
  float noise( float x, float y, float z );
}