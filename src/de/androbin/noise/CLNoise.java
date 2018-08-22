package de.androbin.noise;

import static de.androbin.lwjgl.util.BufferUtil.*;
import static de.androbin.opencl.CLBufferUtil.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL10.*;
import de.androbin.opencl.*;
import java.nio.*;
import org.lwjgl.opencl.*;

public abstract class CLNoise implements Noise {
  private FloatBuffer buffer;
  private CLMem mem;
  
  public void cleanup() {
    clReleaseMemObject( mem );
  }
  
  private float[] coverMemory( final float[] result, final int length ) {
    if ( result != null && result.length == length ) {
      return result;
    }
    
    buffer = createFloatBuffer( length );
    
    if ( mem != null ) {
      clReleaseMemObject( mem );
    }
    
    mem = createReadOnlyBuffer( length, Float.BYTES );
    return new float[ length ];
  }
  
  protected float[] noise( final CLExecutor executor, final float[] result0, final int ... dims ) {
    int length = 1;
    
    for ( final int dim : dims ) {
      length *= dim;
    }
    
    final float[] result = coverMemory( result0, length );
    
    final CLKernel kernel = executor.kernel;
    kernel.setArg( 0, mem );
    
    executor.execute( dims );
    
    readBuffer( mem, buffer );
    getBuffer( buffer, result );
    return result;
  }
}