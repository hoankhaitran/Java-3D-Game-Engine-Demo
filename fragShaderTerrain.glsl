#version 430 
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertNormal;
in vec2 tc;

uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
layout (binding = 5) uniform sampler2D s;
layout (binding = 6) uniform sampler2D s1; 



in vec3 position_eye, normal_eye;
out vec4 frag_color;
uniform vec4 fog_color = vec4(1, 1, 1, 0.0); 

 

in TES_OUT 
{ vec2 tc; 
 vec3 world_coord; 
 vec3 eye_coord; 
} fs_in; 
 
vec4 fog(vec4 c) 
{ float z = length(fs_in.eye_coord); 
 float de = 0.035 * 
 smoothstep(0.0, 6.0, 10.0 - fs_in.world_coord.z); 
 float di = 0.045 * 
 smoothstep(0.0, 40.0, 20.0 - fs_in.world_coord.z); 
 float extinction = exp(-z * de); 
 float inscattering = exp(-z * di); 
 return c * extinction + fog_color * (1.0 - inscattering); 
} 


void main () {
vec4 texel = texture(s, fs_in.tc);
vec4 texel1 = texture(s1, fs_in.tc);


  

 frag_color =fog(texel*texel1);//*globalAmbient*material.ambient+ textureProj(shadow_tex, shadow_coord)*vec4 ( Ia+Id+Is , 1.0); 

  
}