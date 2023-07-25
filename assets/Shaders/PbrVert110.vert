
void main(){
        //@input vec3 modelPosition the vertex position in model space (usually assigned with Attr.inPosition or Global.position)
    //@input mat4 worldViewProjectionMatrix the World View Projection Matrix transforms model space to projection space
    //@input mat4 worldNormalMatrix the World View Normal Matrix transforms model space to world space
    //@input vec2 texCoord1 The first texture coordinates of the vertex (usually assigned with Attr.inTexCoord)
    //@input vec2 texCoord2 The second texture coordinates of the vertex (usually assigned with Attr.inTexCoord2)
    //@input vec4 vertColor The color of the vertex (output as a varying)
    //@input vec3 normal Normal of the vertex (usually assigned with Attr.inNormal)
    //@input vec4 tangent Tangent of the vertex (usually assigned with Attr.inTangent)
   
    //@output vec4 projPosition Position of the vertex in projection space.(usually assigned to Global.position)
    //@output vec2 texCoord1 The first texture coordinates of the vertex (output as a varying)
    //@output vec2 texCoord2 The second texture coordinates of the vertex (output as a varying)
    //@output vec4 vertColor The color of the vertex (output as a varying)
    //@output vec4 projTangent Tangent of vertex in projection space

    projPosition = worldViewProjectionMatrix * vec4(modelPosition, 1.0);
    projNormal = normalize(worldNormalMatrix * normal);
    projTangent = vec4(normalize(worldNormalMatrix * tangent.xyz), tangent.w);
}