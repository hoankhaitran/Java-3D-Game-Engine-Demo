#version 430 
layout(location = 0) in vec3 vertex_position;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main () {
  
  gl_Position =proj_matrix*mv_matrix*vec4 (vertex_position, 4) ;
  
}
