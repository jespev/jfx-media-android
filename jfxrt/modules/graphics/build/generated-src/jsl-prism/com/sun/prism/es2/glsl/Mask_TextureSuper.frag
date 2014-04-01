#ifdef GL_ES
#extension GL_OES_standard_derivatives : enable
precision highp float;
precision highp int;
#else
#define highp
#define mediump
#define lowp
#endif
varying vec2 texCoord0;
varying vec2 texCoord1;
varying lowp vec4 perVertexColor;
uniform sampler2D inputTex0;
uniform sampler2D inputTex1;
lowp vec4 paint(vec2 texCoord0, vec2 texCoord1) {
return (texture2D(inputTex0, texCoord0) * texCoord1.x) + (texture2D(inputTex1, texCoord0).a * texCoord1.y);
}
void main() {
gl_FragColor = paint(texCoord0, texCoord1) * perVertexColor;
}
