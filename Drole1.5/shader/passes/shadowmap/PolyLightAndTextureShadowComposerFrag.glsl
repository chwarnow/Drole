varying vec3 normal, vertex;
varying vec4 vAmbient;

uniform sampler2D permTexture;

uniform sampler2DShadow ShadowMap;

varying vec4 ShadowCoord;

// This define the value to move one pixel left or right
uniform float xPixelOffset ;

// This define the value to move one pixel up or down
uniform float yPixelOffset ;

float lookup( vec2 offSet) {
  // Values are multiplied by ShadowCoord.w because shadow2DProj does a W division for us.
  return shadow2DProj(ShadowMap, ShadowCoord + vec4(offSet.x * xPixelOffset * ShadowCoord.w, offSet.y * yPixelOffset * ShadowCoord.w, 0.0, 0.0) ).w;
}

vec4 getShadowComponent() {
 // Used to lower moir?pattern and self-shadowing
  //shadowCoord+=0.005;
  float shadow ;
  
  // Avoid counter shadow
  //if (ShadowCoord.w > 1.0)
  // Simple lookup, no PCF
  vec3 color = vec3(1, 1, 1);	
  //			shadow = lookup(vec2(0.0,0.0));

  // 8x8 kernel PCF
  float x,y;
  for (y = -3.5 ; y <=3.5 ; y+=1.0)
    for (x = -3.5 ; x <=3.5 ; x+=1.0)
      shadow += lookup(vec2(x,y));

  shadow /= 64.0 ;
  color*=shadow;

  // 8x8 PCF wide kernel (step is 10 instead of 1)
  /*
					float x,y;
   					for (y = -30.5 ; y <=30.5 ; y+=10.0)
   						for (x = -30.5 ; x <=30.5 ; x+=10.0)
   							shadow += lookup(vec2(x,y));
   					
   					shadow /= 64.0 ;
   					*/

  // 4x4 kernel PCF

  //float x,y;
  //for (y = -1.5 ; y <=1.5 ; y+=1.0)
  //for (x = -1.5 ; x <=1.5 ; x+=1.0)
  //	shadow += lookup(vec2(x,y));

  //shadow /= 16.0 ;

  //color *= shadow;

  // 4x4  PCF wide kernel (step is 10 instead of 1)
  /*
					float x,y;
   					for (y = -10.5 ; y <=10.5 ; y+=10.0)
   						for (x = -10.5 ; x <=10.5 ; x+=10.0)
   							shadow += lookup(vec2(x,y));
   					
   					shadow /= 16.0 ;
   					*/

  // 4x4  PCF dithered
  /*
					// use modulo to vary the sample pattern
   					vec2 o = mod(floor(gl_FragCoord.xy), 2.0);
   				
   					shadow += lookup(vec2(-1.5, 1.5) + o);
   					shadow += lookup(vec2( 0.5, 1.5) + o);
   					shadow += lookup(vec2(-1.5, -0.5) + o);
   					shadow += lookup(vec2( 0.5, -0.5) + o);
   					shadow *= 0.25 ;
   					color*=shadow;
   					*/
  //gl_FragColor =	(shadow)*gl_Color;//vec4((shadow)*gl_Color.rgb, gl_Color.a);
  
  return vec4(color, 1);
}

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

	gl_FragColor = texture2D(permTexture, gl_TexCoord[0].st) * (vAmbient + lightColor) * getShadowComponent();
} 
