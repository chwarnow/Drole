uniform int numLights;

uniform vec4 ambient;

varying vec4 ecPos;
varying vec3 normal;


vec4 pointLight(int lightIndex) {
    vec3 n, lightDir;
    float NdotL;
    float att, dist;

	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
     
    /* a fragment shader can't write a verying variable, hence we need
    a new variable to store the normalized interpolated normal */
    n = normalize(normal);
     
    // Compute the ligt direction
    lightDir = vec3(gl_LightSource[lightIndex].position - ecPos);
     
    /* compute the distance to the light source to a varying variable*/
    dist = length(lightDir);
 
    /* compute the dot product between normal and ldir */
    NdotL = max(dot(n, normalize(lightDir)), 0.0);
 
    if(NdotL > 0.0) {
        att = 1.0 / (gl_LightSource[lightIndex].constantAttenuation +
                gl_LightSource[lightIndex].linearAttenuation * dist +
                gl_LightSource[lightIndex].quadraticAttenuation * dist * dist);

		color = att * (color + gl_LightSource[lightIndex].diffuse) * NdotL;
    }
    
    return color;
}

void main() {
	vec4 color = gl_Color * ambient;
 
	if(numLights > 0) {
		for(int i = 0; i < numLights; i++) color += pointLight(i);
	}
	
    gl_FragColor = color;
}
