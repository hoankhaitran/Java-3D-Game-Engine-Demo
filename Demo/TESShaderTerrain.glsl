#version 430 core 

uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
uniform layout(location = 6)mat4 m_matrix;

layout (quads, fractional_odd_spacing) in; // reduce popping 
layout (binding = 5) uniform sampler2D s; 

in TCS_OUT 
{ vec2 tc; 
} tes_in[]; // in vertex attrib is an array 
 
out TES_OUT 
{ vec2 tc; 
 vec3 world_coord; 
 vec3 eye_coord;
} tes_out; // out vertex attrib is a scalar 
 
void main(void) 
{ 
mat4 mvp =proj_matrix*mv_matrix;
// interpolate the texture coordinates for the grid points 
 vec2 tc1 = mix(tes_in[0].tc, tes_in[1].tc, gl_TessCoord.x); 
 vec2 tc2 = mix(tes_in[2].tc, tes_in[3].tc, gl_TessCoord.x); 
 vec2 tc = mix(tc2, tc1, gl_TessCoord.y); 
 
 // interpolate the actual positions of the grid points 
 vec4 p1 = mix(gl_in[0].gl_Position, gl_in[1].gl_Position, 
 gl_TessCoord.x); 
 vec4 p2 = mix(gl_in[2].gl_Position, gl_in[3].gl_Position, 
 gl_TessCoord.x); 
 vec4 p = mix(p2, p1, gl_TessCoord.y); 
 
 // offset the y coordinate by the height map 
 p.y += texture2D(s, tc).r * 10.0; // last number is depth 
 gl_Position = mvp * p; 
 tes_out.tc = tc; 
 tes_out.world_coord = (mv_matrix * p).xyz; 
 tes_out.eye_coord = (mv_matrix * p).xyz; 
 
} 
