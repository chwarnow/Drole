varying vec3 normal, vertex;

varying vec4 ecPos;
 
void main() {
    /* first transform the normal into eye space and normalize the result */
    normal = normalize(gl_NormalMatrix * gl_Normal);
 
    /* compute the vertex position  in camera space. */
    ecPos = gl_ModelViewMatrix * gl_Vertex;

    gl_Position = ftransform();

	gl_FrontColor = gl_Color;

	gl_Position = ftransform();
	
	gl_FrontColor = gl_Color;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord1;
	gl_TexCoord[2] = gl_MultiTexCoord2;
	gl_TexCoord[3] = gl_MultiTexCoord3;
	gl_TexCoord[4] = gl_MultiTexCoord4;
	gl_TexCoord[5] = gl_MultiTexCoord5;
	gl_TexCoord[6] = gl_MultiTexCoord6;
	gl_TexCoord[7] = gl_MultiTexCoord7;

	vertex = gl_Position.xyz;
}
