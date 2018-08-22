package de.androbin.noise;

import java.util.*;
import java.util.function.*;

public class SimplexNoise implements Noise {
  protected final int[] perm = new int[ 512 ];
  
  @ Override
  public void seed( final Random random ) {
    seed( i -> random.nextInt( 256 ) );
  }
  
  public void seed( final IntUnaryOperator function ) {
    for ( int i = 0; i < 256; i++ ) {
      final int n = function.applyAsInt( i );
      perm[ i + 256 ] = n;
      perm[ i ] = n;
    }
  }
}