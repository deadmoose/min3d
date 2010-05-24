package min3d.core;

import java.util.Comparator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import min3d.Min3d;
import min3d.Shared;
import min3d.animation.AnimationObject3d;
import min3d.vos.FrustumManaged;
import min3d.vos.Light;
import min3d.vos.RenderType;
import min3d.vos.TextureVo;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;


public class Renderer implements GLSurfaceView.Renderer
{
	private final int FPS_SAMPLE_INTERVAL = 60;

	private GL10 _gl;
	private Scene _scene;
	private TextureManager _textureManager;

	private long _time;
	private long _timeWas;
	private long _timeCount;
	private float _fps = -1;
	private boolean _logFps = false;
	
	
	private Comparator<Object3d> _zComparator; // not implemented
	
	private float _surfaceAspectRatio;
	
	
	public Renderer(Scene $scene)
	{
		_scene = $scene;

		_textureManager = new TextureManager();
		Shared.textureManager(_textureManager); // xxx not ideal
		
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
		Log.i(Min3d.TAG, "Renderer.onSurfaceCreated()");
		
		RenderCaps.setRenderCaps($gl);
		
		setGl($gl);

		reset();
		
		_scene.init();
	}
	
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{
		Log.i(Min3d.TAG, "Renderer.onSurfaceChanged()");
		
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
		
		if (_scene.camera().frustum.isDirty()) {
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
			if(o.animationEnabled())
			{
				((AnimationObject3d)o).update();
			}
			drawObject(o);
		}
	}
	
	
	//boolean customResult = o.customRenderer(_gl); 
	//if (customResult) return;


