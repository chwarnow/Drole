varying vec3 normal;
varying vec3 vpos;
varying vec4 dvpos;


uniform float shininess;
uniform float zmin;
uniform float zmax;

uniform vec3 lightPos;
varying vec3 vertex_light_position;
varying vec3 vertex_normal;

void main()
{
/*
	// depth
	vec4 v = vec4(dvpos);
	v /= v.w;
	float gray = (v.z - zmin) / (zmax - zmin);
*/

	// phong
	vec3 n = normalize(normal);
	vec4 diffuse = vec4(0.0);
	vec4 specular = vec4(0.0);
	
	// the material properties are embedded in the shader (for now)
	vec4 mat_ambient = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 mat_diffuse = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 mat_specular = vec4(1.0, 1.0, 1.0, 1.0);
	
	// ambient term
	vec4 ambient = mat_ambient * gl_Color;//gl_Color;// gl_LightSource[0].ambient;
	
	// diffuse color
	vec4 kd = mat_diffuse*vec4(.075, .075, .065, gl_Color.a);// * gl_Color*1.0;// gl_LightSource[0].diffuse;
	
	// specular color
	vec4 ks = mat_specular * vec4(.3, .3, .3, gl_Color.a);//(gl_Color*1.5);// gl_Color;// gl_LightSource[0].specular;
	
	// diffuse term
	vec3 lightDir = normalize(lightPos - vpos);
	float NdotL = dot(n, lightDir);
	
	if (NdotL > 0.0)
		diffuse = kd * NdotL;
	
	// specular term
	vec3 rVector = normalize(2.0 * n * dot(n, lightDir) - lightDir);
	vec3 viewVector = normalize(-vpos);
	float RdotV = dot(rVector, viewVector);
	
	if (RdotV > 0.0)
		specular = ks * pow(RdotV, shininess);

	vec4 phongColor = ambient + diffuse + specular;

	// Set the diffuse value (darkness). This is done with a dot product between the normal and the light
    	// and the maths behind it is explained in the maths section of the site.
    	float diffuse_value = max(dot(vertex_normal, vertex_light_position), 0.0);

	// final color
	gl_FragColor = phongColor;// * (1.0-gray);//vec4(1.0 - gray);
	gl_FragColor.a = gl_Color.a;
	// gl_FragColor.a *= (1.0 - gray);
}
