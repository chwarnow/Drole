// Used for shadow lookup
varying vec4 ShadowCoord;

void main()
{
		gl_TexCoord[0] = gl_MultiTexCoord0;

     	ShadowCoord = gl_TextureMatrix[7] * gl_Vertex;
  
		gl_Position = ftransform();

		gl_FrontColor = gl_Color;
}
