package min3d.sampleProject1;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Rectangle;
import min3d.vos.Light;
import min3d.vos.ShadeModel;

/**
 * Test activity - ignore
 * 
 * @author Lee
 */
public class ScratchActivity extends RendererActivity
{
	Object3dContainer _object1;
	Object3dContainer _object2;
	Object3dContainer _rect;
	Light _light;
	
	int _count;
	
	public void initScene() 
	{
		_light = new Light();
		_light.ambient.setAll(0x0);
		_light.diffuse.setAll(0xffff8888);
		_light.position.setAll(0,2,2);
		scene.lights().add( _light );

		_object1 = new Box(1,1,1); // Sphere(1,20,15);
		_object1.position().x = -.75f;
		scene.addChild(_object1);
		
		_object2 = new Box(1,1,1); // Sphere(1,20,15);
		_object2.position().x = .75f;
		scene.addChild(_object2);
		
		_rect = new Rectangle(3, 3, 10, 10, 0xffffffff);
		_rect.doubleSidedEnabled(true);
		scene.addChild(_rect);
		
		//

//		Bitmap b = Utils.makeBitmapFromResourceId(this, R.drawable.moon);
//		Shared.textureManager().addTextureId(b, "moon", false);
//		b.recycle();
//		TextureVo texture = new TextureVo("moon");
//		_object.textures().add(texture);
		
		_count = 0;
	}

	@Override 
	public void updateScene() 
	{
//		short i = (short)((_count*2) % 255);
//		Log.v("x", i+"");
		_light.specular.setAll(0xff00ff00);
		
		
		if (_count % 60 == 0) {
			_object1.colorMaterialEnabled(! _object1.colorMaterialEnabled() );
		}
		
		if (_count % 60 == 0) {
			_object2.shadeModel(ShadeModel.FLAT);
		}
		if (_count % 60 == 30) {
			_object2.shadeModel(ShadeModel.SMOOTH);
		}
		
		_object1.rotation().y++;
		_object2.rotation().y++;
		_count++;
	}
}
