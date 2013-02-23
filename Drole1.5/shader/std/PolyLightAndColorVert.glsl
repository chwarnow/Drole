varying vec4 ecPos;
varying vec3 normal;
 
void main() {
    /* first transform the normal into eye space and normalize the result */
    normal = normalize(gl_NormalMatrix * gl_Normal);
 
    /* compute the vertex position  in camera space. */
    ecPos = gl_ModelViewMatrix * gl_Vertex;

    gl_Position = ftransform();

	gl_FrontColor = gl_Color;
}
