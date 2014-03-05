#version 430 
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_normal;
layout(location = 2) in vec2 texPos;
uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
uniform mat4 shadowMVP_matrix;


out vec3 position_eye, normal_eye;
//out vec2 tc;
out vec4 shadow_coord;
uniform layout(location = 5)sampler2D s;
uniform mat4 n_matrix;

out VS_OUT 
{ vec2 tc; 
} vs_out;
 



void main () {

	
 const vec4 vertices[] = 
 vec4[] (vec4(-0.5, 0.0, -0.5, 1.0), // simple corners of square 
 vec4( 0.5, 0.0, -0.5, 1.0), 
 vec4(-0.5, 0.0, 0.5, 1.0), 
 vec4( 0.5, 0.0, 0.5, 1.0)); 
 int x = gl_InstanceID & 63; // note use of instancing 
 int y = gl_InstanceID >> 6; 
 vec2 offs = vec2(x,y); 
 vs_out.tc = (vertices[gl_VertexID].xz + offs + vec2(0.5)) / 64.0; 
 gl_Position = vertices[gl_VertexID] + vec4(float(x-32), 
 0.0, float(y-32), 0.0); 
	
  
}
