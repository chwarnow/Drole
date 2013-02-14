/* BaseShader for Drole */

uniform sampler2D maskTexture;

void main() {
	gl_FragColor = gl_Color;
	//if(texture2D(maskTexture, gl_FragCoord.xy).a == 0.0) gl_FragColor = vec4(0, 0, 0, 0);

	//gl_FragColor = texture2D(maskTexture, gl_FragCoord.xy);
}
