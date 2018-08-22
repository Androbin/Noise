#define F3 ( 1.f / 3.f )
#define G3 ( 1.f / 6.f )

#define GRAD_3 (float3[]) { \
  (float3) ( 1.f, 1.f, 0.f ), (float3) ( -1.f,  1.f, 0.f ), (float3) ( 1.f, -1.f,  0.f ), (float3) ( -1.f, -1.f,  0.f ), \
  (float3) ( 1.f, 0.f, 1.f ), (float3) ( -1.f,  0.f, 1.f ), (float3) ( 1.f,  0.f, -1.f ), (float3) ( -1.f,  0.f, -1.f ), \
  (float3) ( 0.f, 1.f, 1.f ), (float3) (  0.f, -1.f, 1.f ), (float3) ( 0.f,  1.f, -1.f ), (float3) (  0.f, -1.f, -1.f ) }

float noise( global const int* perm, const float xin, const float yin, const float zin ) {
  const float s = ( xin + yin + zin ) * F3;
  
  const int i = floor( xin + s );
  const int j = floor( yin + s );
  const int k = floor( zin + s );
  
  const float t = ( i + j + k ) * G3;
  
  const float x0 = xin - i + t;
  const float y0 = yin - j + t;
  const float z0 = zin - k + t;
  
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
  
  const float x1 = x0 - i1 + G3;
  const float y1 = y0 - j1 + G3;
  const float z1 = z0 - k1 + G3;
  
  const float x2 = x0 - i2 + F3;
  const float y2 = y0 - j2 + F3;
  const float z2 = z0 - k2 + F3;
  
  const float x3 = x0 - .5f;
  const float y3 = y0 - .5f;
  const float z3 = z0 - .5f;
  
  const int ii = i & 255;
  const int jj = j & 255;
  const int kk = k & 255;
  
  const int gi0 = perm[ ii + perm[ jj + perm[ kk ] ] ] % 12;
  const int gi1 = perm[ ii + i1 + perm[ jj + j1 + perm[ kk + k1 ] ] ] % 12;
  const int gi2 = perm[ ii + i2 + perm[ jj + j2 + perm[ kk + k2 ] ] ] % 12;
  const int gi3 = perm[ ii + 1 + perm[ jj + 1 + perm[ kk + 1 ] ] ] % 12;
  
  const float4 x4 = (float4) ( x0, x1, x2, x3 );
  const float4 y4 = (float4) ( y0, y1, y2, y3 );
  const float4 z4 = (float4) ( z0, z1, z2, z3 );
  
  float4 t4 = .6f - x4 * x4 - y4 * y4 - z4 * z4;
  t4 = max( t4, 0.f );
  t4 *= t4;
  t4 *= t4;
  
  const float n0 = dot( GRAD_3[ gi0 ], (float3) ( x0, y0, z0 ) );
  const float n1 = dot( GRAD_3[ gi1 ], (float3) ( x1, y1, z1 ) );
  const float n2 = dot( GRAD_3[ gi2 ], (float3) ( x2, y2, z2 ) );
  const float n3 = dot( GRAD_3[ gi3 ], (float3) ( x3, y3, z3 ) );
  
  const float4 n4 = (float4) ( n0, n1, n2, n3 );
  
  return dot( t4, n4 );
}

kernel void simplex3d( global float* result, global const int* perm, const int n,
                       const float scale0, const float dx, const float dy, const float dz ) {
  const size_t idx = get_global_id( 0 );
  const size_t idy = get_global_id( 1 );
  const size_t idz = get_global_id( 2 );
  
  const size_t strideX = get_global_size( 0 );
  const size_t strideY = get_global_size( 1 );
  
  const size_t id = strideX * ( strideY * idz + idy ) + idx;
  
  float value = 0.f;
  float sum = 0.f;
  
  for ( int i = 0; i < n; i++ ) {
    const int scale1 = 1 << i;
    const float scale = scale0 * scale1;
    
    const float xin = ( idx + dx ) * scale;
    const float yin = ( idy + dy ) * scale;
    const float zin = ( idz + dz ) * scale;
    
    value += noise( perm, xin, yin, zin ) / scale1;
    sum += 1.f / scale1;
  }
  
  result[ id ] = 16.f * value / sum + .5f;
}