#version 120

// http://www.emich.edu/compsci/projects/LukasLang.pdf

uniform sampler2D Hatch0, Hatch1, Hatch2, Hatch3, Hatch4, Hatch5, imageTex;
varying float[6] hatchWeights;

void main(void) {
	vec2 st = gl_TexCoord[0].st; // texturecoordinate
	// compose hatched color by blending between texture of tonalart maps using hatch weights.
	gl_FragColor =    texture2D(Hatch0, st) * hatchWeights[0]
			+ texture2D(Hatch1, st) * hatchWeights[1]
			+ texture2D(Hatch2, st) * hatchWeights[2]
			+ texture2D(Hatch3, st) * hatchWeights[3]
			+ texture2D(Hatch4, st) * hatchWeights[4]
			+ texture2D(Hatch5, st) * hatchWeights[5];

	if(gl_FragColor.r == 0.0 && gl_FragColor.g == 0.0 && gl_FragColor.b == 0.0) {
		gl_FragColor = texture2D(imageTex, st);
	} else {
		gl_FragColor *= texture2D(imageTex, st);
	}
	
	gl_FragColor.a = 1.0;
}