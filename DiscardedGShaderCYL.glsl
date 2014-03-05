#version 430 core 
 
layout (triangles) in; 
 
in vec3 normal_eye[]; // pass through 
//in vec3 shadow_coord[]; 
in vec3 position_eye[]; 
in vec3 originalVertex[]; 

in vec2 tc[]; 
in int count[]; 
 
out vec3 normal_eyeG; // pass through 
//out vec3 shadow_coordG; 
out vec3 position_eyeG; 
out vec3 originalVertexG; 
out vec3 varyingHalfVectorG; 
out vec2 tcG; 
 
layout (triangle_strip) out; 
layout (max_vertices=3) out; 
 
void main (void) 
{ if (mod(count[0],3)!=0) 
 { for (int i=0; i<=gl_in.length(); i++) 
 { gl_Position = gl_in[i].gl_Position; 
 normal_eyeG = normal_eye[i]; 
 //shadow_coordG = shadow_coord[i]; 
 position_eyeG = position_eye[i]; 
 originalVertexG = originalVertex[i]; 

 tcG = tc[i]; 
 EmitVertex(); 
 } 
 } 
 EndPrimitive(); 
} 
