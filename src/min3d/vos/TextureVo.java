package min3d.vos;

import java.util.ArrayList;

/**
 * Contains the properties of a texture which can be assigned to an object.
 * An object can be assigned multiple TextureVo's by adding them to 
 * the Object3d's TextureList (usually up to just 2 w/ current Android hardware).
 * 
 *  The "textureEnvs" ArrayList defines what texture environment commands 
 *  will be sent to OpenGL for the texture. Typically, this needs to hold
 *  just one TextureEnvVo, but can hold an arbitrary number, for more
 *  complex 'layering' operations. 
 *  
 *  TODO: Allow for adding glTexEnvf commands (float instead of int)
 *  
 *  TODO: Add more properties, like glTexParameterx-related things, etc.
 *  
 *  TODO: Ability to assign arbitrary UV lists per-TextureVo! Non-trivial...
 */
public class TextureVo 
{
	public String textureId;
	public ArrayList<TexEnvxVo> textureEnvs;
	
	public TextureVo(String $textureId, ArrayList<TexEnvxVo> $textureEnvVo)
	{
		textureId = $textureId;
		textureEnvs = $textureEnvVo;
	}
	
	public TextureVo(String $textureId)
	{
		textureId = $textureId;
		textureEnvs = new ArrayList<TexEnvxVo>();
		textureEnvs.add( new TexEnvxVo());
	}
}
