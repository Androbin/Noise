package de.androbin.noise;

public interface Noise1D extends Noise {
  float[] noise1d( float[] result, int n, float scale, int sx, float dx );
}