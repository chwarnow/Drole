uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;

uniform int w00t;

void main() {
	vec4 color = vec4(1.0, 0.0, 0.0, 0.0);

	if(w00t >= 1) color = texture2D(texture0, gl_TexCoord[0].st);
	if(w00t >= 2) color *= texture2D(texture1, gl_TexCoord[1].st);
	if(w00t >= 3) color *= texture2D(texture2, gl_TexCoord[2].st);
	if(w00t >= 4) color *= texture2D(texture3, gl_TexCoord[3].st);
	if(w00t >= 5) color *= texture2D(texture4, gl_TexCoord[4].st);
	if(w00t >= 6) color *= texture2D(texture5, gl_TexCoord[5].st);
	if(w00t == 7) color *= texture2D(texture6, gl_TexCoord[6].st);

	gl_FragColor = color;
}
