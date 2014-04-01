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
sampler2D inputTex : register(s0);
float4 xParams : register(c0);
float4 yParams : register(c1);
float3 perspVec : register(c2);
float4 content : register(c3);
float4 paint(float2 winCoord) {
float3 fragCoord = float3(winCoord.x, winCoord.y, 1.0);
float wParam = dot(fragCoord, perspVec);
float2 texCoord = float2(dot(xParams.xyz, fragCoord) / wParam + xParams.w, dot(yParams.xyz, fragCoord) / wParam + yParams.w);
texCoord = frac(texCoord);
texCoord = float2(content.x, content.y) + texCoord * float2(content.z, content.w);
return tex2D(inputTex, texCoord);
}
void main(in float2 pos0 : TEXCOORD0,
in float2 pos1 : TEXCOORD1,
in float2 pixcoord : VPOS,
in float4 jsl_vertexColor : COLOR0,
out float4 color : COLOR0) {
color = mask(pos0, pos1) * paint(pixcoord) * jsl_vertexColor;
}
