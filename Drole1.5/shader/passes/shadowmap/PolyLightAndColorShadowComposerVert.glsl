varying vec3 normal, vertex;
varying vec4 vAmbient;

uniform vec4 ambient;

varying vec4 ShadowCoord;
	
void main() {
	ShadowCoord = gl_TextureMatrix[7] * gl_Vertex;
	
	normal = gl_NormalMatrix * gl_Normal;

	gl_FrontColor = gl_Color;

	gl_Position = ftransform();

	vertex = gl_Position.xyz;

	vAmbient = ambient;
}
