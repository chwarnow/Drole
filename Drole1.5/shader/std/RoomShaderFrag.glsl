varying vec3 normal, vertex;

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;

uniform int numTextures;

uniform int numLights;

uniform vec4 ambient;

varying vec4 ecPos;

vec4 pointLight(int lightIndex) {
    vec3 n, lightDir;
    float NdotL;
    float att, dist;

	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
     
    /* a fragment shader can't write a verying variable, hence we need
    a new variable to store the normalized interpolated normal */
    n = normalize(normal);
     
    // Compute the light direction
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
	// Calculate Texture color
	vec4 preLightColor;

	vec4 textureColor = vec4(0.0, 0.0, 0.0, 1.0);

	if(numTextures >= 1) textureColor = texture2D(texture0, gl_TexCoord[0].st);
	if(numTextures >= 2) textureColor *= texture2D(texture1, gl_TexCoord[1].st);
	if(numTextures >= 3) textureColor *= texture2D(texture2, gl_TexCoord[2].st);
	if(numTextures >= 4) textureColor *= texture2D(texture3, gl_TexCoord[3].st);
	if(numTextures >= 5) textureColor *= texture2D(texture4, gl_TexCoord[4].st);
	if(numTextures >= 6) textureColor *= texture2D(texture5, gl_TexCoord[5].st);
	if(numTextures == 7) textureColor *= texture2D(texture6, gl_TexCoord[6].st);

	if(numTextures > 0) preLightColor = vec4(textureColor.rgb * gl_Color.rgb, textureColor.a);
	else preLightColor = gl_Color;

	// Calculate Light color
	vec4 color = preLightColor;
 
	if(numLights > 0) {
		vec4 lightColor = vec4(0.0, 0.0, 0.0, 0.0);
		for(int i = 0; i < numLights; i++) lightColor += pointLight(i);
		color *= lightColor;
	}

	gl_FragColor = vec4(color.rgb * ambient.rgb, gl_Color.a);
}
