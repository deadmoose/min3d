package min3d.core;

import java.util.Comparator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import min3d.Min3d;
import min3d.interfaces.IBitmapManagable;
import min3d.interfaces.ISceneController;
import min3d.vos.FrustrumManaged;
import min3d.vos.Light;
import min3d.vos.RenderType;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;


public class Renderer implements GLSurfaceView.Renderer, IBitmapManagable
{
	private final int FPS_SAMPLE_INTERVAL = 60;

	private GL10 _gl;
	private Scene _scene;

	private boolean _isGl10Only;
	
	private long _time;
	private long _timeWas;
	private long _timeCount;
	private float _fps = -1;
	private boolean _logFps = false;
	
	private Comparator<Object3d> _zComparator; // not implemented
	
	private float _surfaceAspectRatio;
	
	/*
	 * add fucking examples
	 * 
	 * (1) normals + vertex colors, w/o textures = just normals on white color! verify this is normal, sotospeak
	 * 
	 * (1) support specular once normals are 100% confirmed
	 * 
	 * (2) test customrendererer
	 * 
	 * (3) try object-wide alpha setting
	 * (3) alpha - figure out more, or at least explain its absence
	 * 
	 * (3) try adding normal view controls on top of glview, or whatever
	 * 
	 * (3) multiple lights using manager or something
	 * (2) wireframe drawmode? ...
	 * (3) importer!...
	 */
	
	public Renderer(Scene $scene)
	{
		_scene = $scene;
		
		 _zComparator = new Comparator<Object3d>() 
		 {
			@Override
			public int compare(Object3d a, Object3d b) 
			{
				// Not currently using. 
				// Should really calc distance from camera, not "z".
				// Ideally, entire triangle list should be sorted by distance.
				// Meh, too expensive.
				
				return (a.position().z > b.position().z) ? 1 : -1;
			}
		};
	}

	public void onSurfaceCreated(GL10 $gl, EGLConfig eglConfig) 
	{
		setGl($gl);
		
		// Log OpenGL version
		
		String strVersion = _gl.glGetString(GL10.GL_VERSION);
	    String[] a = strVersion.split("\\ ");
	    float n = Float.parseFloat(a[a.length-1]);
	    Log.v(Min3d.TAG, "OpenGL ES version: " + n);

	    // Depth - explicit settings
	    
		_gl.glEnable(GL10.GL_DEPTH_TEST);									
		_gl.glClearDepthf(1.0f);											
		_gl.glDepthFunc(GL10.GL_LESS);										
		_gl.glDepthRangef(0,1f);											
		_gl.glDepthMask(true);												

		// Alpha enabled
		
		_gl.glEnable(GL10.GL_BLEND);										
		_gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); 	

		// "Transparency is best implemented using glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) 
		// with primitives sorted from farthest to nearest."
		
		// CCW frontfaces only, by default
		
	    _gl.glEnable(GL10.GL_CULL_FACE);
	    _gl.glFrontFace(GL10.GL_CCW);
	    _gl.glCullFace(GL10.GL_BACK);
	    
	    // Textures enabled
	    
	    _gl.glEnable(GL10.GL_TEXTURE_2D);

		//
		// Scene object init only happens here, when we get GL for the first time
		//
		