	protected void drawObject(Object3d $o)
	{
		if ($o.isVisible() == false) return;		

		// Various per-object settings:
		
		// Normals

		if ($o.hasNormals() && $o.normalsEnabled()) {
			$o.vertices().normals().buffer().position(0);
			_gl.glNormalPointer(GL10.GL_FLOAT, 0, $o.vertices().normals().buffer());
			_gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			_gl.glEnable(GL10.GL_LIGHTING);
		}
		else {
			_gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			_gl.glDisable(GL10.GL_LIGHTING);
		}

		// Colors: either per-vertex, or per-object

		if ($o.hasColors() && $o.colorsEnabled()) {
			$o.vertices().colors().buffer().position(0);
			_gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, $o.vertices().colors().buffer());
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
		
		// Point size
		
		if ($o.renderType() == RenderType.POINTS) 
		{
			if ($o.pointSmoothing()) 
				_gl.glEnable(GL10.GL_POINT_SMOOTH);
			else
				_gl.glDisable(GL10.GL_POINT_SMOOTH);
			
			_gl.glPointSize($o.pointSize());
		}

		// Line properties
		
		if ($o.renderType() == RenderType.LINES) 
		{
			if ( $o.lineSmoothing() == true) {
				_gl.glEnable(GL10.GL_LINE_SMOOTH);
			}
			else {
				_gl.glDisable(GL10.GL_LINE_SMOOTH);
			}

			_gl.glLineWidth($o.lineWidth());
		}

		// Backface culling 
		
		if ($o.doubleSidedEnabled()) {
		    _gl.glDisable(GL10.GL_CULL_FACE);
		} 
		else {
		    _gl.glEnable(GL10.GL_CULL_FACE);
		}
		

		drawObject_textures($o);

		
		// Matrix operations in modelview

		_gl.glPushMatrix();
		
		_gl.glTranslatef($o.position().x, $o.position().y, $o.position().z);
		
		_gl.glRotatef($o.rotation().x, 1,0,0);
		_gl.glRotatef($o.rotation().y, 0,1,0);
		_gl.glRotatef($o.rotation().z, 0,0,1);
		
		_gl.glScalef($o.scale().x, $o.scale().y, $o.scale().z);
		
		// Draw

		$o.vertices().points().buffer().position(0);
		_gl.glVertexPointer(3, GL10.GL_FLOAT, 0, $o.vertices().points().buffer());

		if (! $o.ignoreFaces())
		{
			int pos, len;
			
			if (! $o.faces().renderSubsetEnabled()) {
				pos = 0;
				len = $o.faces().size();
			}
			else {
				pos = $o.faces().renderSubsetStartIndex() * FacesBufferedList.PROPERTIES_PER_ELEMENT;
				len = $o.faces().renderSubsetLength();
			}
				
			$o.faces().buffer().position(pos);

			_gl.glDrawElements(
					$o.renderTypeInt(), 
					len * FacesBufferedList.PROPERTIES_PER_ELEMENT, 
					GL10.GL_UNSIGNED_SHORT, 
					$o.faces().buffer());
		}
		else
		{
			_gl.glDrawArrays($o.renderTypeInt(), 0, $o.vertices().size());
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
	
	private void drawObject_textures(Object3d $o)
	{
		// iterate thru object's textures
		
		for (int i = 0; i < RenderCaps.maxTextureUnits(); i++)
		{
			_gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
			_gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i); 

			if ($o.hasUvs() && $o.texturesEnabled())
			{
				$o.vertices().uvs().buffer().position(0);
				_gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, $o.vertices().uvs().buffer());

				TextureVo textureVo = ((i < $o.textures().size())) ? textureVo = $o.textures().get(i) : null;

				if (textureVo != null)
				{
					// activate texture
					int glId = _textureManager.getGlTextureId(textureVo.textureId);
					_gl.glBindTexture(GL10.GL_TEXTURE_2D, glId);
				    _gl.glEnable(GL10.GL_TEXTURE_2D);
					_gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

					_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST); // (OpenGL default is GL_NEAREST_MIPMAP)
					_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // (is OpenGL default)
					
					// do texture environment settings
					for (int j = 0; j < textureVo.textureEnvs.size(); j++)
					{
						_gl.glTexEnvx(GL10.GL_TEXTURE_ENV, textureVo.textureEnvs.get(j).pname, textureVo.textureEnvs.get(j).param);
					}
					
					// texture wrapping settings
					_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, (textureVo.repeatU ? GL10.GL_REPEAT : GL10.GL_CLAMP_TO_EDGE));
					_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, (textureVo.repeatV ? GL10.GL_REPEAT : GL10.GL_CLAMP_TO_EDGE));		

					// texture offset, if any
					if (textureVo.offsetU != 0 || textureVo.offsetV != 0)
					{
						_gl.glMatrixMode(GL10.GL_TEXTURE);
						_gl.glLoadIdentity();
						_gl.glTranslatef(textureVo.offsetU, textureVo.offsetV, 0);
						_gl.glMatrixMode(GL10.GL_MODELVIEW); // .. restore matrixmode
					}
				}
				else
				{
					_gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
				    _gl.glDisable(GL10.GL_TEXTURE_2D);
					_gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				}
			}
			else
			{
				_gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
			    _gl.glDisable(GL10.GL_TEXTURE_2D);
				_gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
		}
	}
	
	/**
	 * Used by TextureManager
	 */
	int uploadTextureAndReturnId(Bitmap $bitmap) /*package-private*/
	{
		int glTextureId;
		
		int[] a = new int[1];
		_gl.glGenTextures(1, a, 0); // create a 'texture name' and put it in array element 0
		glTextureId = a[0];
		_gl.glBindTexture(GL10.GL_TEXTURE_2D, glTextureId);
		
		// 'upload' to gpu
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, $bitmap, 0);
		
		return glTextureId;
	}
	

	/**
	 * Used by TextureManager
	 */
	void deleteTexture(int $glTextureId) /*package-private*/
	{
		int[] a = new int[1];
		a[0] = $glTextureId;
		_gl.glDeleteTextures(1, a, 0);
	}
	
	protected void updateViewFrustrum()
	{
		FrustumManaged vf = _scene.camera().frustum;
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

			//Log.v(Min3d.TAG, "Renderer FPS " + Math.round(_fps));
		}
	}
	
	private void reset()
	{
		// Reset TextureManager
		Shared.textureManager().reset();

		// Do OpenGL settings which we are using as defaults, or which we will not be changing on-draw
		
	    // Explicit depth settings
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

		// Texture
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST); // (OpenGL default is GL_NEAREST_MIPMAP)
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // (is OpenGL default)
		
		// CCW frontfaces only, by default
		_gl.glFrontFace(GL10.GL_CCW);
	    _gl.glCullFace(GL10.GL_BACK);
	    _gl.glEnable(GL10.GL_CULL_FACE);

		//
		// Scene object init only happens here, when we get GL for the first time
		//
	}
}
