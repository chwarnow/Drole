varying vec3 normal, vertex;

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;

uniform int numTextures;

void main() {
	vec4 textureColor = vec4(0.0, 0.0, 0.0, 1.0);

	if(numTextures >= 1) textureColor = texture2D(texture0, gl_TexCoord[0].st);
	if(numTextures >= 2) textureColor *= texture2D(texture1, gl_TexCoord[1].st);
	if(numTextures >= 3) textureColor *= texture2D(texture2, gl_TexCoord[2].st);
	if(numTextures >= 4) textureColor *= texture2D(texture3, gl_TexCoord[3].st);
	if(numTextures >= 5) textureColor *= texture2D(texture4, gl_TexCoord[4].st);
	if(numTextures >= 6) textureColor *= texture2D(texture5, gl_TexCoord[5].st);
	if(numTextures == 7) textureColor *= texture2D(texture6, gl_TexCoord[6].st);

	vec3 lightDir = vec3(gl_LightSource[0].position.xyz - vertex);

 	// Calculate the ambient term
    vec4 ambient_color = (gl_FrontMaterial.ambient * gl_LightSource[0].ambient) + (gl_LightModel.ambient * gl_FrontMaterial.ambient);

    // Calculate the diffuse term
    vec4 diffuse_color = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;

    // Calculate the specular value
    vec4 specular_color = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(max(dot(normal, lightDir), 0.0) , gl_FrontMaterial.shininess);

    // Set the diffuse value (darkness). This is done with a dot product between the normal and the light
    // and the maths behind it is explained in the maths section of the site.
    float diffuse_value = max(dot(normal, gl_LightSource[0].position.xyz), 0.0);

    // Set the output color of our current pixel
    //gl_FragColor = textureColor * (gl_Color + (ambient_color + diffuse_color * diffuse_value + specular_color));

	if(numTextures > 0) gl_FragColor = vec4(textureColor.rgb * gl_Color.rgb, textureColor.a);
	else gl_FragColor = gl_Color;
}
