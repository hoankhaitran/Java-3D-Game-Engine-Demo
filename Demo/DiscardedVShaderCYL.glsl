#version 430 
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_normal;
layout(location = 2) in vec2 texPos;
uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
uniform mat4 shadowMVP_matrix;
uniform layout(location = 5)sampler2D s;
uniform layout(location = 8)sampler2D heightmap;
uniform mat4 n_matrix;
uniform vec4 clip_plane = vec4 (1.5,1.5,2.0,0.85);
out int count;
out vec3 position_eye, normal_eye;
out vec2 tc;
out vec4 shadow_coord;
out vec3 originalVertex;

out gl_PerVertex 
{ vec4 gl_Position; 
  float gl_ClipDistance[]; 	
}; 
 
struct PositionalLight 
{ vec4 ambient; 
 vec4 diffuse; 
 vec4 specular; 
 vec3 position; 
}; 
struct Material 
{ vec4 ambient; 
 vec4 diffuse; 
 vec4 specular; 
 float shininess; 
}; 
uniform vec4 globalAmbient; // global ambient light 
uniform PositionalLight light; // current positional light 
uniform Material material; // current material 

void main () {
    tc=texPos;
    originalVertex=vertex_position;
	vec4 newPos = vec4(vertex_position,1.0);
	vec4 P = vec4(vertex_position,1.0);
    P.y = P.y + texture2D(heightmap,texPos).r; 
    
    P = mv_matrix * P;
	newPos = newPos+gl_InstanceID;
	position_eye = vec3 (mv_matrix * newPos);
	normal_eye = vec3 (n_matrix* vec4 (vertex_normal, 0.0));
    count = gl_VertexID / 3; 

	shadow_coord = shadowMVP_matrix*newPos;
	
	gl_Position = proj_matrix * P;
  
}
