/* BaseShader for Drole */

void main() {
	gl_FragColor = gl_Color;

	// Mask
	if(distance(gl_FragCoord.xy, vec2(540, 540)) > 540.0) gl_FragColor = vec4(0, 0, 0, 0);
}
