package de.androbin.noise;

import static de.androbin.math.util.floats.FloatMathUtil.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public final class SimplexNoise implements Noise3D {
  private static final float F2 = 0.5f * (float) Math.sqrt( 3.0 ) - 0.5f;
  private static final float G2 = 0.5f - (float) Math.sqrt( 3.0 ) / 6f;
  private static final float F3 = 1f / 3f;
  private static final float G3 = 1f / 6f;
  
  private static final int[][] GRAD_3 = {
      { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 },
      { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 },
      { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }
  };
  
  private final int[] perm = new int[ 512 ];
  
  public SimplexNoise() {
    this( ThreadLocalRandom.current() );
  }
  
  public SimplexNoise( final long seed ) {
    this( new Random( seed ) );
  }
  
  public SimplexNoise( final Random random ) {
    this( i -> random.nextInt( 256 ) );
  }
  
  public SimplexNoise( final IntUnaryOperator function ) {
    for ( int i = 0; i < 256; i++ ) {
      final int n = function.applyAsInt( i );
      perm[ i + 256 ] = n;
      perm[ i ] = n;
    }
  }
  
  private static float dot( final int[] g, final float x, final float y ) {
    return g[ 0 ] * x + g[ 1 ] * y;
  }
  
  private static float dot( final int[] g, final float x, final float y, final float z ) {
    return g[ 0 ] * x + g[ 1 ] * y + g[ 2 ] * z;
  }
  
  @ Override
  public float noise( final float xin, final float yin ) {
    float t;
    float y0;
    int j;
    
    final float s = ( xin + yin ) * F2;
    final int i = floor( xin + s );
    final float X0 = i - ( t = ( i + ( j = floor( yin + s ) ) ) * G2 );
    
    final float x0 = xin - X0;
    
    int i1 = 0;
    int j1 = 0;
    
    if ( x0 > ( y0 = yin - j + t ) ) {
      i1 = 1;
    } else {
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
    
    float t0 = 0.5f - x0 * x0 - y0 * y0;
    
    float n0;
    float n1;
    float n2;
    
    if ( t0 < 0f ) {
      n0 = 0f;
    } else {
      t0 *= t0;
      n0 = t0 * t0 * dot( GRAD_3[ gi0 ], x0, y0 );
    }
    
    float t1 = 0.5f - x1 * x1 - y1 * y1;
    
    if ( t1 < 0f ) {
      n1 = 0f;
    } else {
      t1 *= t1;
      n1 = t1 * t1 * dot( GRAD_3[ gi1 ], x1, y1 );
    }
    
    float t2 = 0.5f - x2 * x2 - y2 * y2;
    
    if ( t2 < 0f ) {
      n2 = 0f;
    } else {
      t2 *= t2;
      n2 = t2 * t2 * dot( GRAD_3[ gi2 ], x2, y2 );
    }
    
    return 70f * ( n0 + n1 + n2 );
  }
  
  @ Override
  public float noise( final float xin, final float yin, final float zin ) {
    final float s = ( xin + yin + zin ) * F3;
    
    final int i = floor( xin + s );
    final int j = floor( yin + s );
    final int k = floor( zin + s );
    
    final float t = ( i + j + k ) * G3;
    
    final float X0 = i - t;
    final float Y0 = j - t;
    final float Z0 = k - t;
    
    final float x0 = xin - X0;
    final float y0 = yin - Y0;
    final float z0 = zin - Z0;
    
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
    
    float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
    
    float n0;
    float n1;
    float n2;
    float n3;
    
    if ( t0 < 0f ) {
      n0 = 0f;
    } else {
      t0 *= t0;
      n0 = t0 * t0 * dot( GRAD_3[ gi0 ], x0, y0, z0 );
    }
    
    float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
    
    if ( t1 < 0f ) {
      n1 = 0f;
    } else {
      t1 *= t1;
      n1 = t1 * t1 * dot( GRAD_3[ gi1 ], x1, y1, z1 );
    }
    float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
    
    if ( t2 < 0f ) {
      n2 = 0f;
    } else {
      t2 *= t2;
      n2 = t2 * t2 * dot( GRAD_3[ gi2 ], x2, y2, z2 );
    }
    
    float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
    
    if ( t3 < 0f ) {
      n3 = 0f;
    } else {
      t3 *= t3;
      n3 = t3 * t3 * dot( GRAD_3[ gi3 ], x3, y3, z3 );
    }
    
    return 32f * ( n0 + n1 + n2 + n3 );
  }
}