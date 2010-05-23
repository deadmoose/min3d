package min3d.sampleProject1;

import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Rectangle;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.TextureVo;

/**
 * Scratch activity (ignore)
 */
public class Testbed extends RendererActivity
{
	private Object3dContainer objModel;

	@Override
	public void initScene() 
	{
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/maqobj");
		
		parser.parse();

		objModel = parser.getParsedObject();
		objModel.scale().x = objModel.scale().y = objModel.scale().z = .7f;
		scene.addChild(objModel);
	}

	@Override
	public void updateScene() {
		objModel.rotation().x++;
		objModel.rotation().z++;
	}
}

