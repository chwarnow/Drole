// Used for shadow lookup
varying vec4 ShadowCoord;

uniform int shadowTexCoord;

void main() {
	ShadowCoord = gl_TextureMatrix[shadowTexCoord] * gl_Vertex;
  
	gl_Position = ftransform();
	
	gl_FrontColor = gl_Color;
}
