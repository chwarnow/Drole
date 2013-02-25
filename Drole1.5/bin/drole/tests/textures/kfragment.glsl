uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;

uniform int numTextures;

void main() {
	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

	if(numTextures == 1) color *= texture2D(texture0, gl_TexCoord[0].st);
	if(numTextures == 2) color *= texture2D(texture1, gl_TexCoord[1].st);
	if(numTextures == 3) color *= texture2D(texture2, gl_TexCoord[2].st);
	if(numTextures == 4) color *= texture2D(texture3, gl_TexCoord[3].st);
	if(numTextures == 5) color *= texture2D(texture4, gl_TexCoord[4].st);
	if(numTextures == 6) color *= texture2D(texture5, gl_TexCoord[5].st);
	if(numTextures == 7) color *= texture2D(texture6, gl_TexCoord[6].st);

	gl_FragColor = color;
}
