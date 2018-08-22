#define F2 ( .5f * sqrt( 3.f ) - .5f )
#define G2 ( .5f - sqrt( 3.f ) / 6.f )

#define GRAD_2 (float2[]) { \
  (float2) ( 1.f, 1.f ), (float2) ( -1.f,  1.f ), (float2) ( 1.f, -1.f ), (float2) ( -1.f, -1.f ), \
  (float2) ( 1.f, 0.f ), (float2) ( -1.f,  0.f ), (float2) ( 1.f,  0.f ), (float2) ( -1.f,  0.f ), \
  (float2) ( 0.f, 1.f ), (float2) (  0.f, -1.f ), (float2) ( 0.f,  1.f ), (float2) (  0.f, -1.f ) }

float noise( global const int* perm, const float xin, const float yin ) {
  const float s = ( xin + yin ) * F2;
  
  const int i = floor( xin + s );
  const int j = floor( yin + s );
  
  const float t = ( i + j ) * G2;
  
  const float x0 = xin - i + t;
  const float y0 = yin - j + t;
  
  const int i1 = (int) ( x0 > y0 );
  const int j1 = (int) ( x0 <= y0 );
  
  const float x1 = x0 - i1 + G2;
  const float y1 = y0 - j1 + G2;
  
  const float x2 = x0 - 1.f + 2.f * G2;
  const float y2 = y0 - 1.f + 2.f * G2;
  
  const int ii = i & 255;
  const int jj = j & 255;
  
  const int gi0 = perm[ ii + perm[ jj ] ] % 12;
  const int gi1 = perm[ ii + i1 + perm[ jj + j1 ] ] % 12;
  const int gi2 = perm[ ii + 1 + perm[ jj + 1 ] ] % 12;
  
  const float3 x3 = (float3) ( x0, x1, x2 );
  const float3 y3 = (float3) ( y0, y1, y2 );
  
  float3 t3 = .5f - x3 * x3 - y3 * y3;
  t3 = max( t3, 0.f );
  t3 *= t3;
  t3 *= t3;
  
  const float n0 = dot( GRAD_2[ gi0 ], (float2) ( x0, y0 ) );
  const float n1 = dot( GRAD_2[ gi1 ], (float2) ( x1, y1 ) );
  const float n2 = dot( GRAD_2[ gi2 ], (float2) ( x2, y2 ) );
  
  const float3 n3 = (float3) ( n0, n1, n2 );
  
  return dot( t3, n3 );
}

kernel void simplex2d( global float* result, global const int* perm, const int n,
                       const float scale0, const float dx, const float dy ) {
  const size_t idx = get_global_id( 0 );
  const size_t idy = get_global_id( 1 );
  
  const size_t strideX = get_global_size( 0 );
  
  const size_t id = strideX * idy + idx;
  
  float value = 0.f;
  float sum = 0.f;
  
  for ( int i = 0; i < n; i++ ) {
    const int scale1 = 1 << i;
    const float scale = scale0 * scale1;
    
    const float xin = ( idx + dx ) * scale;
    const float yin = ( idy + dy ) * scale;
    
    value += noise( perm, xin, yin ) / scale1;
    sum += 1.f / scale1;
  }
  
  result[ id ] = 35.f * value / sum + .5f;
}