		_scene.init();
	}
	
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{
		setGl(_gl);
		_surfaceAspectRatio = (float)w / (float)h;
		
		_gl.glViewport(0, 0, w, h);
		_gl.glMatrixMode(GL10.GL_PROJECTION);
		_gl.glLoadIdentity();
		
		updateViewFrustrum();
	}
	
	public void onDrawFrame(GL10 gl)
	{
		// Update 'model'
		_scene.sceneController().updateScene();
		
		// Update 'view'
		drawSetup();
		drawScene();

		if (_logFps) doFps();
	}
	
	//
	
	/**
	 *  Accessor to the GL object, in case anything outside this class wants to do 
	 *  bad things with it :)
	 */
	public GL10 gl()
	{
		return _gl;
	}

	/**
	 * Returns last sampled framerate (logFps must be set to true) 
	 */
	public float fps()
	{
		return _fps;
	}
	
	protected void drawSetup()
	{
		// View frustrum
		
		if (_scene.camera().frustrum.isDirty()) {
			updateViewFrustrum();
		}
		 
		// Lighting

//		if (_scene.lightingEnabled()) {
//			_gl.glEnable(GL10.GL_LIGHTING);
//		} 
//		else {
//			_gl.glDisable(GL10.GL_LIGHTING);
//		}

		// Light (just one for now)
		
		Light l = _scene.light();
		
		if (l.ambient.isDirty()) 
		{
			l.commitAmbientBuffer();
			_gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, l.getAmbientBuffer());
			l.ambient.clearDirtyFlag();
		}
		if (l.diffuse.isDirty()) 
		{
			l.commitDiffuseBuffer();
			_gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, l.getDiffuseBuffer());
			l.diffuse.clearDirtyFlag();
		}
		if (l.position.isDirty())
		{
			l.commitPositionBuffer();
			_gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, l.getPositionBuffer());
			l.position.clearDirtyFlag();
		}

		_gl.glEnable(GL10.GL_LIGHT0);
	
		// Background color
		
		if (_scene.backgroundColor().isDirty())
		{
			_gl.glClearColor( 
				(float)_scene.backgroundColor().r() / 255f, 
				(float)_scene.backgroundColor().g() / 255f, 
				(float)_scene.backgroundColor().b() / 255f, 
				(float)_scene.backgroundColor().a() / 255f);
			_scene.backgroundColor().clearDirtyFlag();
		}
		
		_gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		

		_gl.glMatrixMode(GL10.GL_MODELVIEW);
		_gl.glLoadIdentity();

		// Camera 
		
		GLU.gluLookAt(_gl, 
			_scene.camera().position.x,_scene.camera().position.y,_scene.camera().position.z,
			_scene.camera().target.x,_scene.camera().target.y,_scene.camera().target.z,
			_scene.camera().upAxis.x,_scene.camera().upAxis.y,_scene.camera().upAxis.z);
		
		// Always on:
		_gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

	protected void drawScene()
	{
		// Works, kind of:
		// Collections.sort(_objects, _zComparator);

		for (int i = 0; i < _scene.children().size(); i++)
		{
			Object3d o = _scene.children().get(i);
			drawObject(o);
		}
	}
	
	
	//boolean customResult = o.customRenderer(_gl); 
	//if (customResult) return;


	protected void drawObject(Object3d $o)
	{
		if ($o.isVisible() == false) return;		

		// Normals
		
		if ($o.normalsEnabled()) {
			$o.verticies().normals().buffer().position(0);
			_gl.glNormalPointer(GL10.GL_FLOAT, 0, $o.verticies().normals().buffer());
			_gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			_gl.glEnable(GL10.GL_LIGHTING);
		}
		else {
			_gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			_gl.glDisable(GL10.GL_LIGHTING);
		}
		
		// Colors: either per-vertex, or per-object

		if ($o.colorsEnabled()) {
			$o.verticies().colors().buffer().position(0);
			_gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, $o.verticies().colors().buffer());
			_gl.glEnableClientState(GL10.GL_COLOR_ARRAY); 
		}
		else {
			_gl.glColor4f(
				(float)$o.defaultColor().r / 255f, 
				(float)$o.defaultColor().g / 255f, 
				(float)$o.defaultColor().b / 255f, 
				(float)$o.defaultColor().a / 255f
			);
			_gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
		
		// Texture
		
		if ($o.textureId() > 0 && $o.textureEnabled()) 
		{
			$o.verticies().uvs().buffer().position(0);
			_gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, $o.verticies().uvs().buffer());
			_gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			_gl.glActiveTexture(GL10.GL_TEXTURE0);
			_gl.glBindTexture(GL10.GL_TEXTURE_2D, $o.textureId());
		
			_gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
			_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		}
		else {
			_gl.glBindTexture(GL10.GL_TEXTURE_2D, 0); // .. ensures texture isn't reused on subsequent non-textured object
			_gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		// Backface culling 
		
		if ($o.doubleSidedEnabled()) {
		    _gl.glDisable(GL10.GL_CULL_FACE);
		} 
		else {
		    _gl.glEnable(GL10.GL_CULL_FACE);
		}
		
		// Matrix operations

		_gl.glPushMatrix();
		
		_gl.glTranslatef($o.position().x, $o.position().y, $o.position().z);
		
		_gl.glRotatef($o.rotation().x, 1,0,0);
		_gl.glRotatef($o.rotation().y, 0,1,0);
		_gl.glRotatef($o.rotation().z, 0,0,1);
		
		_gl.glScalef($o.scale().x, $o.scale().y, $o.scale().z);
		
		// Draw

		$o.verticies().points().buffer().position(0);
		_gl.glVertexPointer(3, GL10.GL_FLOAT, 0, $o.verticies().points().buffer());

		if ($o.renderType() == RenderType.TRIANGLES)
		{
			if ($o.faces().renderSubsetEnabled())
			{
				int pos = $o.faces().renderSubsetStartIndex() * FacesBufferedList.PROPERTIES_PER_ELEMENT;
				
				$o.faces().buffer().position(pos);
				
				_gl.glDrawElements(
					$o.renderTypeInt(), 
					$o.faces().renderSubsetLength() * FacesBufferedList.PROPERTIES_PER_ELEMENT, 
					GL10.GL_UNSIGNED_SHORT, 
					$o.faces().buffer());
			}
			else
			{
				$o.faces().buffer().position(0);
				
				_gl.glDrawElements(
					$o.renderTypeInt(), 
					$o.faces().size() * FacesBufferedList.PROPERTIES_PER_ELEMENT, 
					GL10.GL_UNSIGNED_SHORT, 
					$o.faces().buffer());
			}
			
		}
		else
		{
			if ($o.renderType() == RenderType.POINTS) {
				_gl.glPointSize($o.pointSize());
			}
			
			_gl.glDrawArrays($o.renderTypeInt(), 0, $o.verticies().size());
		}
		
		
		
		//
		// Recurse on children
		//
		
		if ($o instanceof Object3dContainer)
		{
			Object3dContainer container = (Object3dContainer)$o;
			
			for (int i = 0; i < container.children().size(); i++)
			{
				Object3d o = container.children().get(i);
				drawObject(o);
			}
		}
		
		// Restore matrix
		
		_gl.glPopMatrix();
	}
	
	public int uploadTextureAndReturnId(Bitmap $bitmap)
	{
		int textureId;
		
		int[] a = new int[1];
		_gl.glGenTextures(1, a, 0); // create a 'texture name' (id) and put it in id array
		textureId = a[0];
		_gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		
	    // must be called _after_ setting texture, aparently
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		_gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);		
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, $bitmap, 0);
		
		return textureId; // ... Object3D saves this id and subsequently uses it in its draw() routine
	}
	
	/**
	 * Called by Object3d.deleteTexture()
	 */
	public void deleteTexture(int $textureId)
	{
		int[] a = new int[1];
		a[0] = $textureId;
		_gl.glDeleteTextures(1, a, 0);
	}
	
	protected void updateViewFrustrum()
	{
		FrustrumManaged vf = _scene.camera().frustrum;
		float n = vf.shortSideLength() / 2f;

		float lt, rt, btm, top;
		
		lt  = vf.horizontalCenter() - n*_surfaceAspectRatio;
		rt  = vf.horizontalCenter() + n*_surfaceAspectRatio;
		btm = vf.verticalCenter() - n*1; 
		top = vf.verticalCenter() + n*1;

		if (_surfaceAspectRatio > 1) {
			lt *= 1f/_surfaceAspectRatio;
			rt *= 1f/_surfaceAspectRatio;
			btm *= 1f/_surfaceAspectRatio;
			top *= 1f/_surfaceAspectRatio;
		}
		
		_gl.glMatrixMode(GL10.GL_PROJECTION);
		_gl.glLoadIdentity();
		_gl.glFrustumf(lt,rt, btm,top, vf.zNear(), vf.zFar());
		
		vf.clearDirtyFlag();
	}

	/**
	 * If true, framerate is periodically calculated and Log'ed 
	 */
	public void logFps(boolean $b)
	{
		_logFps = $b;
		
		if (_logFps) { // init
			_time = System.currentTimeMillis();
			_timeCount = 0;
		}
	}
	
	private void setGl(GL10 $gl)
	{
		_gl = $gl;
		_isGl10Only = ! ($gl instanceof GL11);
	}
	
	private void doFps()
	{
		_timeCount++;

		if (_timeCount == FPS_SAMPLE_INTERVAL) 
		{
			_timeWas = _time;
			_time = System.currentTimeMillis();
			float msPerFrame = (float)(_time - _timeWas)/(float)_timeCount;
			_fps = 1000f / msPerFrame;
			_timeCount = 0;

			Log.v(Min3d.TAG, "Renderer FPS " + Math.round(_fps));
		}
	}
}
