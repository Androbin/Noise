package de.androbin.noise;

import static de.androbin.math.util.floats.FloatMathUtil.*;

public class SimplexNoise3D extends SimplexNoise implements Noise3D {
  private static final float F3 = 1f / 3f;
  private static final float G3 = 1f / 6f;
  
  private static final int[][] GRAD_3 = {
      { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 },
      { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 },
      { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 },
  };
  
  private static float dot( final int[] g, final float x, final float y, final float z ) {
    return g[ 0 ] * x + g[ 1 ] * y + g[ 2 ] * z;
  }
  
  public float noise3d( final float xin, final float yin, final float zin ) {
    final float s = ( xin + yin + zin ) * F3;
    
    final int i = floor( xin + s );
    final int j = floor( yin + s );
    final int k = floor( zin + s );
    
    final float t = ( i + j + k ) * G3;
    
    final float x0 = xin - i + t;
    final float y0 = yin - j + t;
    final float z0 = zin - k + t;
    
    int i1 = 0;
    int i2 = 0;
    int j1 = 0;
    int j2 = 0;
    int k1 = 0;
    int k2 = 0;
    
    if ( x0 >= y0 ) {
      if ( y0 >= z0 ) {
        i1 = 1;
        i2 = 1;
        j2 = 1;
      } else if ( x0 >= z0 ) {
        i1 = 1;
        i2 = 1;
        k2 = 1;
      } else {
        k1 = 1;
        i2 = 1;
        k2 = 1;
      }
    } else if ( y0 < z0 ) {
      k1 = 1;
      j2 = 1;
      k2 = 1;
    } else if ( x0 < z0 ) {
      j1 = 1;
      j2 = 1;
      k2 = 1;
    } else {
      j1 = 1;
      i2 = 1;
      j2 = 1;
    }
    
    final float x1 = x0 - i1 + G3;
    final float y1 = y0 - j1 + G3;
    final float z1 = z0 - k1 + G3;
    
    final float x2 = x0 - i2 + F3;
    final float y2 = y0 - j2 + F3;
    final float z2 = z0 - k2 + F3;
    
    final float x3 = x0 - 0.5f;
    final float y3 = y0 - 0.5f;
    final float z3 = z0 - 0.5f;
    
    final int ii = i & 255;
    final int jj = j & 255;
    final int kk = k & 255;
    
    final int gi0 = perm[ ii + perm[ jj + perm[ kk ] ] ] % 12;
    final int gi1 = perm[ ii + i1 + perm[ jj + j1 + perm[ kk + k1 ] ] ] % 12;
    final int gi2 = perm[ ii + i2 + perm[ jj + j2 + perm[ kk + k2 ] ] ] % 12;
    final int gi3 = perm[ ii + 1 + perm[ jj + 1 + perm[ kk + 1 ] ] ] % 12;
    
    float n0 = 0f;
    float n1 = 0f;
    float n2 = 0f;
    float n3 = 0f;
    
    float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
    
    if ( t0 >= 0f ) {
      t0 *= t0;
      t0 *= t0;
      n0 = t0 * dot( GRAD_3[ gi0 ], x0, y0, z0 );
    }
    
    float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
    
    if ( t1 >= 0f ) {
      t1 *= t1;
      t1 *= t1;
      n1 = t1 * dot( GRAD_3[ gi1 ], x1, y1, z1 );
    }
    float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
    
    if ( t2 >= 0f ) {
      t2 *= t2;
      t2 *= t2;
      n2 = t2 * dot( GRAD_3[ gi2 ], x2, y2, z2 );
    }
    
    float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
    
    if ( t3 >= 0f ) {
      t3 *= t3;
      t3 *= t3;
      n3 = t3 * dot( GRAD_3[ gi3 ], x3, y3, z3 );
    }
    
    return 32f * ( n0 + n1 + n2 + n3 );
  }
  
  @ Override
  public float[] noise3d( final float[] result, final int n, final float scale0,
      final int sx, final float dx, final int sy, final float dy, final int sz, final float dz ) {
    final int length = sx * sy * sz;
    
    if ( result == null || result.length < length ) {
      return noise3d( new float[ length ], n, scale0, sx, dx, sy, dy, sz, dz );
    }
    
    for ( int z = 0; z < sz; z++ ) {
      for ( int y = 0; y < sy; y++ ) {
        for ( int x = 0; x < sx; x++ ) {
          final int index = sx * ( sy * z + y ) + x;
          
          float value = 0f;
          float sum = 0f;
          
          for ( int i = 0; i < n; i++ ) {
            final int scale1 = 1 << i;
            final float scale = scale0 * scale1;
            
            final float xin = ( x + dx ) * scale;
            final float yin = ( y + dy ) * scale;
            final float zin = ( z + dz ) * scale;
            
            value += noise3d( xin, yin, zin ) / scale1;
            sum += 1f / scale1;
          }
          
          result[ index ] = ( value / sum + 1f ) * 0.5f;
        }
      }
    }
    
    return result;
  }
}