/* BaseShader for Drole */

void main() {
	// vertex position
	gl_Position = ftransform();

	// Set the front color to the color passed through with glColor*f
	gl_FrontColor = gl_Color;
} 
