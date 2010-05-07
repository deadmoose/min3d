package min3d.sampleProject1;

import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Sphere;
import android.graphics.Bitmap;

/**
 * @author Lee
 */
public class ExampleAssigningTexturesDynamically extends RendererActivity
{
	Object3dContainer _object;
	int _textureId1;
	int _textureId2;
	int _textureId3;
	int _count;
	
	
	public void initScene() 
	{
		_count = 0;
		
		Bitmap b;
		
		b = Utils.makeBitmapFromResourceId(R.drawable.jupiter);
		_textureId1 = Shared.renderer().uploadTextureAndReturnId(b);
		b.recycle();
		
		b = Utils.makeBitmapFromResourceId(R.drawable.earth);
		_textureId2 = Shared.renderer().uploadTextureAndReturnId(b);
		b.recycle();
		
		b = Utils.makeBitmapFromResourceId(R.drawable.moon);
		_textureId3 = Shared.renderer().uploadTextureAndReturnId(b);
		b.recycle();
		
		_object = new Sphere(1f, 15, 10);
		scene.addChild(_object);
	}
	
	@Override 
	public void updateScene() 
	{
		_count++;
		
		if (_count % 240 == 0)
			_object.textureId(0);
		else if (_count % 240 == 60) 
			_object.textureId(_textureId1);
		else if (_count % 240 == 120) 
			_object.textureId(_textureId2);
		else if (_count % 240 == 180) 
			_object.textureId(_textureId3);
		
		_object.rotation().y +=1;
	}
}
