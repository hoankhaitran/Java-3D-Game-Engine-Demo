#version 430 
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertNormal;
in vec2 tc;
in vec4 shadow_coord;
in mat3 toObjectLocal;
uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
uniform layout(location = 5)sampler2D s;
uniform layout(location = 6)sampler2D s1;
uniform layout(location = 7)sampler2D normalSampler;
uniform mat4 shadowMVP_matrix;

layout(binding =0) uniform sampler2DShadow shadow_tex;
uniform mat4 n_matrix;

in vec3 position_eye, normal_eye;
out vec4 frag_color;
 
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
vec4 texel = texture(s,tc);
vec4 texel1 = texture(s1,tc);
vec4 NormalTexel = texture(normalSampler,tc);

vec3 Ia = light.ambient.xyz * material.ambient.xyz;

vec3 light_position_eye = vec3 (vec4 (light.position, 1.0));
 // vec3 distance_to_light_eye = light_position_eye - position_eye;
 vec3 distance_to_light_eye = toObjectLocal*(light_position_eye - position_eye);
  vec3 s = normalize (distance_to_light_eye);
  //vec3 n = normalize(normal_eye);
  vec3 N = normalize((texture(normalSampler,tc).rgb) * 2.0f - vec3(1.0f)); 
  
  
  float dot_prod = dot (s, N);
  dot_prod = max (dot_prod, 0.0);
  vec3 Id = light.diffuse.xyz * material.diffuse.xyz * dot_prod; // final diffuse intensity

vec3 r = reflect (-s, N);
  vec3 v = toObjectLocal* normalize (-position_eye);
  float dot_prod_specular = dot (r, v);
  dot_prod_specular = max (dot_prod_specular, 0.0);
  float specular_factor = pow (dot_prod_specular, material.shininess);
  vec3 Is = light.specular.xyz * material.specular.xyz * specular_factor; // final specular intensity
  

 frag_color = texel*texel1*globalAmbient*material.ambient+ textureProj(shadow_tex, shadow_coord)*vec4 ( Ia+Id+Is , 1.0); 

// frag_color= new vec4(0.5,1,1,1);
  
}