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
#define MAX_FRACTIONS (12)
#define TEXTURE_WIDTH (16.0)
#define FULL_TEXEL_X (1.0 / TEXTURE_WIDTH)
#define HALF_TEXEL_X (FULL_TEXEL_X / 2.0)
float4 fractions[12] : register(c0);
sampler2D colors : register(s0);
float offset : register(c12);
float4 sampleGradient(float dist) {
int i;
float relFraction = 0.0;
{
relFraction += clamp((dist - fractions[0].x) * fractions[0].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[1].x) * fractions[1].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[2].x) * fractions[2].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[3].x) * fractions[3].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[4].x) * fractions[4].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[5].x) * fractions[5].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[6].x) * fractions[6].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[7].x) * fractions[7].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[8].x) * fractions[8].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[9].x) * fractions[9].y, 0.0, 1.0);
}
{
relFraction += clamp((dist - fractions[10].x) * fractions[10].y, 0.0, 1.0);
}
float tc = HALF_TEXEL_X + (FULL_TEXEL_X * relFraction);
return tex2D(colors, float2(tc, offset));
}
float4 cycleNone(float dist) {
if (dist <= 0.0){
return tex2D(colors, float2(0.0, offset));
}
 else if (dist >= 1.0){
return tex2D(colors, float2(1.0, offset));
}
 else {
return sampleGradient(dist);
}
}
float4 cycleReflect(float dist) {
dist = 1.0 - (abs(frac(dist * 0.5) - 0.5) * 2.0);
return sampleGradient(dist);
}
float4 cycleRepeat(float dist) {
dist = frac(dist);
return sampleGradient(dist);
}
float4 m0 : register(c13);
float4 m1 : register(c14);
float3 precalc : register(c15);
float3 perspVec : register(c16);
float4 paint(float2 winCoord) {
float3 fragCoord = float3(winCoord.x, winCoord.y, 1.0);
float wdist = dot(fragCoord, perspVec);
float x = dot(fragCoord, m0.xyz) / wdist + m0.w;
float y = dot(fragCoord, m1.xyz) / wdist + m1.w;
float xfx = x - precalc.x;
float dist = (precalc.x * xfx + sqrt(xfx * xfx + y * y * precalc.y)) * precalc.z;
return cycleReflect(dist);
}
void main(in float2 pos0 : TEXCOORD0,
in float2 pos1 : TEXCOORD1,
in float2 pixcoord : VPOS,
in float4 jsl_vertexColor : COLOR0,
out float4 color : COLOR0) {
color = mask(pos0, pos1) * paint(pixcoord) * jsl_vertexColor;
}
