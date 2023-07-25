#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/PBR.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"

uniform vec4 g_LightData[NB_LIGHTS];

void main(){
        //@input vec4 position 
    //@input vec3 cameraPosition 
    //@input float materialRoughness 
    //@input float materialMetallic 
    //@input vec2 texCoord1
    //@input vec2 texCoord2 
    //@input vec4 materialColor
    //@input sampler2D albedoMap 
    //@input sampler2D normalMap 
    //@input sampler2D metallicMap 
    //@input sampler2D roughnessMap 
    //@input sampler2D emissiveMap 
    //@input sampler2D lightMap 
    //@input vec4 tangent 
    //@input vec3 normal 
    //@input float normalType
    //@output vec4 outColor 

    vec3 viewDir = normalize(cameraPosition - position.xyz);
    vec3 norm = normalize(normal);
    #ifdef NORMALMAP
        vec3 tan = normalize(tangent.xyz);
        mat3 tbnMat = mat3(tan, tangent.xyz * cross( norm, tan), norm);
    #endif

    #ifdef ALBEDOMAP
        vec4 albedo = texture2D(albedoMap, texCoord1) * materialColor;
    #else
        vec4 albedo = materialColor;
    #endif
    

    #ifdef ROUGHNESSMAP
        float Roughness = texture2D(roughnessMap, texCoord1).r * max(materialRoughness, 1e-4);
    #else
        float Roughness =  max(materialRoughness, 1e-4);
    #endif
    #ifdef METALLICMAP
        float Metallic = texture2D(metallicMap, texCoord1).r * max(materialMetallic, 1e-4);
    #else
        float Metallic =  max(materialMetallic, 1e-4);
    #endif
 
    float alpha = albedo.a;

    #ifdef NORMALMAP
      vec4 normalHeight = texture2D(normalMap, texCoord1);
      //Note the -2.0 and -1.0. We invert the green channel of the normal map, 
      //as it's complient with normal maps generated with blender.
      //see http://hub.jmonkeyengine.org/forum/topic/parallax-mapping-fundamental-bug/#post-256898
      //for more explanation.
      vec3 normal2 = normalize((normalHeight.xyz * vec3(2.0, normalType * 2.0, 2.0) - vec3(1.0, normalType * 1.0, 1.0)));
      normal2 = normalize(tbnMat * normal2);
      //normal = normalize(normal * inverse(tbnMat));
    #else
      vec3 normal2 = norm;
    #endif

    float specular = 0.5;
    float nonMetalSpec = 0.08 * specular;
    vec4 specularColor = (nonMetalSpec - nonMetalSpec * Metallic) + albedo * Metallic;
    vec4 diffuseColor = albedo - albedo * Metallic;
    vec3 fZero = vec3(specular);

    outColor.rgb = vec3(0.0);
    vec3 ao = vec3(1.0);

    #ifdef LIGHT_MAP
       vec3 lightMapColor;
       #if defined(separateTexCoord)
          lightMapColor = texture2D(lightMap, texCoord2).rgb;
       #else
          lightMapColor = texture2D(lightMap, texCoord1).rgb;
       #endif
       #ifdef AO_MAP
         lightMapColor.gb = lightMapColor.rr;
         ao = lightMapColor;
       #else
         outColor.rgb += diffuseColor.rgb * lightMapColor;
       #endif
       specularColor.rgb *= lightMapColor;
    #endif

    float ndotv = max( dot( normal2, viewDir ), 0.0);

    for( int i = 0;i < NB_LIGHTS; i+=3){
        vec4 lightColor = g_LightData[i];
        vec4 lightData1 = g_LightData[i+1];
        vec4 lightDir;
        vec3 lightVec;            
        lightComputeDir(position.xyz, lightColor.w, lightData1, lightDir, lightVec);

        float fallOff = 1.0;
        #if __VERSION__ >= 110
            // allow use of control flow
        if(lightColor.w > 1.0){
        #endif
            fallOff =  computeSpotFalloff(g_LightData[i+2], lightVec);
        #if __VERSION__ >= 110
        }
        #endif
        //point light attenuation
        fallOff *= lightDir.w;

        lightDir.xyz = normalize(lightDir.xyz);
        vec3 directDiffuse;
        vec3 directSpecular;

        float hdotv = PBR_ComputeDirectLight(normal2, lightDir.xyz, viewDir,
                            lightColor.rgb, fZero, Roughness, ndotv,
                            directDiffuse,  directSpecular);

        vec3 directLighting = diffuseColor.rgb * directDiffuse + directSpecular;

        outColor.rgb += directLighting * fallOff;
    }
    #if NB_PROBES >= 1
        vec3 color1 = vec3(0.0);
        vec3 color2 = vec3(0.0);
        vec3 color3 = vec3(0.0);
        float weight1 = 1.0;
        float weight2 = 0.0;
        float weight3 = 0.0;

        float ndf = renderProbe(viewDir, position.xyz, normal2, norm, Roughness, diffuseColor, specularColor, ndotv, ao, g_LightProbeData, g_ShCoeffs, g_PrefEnvMap, color1);
        #if NB_PROBES >= 2
            float ndf2 = renderProbe(viewDir, wPosition, normal, norm, Roughness, diffuseColor, specularColor, ndotv, ao, g_LightProbeData2, g_ShCoeffs2, g_PrefEnvMap2, color2);
        #endif
        #if NB_PROBES == 3
            float ndf3 = renderProbe(viewDir, wPosition, normal, norm, Roughness, diffuseColor, specularColor, ndotv, ao, g_LightProbeData3, g_ShCoeffs3, g_PrefEnvMap3, color3);
        #endif

        #if NB_PROBES >= 2
            float invNdf =  max(1.0 - ndf,0.0);
            float invNdf2 =  max(1.0 - ndf2,0.0);
            float sumNdf = ndf + ndf2;
            float sumInvNdf = invNdf + invNdf2;
            #if NB_PROBES == 3
                float invNdf3 = max(1.0 - ndf3,0.0);
                sumNdf += ndf3;
                sumInvNdf += invNdf3;
                weight3 =  ((1.0 - (ndf3 / sumNdf)) / (NB_PROBES - 1)) *  (invNdf3 / sumInvNdf);
            #endif

            weight1 = ((1.0 - (ndf / sumNdf)) / (NB_PROBES - 1)) *  (invNdf / sumInvNdf);
            weight2 = ((1.0 - (ndf2 / sumNdf)) / (NB_PROBES - 1)) *  (invNdf2 / sumInvNdf);

            float weightSum = weight1 + weight2 + weight3;

            weight1 /= weightSum;
            weight2 /= weightSum;
            weight3 /= weightSum;
        #endif

        #ifdef USE_AMBIENT_LIGHT
            color1.rgb *= g_AmbientLightColor.rgb;
            color2.rgb *= g_AmbientLightColor.rgb;
            color3.rgb *= g_AmbientLightColor.rgb;
        #endif
        outColor.rgb += color1 * clamp(weight1,0.0,1.0) + color2 * clamp(weight2,0.0,1.0) + color3 * clamp(weight3,0.0,1.0);

    #endif

    #if defined(EMISSIVE) || defined (EMISSIVEMAP)
        #if defined(EMISSIVEMAP)
            vec4 emissive = texture2D(emissiveMap, texCoord1);
        #else
            vec4 emissive = emissiveColor;
        #endif
        outColor += emissive * pow(emissive.a, emissivePower) * emissiveIntensity;
    #endif
    outColor.a = alpha;
}