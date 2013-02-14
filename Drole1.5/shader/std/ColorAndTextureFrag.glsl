/* BaseShader for Drole */

uniform sampler2D permTexture, maskTexture;

void main() {
	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st);
	//if(texture2D(maskTexture, gl_FragCoord.xy).a == 0.0) gl_FragColor = vec4(0, 0, 0, 0);

//	gl_FragColor = texture2D(maskTexture, gl_TexCoord[0].st);
}
