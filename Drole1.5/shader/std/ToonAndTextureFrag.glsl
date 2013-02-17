varying vec3 normal, vertex;
varying vec4 vAmbient;

uniform sampler2D permTexture;

vec4 pointLight(int lightNum) {
	vec3 lightDir = vec3(gl_LightSource[lightNum].position.xyz - vertex);
	vec3 eyeVec = -vertex;
	
	float d = length(lightDir);
	
	float att = 1.0 / ( gl_LightSource[lightNum].constantAttenuation + 
	(gl_LightSource[lightNum].linearAttenuation*d) + 
	(gl_LightSource[lightNum].quadraticAttenuation*d*d) );

	vec4 final_color =  gl_LightSource[lightNum].ambient * att;
							
	vec3 N = normalize(normal);
	vec3 L = normalize(lightDir);
	
	float lambertTerm = dot(N,L);
	
	if(lambertTerm > 0.0) {
		final_color += gl_LightSource[lightNum].diffuse * lambertTerm * att;	
		
		vec3 E = normalize(eyeVec);
		vec3 R = reflect(-L, N);
		
		float specular = pow( max(dot(R, E), 0.0), gl_FrontMaterial.shininess );
		
		final_color += gl_LightSource[lightNum].specular * gl_FrontMaterial.specular * specular * att;	
	}
	
	return final_color;
}

void main() {

	vec4 lightColor = vec4(0.0, 0.0, 0.0, 0.0);

	for(int i = 2; i < 5; i++){
    	lightColor += pointLight(i);
	}

	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st) * (vAmbient + lightColor);	

	//gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st)*color;
	//gl_FragColor = vec4(intensity, intensity, intensity, 1.0);
} 
