#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D permTexture;

varying vec4 vertColor;

void main() {
	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st) * vertColor;
}