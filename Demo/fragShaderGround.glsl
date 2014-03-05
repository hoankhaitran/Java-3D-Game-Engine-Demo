#version 430 
layout(location = 0) in vec3 vertex_position;

in vec2 tc;
in vec4 shadow_coord;


uniform mat4 shadowMVP_matrix;

uniform sampler2DShadow shadow_tex;



out vec4 frag_color;
 

struct Material 
{ vec4 ambient; 
 vec4 diffuse; 
 vec4 specular; 
 float shininess; 
}; 
uniform vec4 globalAmbient; // global ambient light 
uniform Material material; // current material 
void main () {

 frag_color = globalAmbient*material.ambient+ textureProj(shadow_tex, shadow_coord); 

// frag_color= new vec4(0.5,1,1,1);
  
}