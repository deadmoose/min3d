package min3d.sampleProject1;

import android.graphics.Bitmap;
import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.HollowCylinder;
import min3d.vos.TextureVo;

/**
 * Demonstrates setting U/V texture wrapping 
 * (TextureVo.repeatU and TextureVo.repeatV)
 * 
 * @author Lee
 */
public class ExampleTextureWrap extends RendererActivity
{
	Object3dContainer _object;
	TextureVo _texture;
	int _counter;
	
	public void initScene() 
	{
		scene.light().ambient.setAll(0xffaaaaaa);
		
		_object = new HollowCylinder(1f, 0.5f, 0.66f, 25);
		_object.normalsEnabled(true);
		_object.colorsEnabled(false);
		scene.addChild(_object);
		
		Bitmap b = Utils.makeBitmapFromResourceId(R.drawable.uglysquares);
		Shared.textureManager().addTextureId(b, "texture");
		b.recycle();
		
		_texture = new TextureVo("texture");
		
		_object.textures().add(_texture);
		
		_counter = 0;
	}

	@Override 
	public void updateScene() 
	{
		_object.rotation().y+=1.5f;
		
		if (_counter % 40 == 0) 
		{
			_texture.repeatU = ! _texture.repeatU;
		}
		if (_counter % 80 == 0) 
		{
			_texture.repeatV = ! _texture.repeatV;
		}
		
		_counter++;
	}
}
