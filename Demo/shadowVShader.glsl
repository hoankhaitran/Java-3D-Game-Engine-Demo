#version 430 
layout(location = 0) in vec3 vertex_position;
uniform mat4 shadowMVP_matrix;
out vec3 originalVertex;
void main () {
	
	gl_Position = shadowMVP_matrix*vec4 (vertex_position, 1.0);
	originalVertex =(shadowMVP_matrix*vec4 (vertex_position, 1.0)).xyz;
  
}
