package min3d.sampleProject1;

import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Rectangle;
import min3d.vos.TextureVo;

/**
 * Scratch activity (ignore)
 */
public class Testbed extends RendererActivity
{
	Object3dContainer _o;
	
	public void initScene() 
	{
		_o = new Rectangle(2f, 2.5f, 1,1, 0xff00ff00);
		_o.doubleSidedEnabled(true);
		_o.normalsEnabled(false);
		scene.addChild(_o);
		
		Shared.textureManager().addTextureId( Utils.makeBitmapFromResourceId(R.drawable.deadmickey), "mickey");
		_o.textures().addById("mickey");
	}

	@Override 
	public void updateScene() 
	{
		_o.rotation().y++;
	}
}
