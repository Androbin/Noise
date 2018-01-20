package de.androbin.noise;

public interface Noise2D extends Noise1D {
  @ Override
  default float noise( final float x ) {
    return noise( x, 0f );
  }
  
  float noise( float x, float y );
}