varying vec3 normal, vertex;
varying vec4 vAmbient;

uniform vec4 ambient;
	
void main() {
	normal = gl_NormalMatrix * gl_Normal;

	gl_TexCoord[0] = gl_MultiTexCoord0;

	gl_Position = ftransform();

	vertex = gl_Position.xyz;

	vAmbient = ambient;
}
