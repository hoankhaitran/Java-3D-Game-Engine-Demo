#version 430 
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_normal;
layout(location = 2) in vec2 texPos;
uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 8)mat4 v_matrix;
uniform layout(location = 4)mat4 proj_matrix;
uniform mat4 shadowMVP_matrix;

vec3 t,b;
out vec3 position_eye, normal_eye,tangent_eye,binormal_eye;
out vec2 tc;
out vec4 shadow_coord;
out mat3 toObjectLocal;
uniform layout(location = 5)sampler2D s;
uniform mat4 n_matrix;
uniform vec4 clip_plane = vec4 (1.5,1.5,2.0,0.85);
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
	vec4 newPos = vec4(vertex_position,1.0);
	newPos = newPos+gl_InstanceID;
	position_eye = vec3 (mv_matrix * newPos);
	normal_eye = vec3 (n_matrix* vec4 (vertex_normal, 0.0));
	//calculating the tangent vector using approximation 
	//reference http://www.geeks3d.com/20130122/normal-mapping-without-precomputed-tangent-space-vectors/
	vec3 c1 = cross(vertex_normal, vec3(0.0, 0.0, 1.0)); 
	vec3 c2 = cross(vertex_normal, vec3(0.0, 1.0, 0.0));
	if (length(c1) > length(c2))
		t=c1;
	else
		t=c2;
	t = normalize(t);
	b = normalize(cross(vertex_normal, t));	
	tangent_eye = vec3 (n_matrix* vec4 (t, 0.0));
	binormal_eye = vec3 (n_matrix* vec4 (b, 0.0));
	toObjectLocal = mat3(tangent_eye.x,binormal_eye.x,normal_eye.x,
	tangent_eye.y,binormal_eye.y,normal_eye.y,
	tangent_eye.z,binormal_eye.z,normal_eye.z);
   
	shadow_coord = shadowMVP_matrix*newPos;
	gl_Position = proj_matrix * vec4 (position_eye, 1.0);
  
}
