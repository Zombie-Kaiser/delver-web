#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_texture1;

uniform float u_brightness;

vec4 blur9(sampler2D image, vec2 uv, vec2 resolution, vec2 direction);

void main()
{    
    vec4 color = texture2D(u_texture, v_texCoords);

    // high pass blur filter
    vec4 blur_color = texture2D(u_texture1, v_texCoords);
    blur_color.a = 1.0;

    gl_FragColor = (color * 0.8) + (blur_color * 1.15);
}