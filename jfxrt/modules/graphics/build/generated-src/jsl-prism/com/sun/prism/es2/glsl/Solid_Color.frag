#ifdef GL_ES
#extension GL_OES_standard_derivatives : enable
precision highp float;
precision highp int;
#else
#define highp
#define mediump
#define lowp
#endif
varying lowp vec4 perVertexColor;
void main() {
gl_FragColor = perVertexColor;
}
