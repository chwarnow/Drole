/* BaseShader for Drole */

uniform sampler2D permTexture;

void main() {
	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st);
	// Mask
	if(distance(gl_FragCoord.xy, vec2(540, 540)) > 540.0) gl_FragColor = vec4(0, 0, 0, 0);
}
