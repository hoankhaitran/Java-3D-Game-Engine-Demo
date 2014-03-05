#version 430 core 
layout (vertices = 4) out; // outgoing array size set in TCS 
uniform layout(location = 3)mat4 mv_matrix;
uniform layout(location = 4)mat4 proj_matrix;
 
in VS_OUT 
{ in vec2 tc; 
} tcs_in[]; // incoming array size set in application 
 
out TCS_OUT 
{ out vec2 tc; 
} tcs_out[]; 
 
void main(void) 
{ 
mat4 mvp =proj_matrix*mv_matrix;
if (gl_InvocationID == 0) 
 { vec4 p0 = mvp * gl_in[0].gl_Position; 
 vec4 p1 = mvp * gl_in[1].gl_Position; 
 vec4 p2 = mvp * gl_in[2].gl_Position; 
 vec4 p3 = mvp * gl_in[3].gl_Position; 
 p0 /= p0.w; 
 p1 /= p1.w; 
 p2 /= p2.w; 
 p3 /= p3.w; 
 float l0 = length(p2.xy - p0.xy) * 16.0 + 1.0; 
 float l1 = length(p3.xy - p2.xy) * 16.0 + 1.0; 
 float l2 = length(p3.xy - p1.xy) * 16.0 + 1.0; 
 float l3 = length(p1.xy - p0.xy) * 16.0 + 1.0; 
 gl_TessLevelOuter[0] = l0; 
 gl_TessLevelOuter[1] = l1; 
 gl_TessLevelOuter[2] = l2; 
 gl_TessLevelOuter[3] = l3; 
 gl_TessLevelInner[0] = min(l1,l3); 
 gl_TessLevelInner[1] = min(l0,l2); 
 } 
 gl_out[gl_InvocationID].gl_Position = 
 gl_in[gl_InvocationID].gl_Position; 
 tcs_out[gl_InvocationID].tc = tcs_in[gl_InvocationID].tc; 
} 
