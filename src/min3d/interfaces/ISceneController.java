package min3d.interfaces;

public interface ISceneController 
{
	// Initialization of initial scene objects must happen here 
	public void initScene();

	// Updating properties of scene objects happens here.
	// Is called on every frame.
	public void updateScene();
	
}
