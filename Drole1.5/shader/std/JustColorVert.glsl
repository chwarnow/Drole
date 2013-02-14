/* BaseShader for Drole */

void main() {
	// vertex position
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	// Set the front color to the color passed through with glColor*f
	gl_FrontColor = gl_Color;
} 
