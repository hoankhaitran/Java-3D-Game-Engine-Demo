#version 430 core 
 
layout (triangles) in; 
 
in vec3 originalVertex[]; 

in int count[]; 
 
out vec3 originalVertexG; 
 
layout (triangle_strip) out; 
layout (max_vertices=3) out; 
 
void main (void) 
{ if (mod(count[0],3)!=0) 
 { for (int i=0; i<=gl_in.length(); i++) 
 { gl_Position = gl_in[i].gl_Position; 
 originalVertexG = originalVertex[i]; 

 EmitVertex(); 
 } 
 } 
 EndPrimitive(); 
} 
