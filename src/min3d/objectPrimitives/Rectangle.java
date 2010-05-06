package min3d.objectPrimitives;

import java.util.ArrayList;

import min3d.Utils;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.vos.Color4;
import min3d.vos.Face;
import min3d.vos.Number3d;
import min3d.vos.Uv;
import min3d.vos.Vertex3d;



import android.opengl.GLUtils;
import android.util.Log;

/**
 * Origin is center of rectangle.
 * A good minimal-example of setting up an Object3d
 */
public class Rectangle extends Object3dContainer
{
	public Rectangle(float $width, float $height)
	{
		super(4, 2);

		float w = $width / 2f;
		float h = $height / 2f;

		short ul = this.verticies().addVertex(-w,+h,0f,	0f,0f,	0,0,1,	(short)255,(short)255,(short)255,(short)255);
		short ur = this.verticies().addVertex(+w,+h,0f,	1f,0f,	0,0,1,	(short)255,(short)255,(short)255,(short)255);
		short lr = this.verticies().addVertex(+w,-h,0f,	1f,1f,	0,0,1,	(short)255,(short)255,(short)255,(short)255);
		short ll = this.verticies().addVertex(-w,-h,0f,	0f,1f,	0,0,1,	(short)255,(short)255,(short)255,(short)255);
		
		Utils.addQuad(this, ul,ur,lr,ll);
	}
}
