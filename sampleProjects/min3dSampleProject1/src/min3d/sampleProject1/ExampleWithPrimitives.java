package min3d.sampleProject1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import min3d.Utils;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.HollowCylinder;
import min3d.objectPrimitives.Rectangle;
import min3d.objectPrimitives.Sphere;
import min3d.vos.Color4;
import min3d.vos.Color4Managed;
import min3d.vos.Number3d;
import min3d.vos.RenderType;


import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Example using a few object primitives.
 * Also demonstrates rendering 'subset' of index buffer list (list of triangle faces)
 * 
 * TODO: Break up examples into smaller Activities...
 * 
 * @author Lee
 */
public class ExampleWithPrimitives extends RendererActivity
{
	Object3dContainer _square1;
	Object3dContainer _cylinder;
	Object3dContainer _box;
	
	int _cylinderNumFaces;
	int _cylinderFaceIndexStart;
	int _cylinderFaceIndexLength;
	int _cylinderFaceIndexIncrementer;
	
	float _dx;
	float _dy;

	public void initScene() 
	{
		scene.backgroundColor().setAll(0xff888888);
		
		initRectangle();
		initCylinder();
		initCube();
	}
	
	public void initRectangle()
	{
		// Rectangle (plane) with amusing image

		Bitmap b = Utils.makeBitmapFromResourceId(this, R.drawable.deadmickey);
		float ratio = (float)b.getHeight() / (float)b.getWidth();

		_square1 = new Rectangle(5f, 5f * ratio);
		_square1.name("square1");
		_square1.position().z = -2f;
		_square1.position().y = 0f;
		_square1.enableDoubleSided(true);

		scene.addChild(_square1);
		
		// * Texture can only be inited after object is added to scene
		_square1.initTexture(b);
		
		b.recycle();
	}

	private void initCylinder()
	{
		// Cylinder with just normals enabled (no vertex colors and texture) 
		
		_cylinder = new HollowCylinder(1f, 0.5f, 0.66f, 25); 
		_cylinder.name("cylinder");
		_cylinder.scale().setAll(1.2f,1.2f,1.2f);
		_cylinder.normalsEnabled(true);
		_cylinder.colorsEnabled(false);
		scene.addChild(_cylinder);
		
		_cylinder.faces().renderSubsetEnabled(true);
		_cylinder.enableDoubleSided(true);

		_cylinderNumFaces = _cylinder.faces().size();
		_cylinderFaceIndexStart = 0;
		_cylinderFaceIndexLength = 0;
		_cylinderFaceIndexIncrementer = +2;		
	}
	
	private void initCube()
	{
		// Cube with just vertex coloring enabled; no normals (lighting) and no texture
		
		_box = new Box(.5f,.5f,.5f);
		_box.name("box");
		_box.colorsEnabled(true);
		_box.normalsEnabled(false);
		scene.addChild(_box);
	}
	
	@Override 
	public void updateScene() 
	{
		// Rotate objects just to have some movement
		
		_box.rotation().y += 1;
		_box.rotation().x += 0.66f;
		
		_cylinder.rotation().y += 1.5;

		// Update the parameters for rendering 'subset' of cylinder's faces

		_cylinder.faces().renderSubsetStartIndex(_cylinderFaceIndexStart);
		_cylinder.faces().renderSubsetLength(_cylinderFaceIndexLength);
		_cylinderFaceIndexLength += _cylinderFaceIndexIncrementer;
		if (_cylinderFaceIndexLength >= _cylinderNumFaces-1) _cylinderFaceIndexIncrementer = -2;
		if (_cylinderFaceIndexLength <= 0+1) _cylinderFaceIndexIncrementer = +2;
		
		// Change camera FOV with trackball (x)
		
		if (_dx != 0) {
			float len = scene.camera().frustrum.shortSideLength() + _dx;
			scene.camera().frustrum.shortSideLength(len); 
			_dx = 0;
		}
	}

	//

	@Override
	public boolean onTrackballEvent(MotionEvent $e)
	{
		_dx = $e.getX();
		_dy = $e.getY();
		
		return true;
	}
}
