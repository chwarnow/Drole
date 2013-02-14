/* BaseShader for Drole */

void main() {
	// vertex position
	gl_Position = ftransform();

	gl_TexCoord[0] = gl_MultiTexCoord0;

	// Set the front color to the color passed through with glColor*f 
	gl_FrontColor = gl_Color;
} 
