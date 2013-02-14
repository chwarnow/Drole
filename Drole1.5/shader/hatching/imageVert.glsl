varying vec3 normal;
varying vec3 vpos;
varying vec4 dvpos;

varying vec3 vertex_light_position;
varying vec3 vertex_normal;
uniform vec3 lightPos;

void main()
{
	// vertex normal
	normal = normalize(gl_NormalMatrix * gl_Normal);
	
	// vertex position
	vpos = vec3(gl_ModelViewMatrix * gl_Vertex);

	// depth vertex position
	dvpos = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	// vertex position
	gl_Position = ftransform();

	// Set the front color to the color passed through with glColor*f 
	gl_FrontColor = gl_Color;

	// lighting
	 // Calculate the normal value for this vertex, in world coordinates (multiply by gl_NormalMatrix)
    	vertex_normal = normalize(gl_NormalMatrix * gl_Normal);
    	// Calculate the light position for this vertex
    	vertex_light_position = normalize(lightPos); 
} 
