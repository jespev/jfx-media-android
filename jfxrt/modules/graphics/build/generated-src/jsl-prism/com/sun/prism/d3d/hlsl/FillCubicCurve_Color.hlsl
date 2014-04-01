float mask(float2 tc0, float2 tc1) {
float3 vwt = float3(tc0.x, tc0.y, tc1.x);
float3 px = ddx(vwt);
float3 py = ddy(vwt);
float vsq = vwt.x * vwt.x;
float vsq3 = 3.0 * vsq;
float fx = (vsq3 * px.x) - (vwt.z * px.y) - (vwt.y * px.z);
float fy = (vsq3 * py.x) - (vwt.z * py.y) - (vwt.y * py.z);
float sd = tc1.y * (((vsq * vwt.x) - (vwt.y * vwt.z)) / sqrt(fx * fx + fy * fy));
float alpha = 0.5 - sd;
return clamp(alpha, 0.0, 1.0);
}
void main(in float2 pos0 : TEXCOORD0,
in float2 pos1 : TEXCOORD1,
in float2 pixcoord : VPOS,
in float4 jsl_vertexColor : COLOR0,
out float4 color : COLOR0) {
color = mask(pos0, pos1) * jsl_vertexColor;
}
