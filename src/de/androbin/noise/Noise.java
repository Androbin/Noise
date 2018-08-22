package de.androbin.noise;

import java.util.*;
import java.util.concurrent.*;

public interface Noise {
  default void seed() {
    seed( ThreadLocalRandom.current() );
  }
  
  default void seed( final long seed ) {
    seed( new Random( seed ) );
  }
  
  void seed( Random random );
}