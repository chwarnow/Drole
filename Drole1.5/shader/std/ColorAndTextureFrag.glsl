/* BaseShader for Drole */

uniform sampler2D permTexture;

void main() {
	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st);
}
