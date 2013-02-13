#version 120

// http://www.emich.edu/compsci/projects/LukasLang.pdf

varying float[6] hatchWeights;
uniform vec3 lightDir;
void main (void) {
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	// vec3 lightDir = vec3(-10.1, .10, .50);//gl_LightSource[0].position.xyz;
	vec3 normalW = normalize(gl_NormalMatrix * gl_Normal);
	float diffuse = clamp(dot(lightDir.xyz, normalW) , 0.0 , 1.0);
	float hatchFactor = 6.0 * diffuse * diffuse * diffuse * diffuse; // make shading darker

	hatchWeights = float[6](0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	hatchFactor = clamp(hatchFactor, 0.0, 5.0); // values between 0. 0 and 5. 0
	int index = int(floor(5.0 - hatchFactor));
	float blending = fract(hatchFactor); // blending value
	if(hatchFactor == 5.0 ) {
		hatchWeights[0] = 1.0;
	} else {
		hatchWeights[index] = blending;
		hatchWeights[index+1] = 1.0 - blending;
	}
}