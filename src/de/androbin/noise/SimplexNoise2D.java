package de.androbin.noise;

import static de.androbin.math.util.floats.FloatMathUtil.*;

public class SimplexNoise2D extends SimplexNoise implements Noise2D {
  private static final float F2 = 0.5f * (float) Math.sqrt( 3.0 ) - 0.5f;
  private static final float G2 = 0.5f - (float) Math.sqrt( 3.0 ) / 6f;
  
  private static final int[][] GRAD_2 = {
      { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 },
      { 1, 0 }, { -1, 0 }, { 1, 0 }, { -1, 0 },
      { 0, 1 }, { 0, -1 }, { 0, 1 }, { 0, -1 },
  };
  
  private static float dot( final int[] g, final float x, final float y ) {
    return g[ 0 ] * x + g[ 1 ] * y;
  }
  
  public float noise2d( final float xin, final float yin ) {
    final float s = ( xin + yin ) * F2;
    
    final int i = floor( xin + s );
    final int j = floor( yin + s );
    
    final float t = ( i + j ) * G2;
    
    final float x0 = xin - i + t;
    final float y0 = yin - j + t;
    
    final int i1;
    final int j1;
    
    if ( x0 > y0 ) {
      i1 = 1;
      j1 = 0;
    } else {
      i1 = 0;
      j1 = 1;
    }
    
    final float x1 = x0 - i1 + G2;
    final float y1 = y0 - j1 + G2;
    
    final float x2 = x0 - 1f + 2f * G2;
    final float y2 = y0 - 1f + 2f * G2;
    
    final int ii = i & 255;
    final int jj = j & 255;
    
    final int gi0 = perm[ ii + perm[ jj ] ] % 12;
    final int gi1 = perm[ ii + i1 + perm[ jj + j1 ] ] % 12;
    final int gi2 = perm[ ii + 1 + perm[ jj + 1 ] ] % 12;
    
    float n0 = 0f;
    float n1 = 0f;
    float n2 = 0f;
    
    float t0 = 0.5f - x0 * x0 - y0 * y0;
    
    if ( t0 >= 0f ) {
      t0 *= t0;
      t0 *= t0;
      n0 = t0 * dot( GRAD_2[ gi0 ], x0, y0 );
    }
    
    float t1 = 0.5f - x1 * x1 - y1 * y1;
    
    if ( t1 >= 0f ) {
      t1 *= t1;
      t1 *= t1;
      n1 = t1 * dot( GRAD_2[ gi1 ], x1, y1 );
    }
    
    float t2 = 0.5f - x2 * x2 - y2 * y2;
    
    if ( t2 >= 0f ) {
      t2 *= t2;
      t2 *= t2;
      n2 = t2 * dot( GRAD_2[ gi2 ], x2, y2 );
    }
    
    return 70f * ( n0 + n1 + n2 );
  }
  
  @ Override
  public float[] noise2d( final float[] result, final int n, final float scale0,
      final int sx, final float dx, final int sy, final float dy ) {
    final int length = sx * sy;
    
    if ( result == null || result.length < length ) {
      return noise2d( new float[ length ], n, scale0, sx, dx, sy, dy );
    }
    
    for ( int y = 0; y < sy; y++ ) {
      for ( int x = 0; x < sx; x++ ) {
        final int index = sx * y + x;
        
        float value = 0f;
        float sum = 0f;
        
        for ( int i = 0; i < n; i++ ) {
          final int scale1 = 1 << i;
          final float scale = scale0 * scale1;
          
          final float xin = ( x + dx ) * scale;
          final float yin = ( y + dy ) * scale;
          
          value += noise2d( xin, yin ) / scale1;
          sum += 1f / scale1;
        }
        
        result[ index ] = ( value / sum + 1f ) * 0.5f;
      }
    }
    
    return result;
  }
}