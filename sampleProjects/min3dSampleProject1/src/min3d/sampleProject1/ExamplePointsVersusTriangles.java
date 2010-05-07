package min3d.sampleProject1;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Sphere;
import min3d.vos.RenderType;

/**
 * @author Lee
 */
public class ExamplePointsVersusTriangles extends RendererActivity
{
	Object3dContainer _object;
	int _count;
	
	public void initScene() 
	{
		_object = new Sphere(1f, 15, 10);
		_object.normalsEnabled(false); // .. allow vertex colors to show through
		
		_object.pointSize(6f); // ... make the points fatter
		
		scene.addChild(_object);
		
		_count = 0;
	}
	
	@Override 
	public void updateScene() 
	{
		if (_count % 120 == 0) {
			_object.renderType(RenderType.TRIANGLES);
		}
		else if (_count % 120 == 60) {
			_object.renderType(RenderType.POINTS);
		}
		
		_object.rotation().y +=1;
		_object.rotation().z += 0.2f;

		_count++;
	}
}
