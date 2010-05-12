package min3d.core;

import java.util.HashMap;
import java.util.Set;

import min3d.Min3d;
import min3d.Shared;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * TextureManager is responsible for managing textures for the whole environment. 
 * It maintains a list of id's that are mapped to the GL texture names (id's).
 * 
 * You add a Bitmap to the TextureManager, which adds a textureId to its list.
 * Then, you assign one or more TextureVo's to your Object3d's using id's that 
 * exist in the TextureManager.
 */
public class TextureManager 
{
	private HashMap<String, Integer> _idMap;
	private static int _counter = 1000001;
	
	
	public TextureManager()
	{
		reset();
	}

	public void reset()
	{
		// Delete any extant textures

		if (_idMap != null) 
		{
			Set<String> s = _idMap.keySet();
			Object[] a = s.toArray(); 
			for (int i = 0; i < a.length; i++) {
				int glId = getGlTextureId((String)a[i]);
				Log.v("x", "hello?" + " " + a[i] + glId);
				Shared.renderer().deleteTexture(glId);
			}
			// ...pain
		}
		
		_idMap = new HashMap<String, Integer>();
	}

	/**
	 * 'Uploads' a texture via OpenGL which is mapped to a textureId to the TextureManager, 
	 * which can subsequently be used to assign textures to Object3d's. 
	 * 
	 * @return The textureId as added to TextureManager, which is identical to $id 
	 */
	public String addTextureId(Bitmap $b, String $id)
	{
		if (_idMap.containsKey($id)) throw new Error("Texture id \"" + $id + "\" already exists."); 

		int glId = Shared.renderer().uploadTextureAndReturnId($b);

		String s = $id;
		_idMap.put(s, glId);
		
		_counter++;
		
		logContents();
		
		return s;
	}
	
	/**
	 * 'Uploads' texture via OpenGL and returns an autoassigned textureId,
	 * which can be used to assign textures to Object3d's. 
	 */
	public String createTextureId(Bitmap $b)
	{
		return addTextureId($b, (_counter+""));
	}
	
	/**
	 * Deletes a textureId from the TextureManager,  
	 * and deletes the corresponding texture from the GPU
	 */
	public void deleteTexture(String $textureId)
	{
		int glId = _idMap.get($textureId);
		Shared.renderer().deleteTexture(glId);
		_idMap.remove($textureId);
		
		logContents();		
	}

	/**
	 * Returns a String Array of textureId's in the TextureManager 
	 */
	public String[] getTextureIds()
	{
		Set<String> set = _idMap.keySet();
		String[] a = new String[set.size()];
		set.toArray(a);
		return a;
	}
	
	/**
	 * Used by Renderer
	 */
	int getGlTextureId(String $textureId) /*package-private*/
	{
		return _idMap.get($textureId);
	}
	
	public boolean contains(String $textureId)
	{
		return _idMap.containsKey($textureId);
	}
	
	
	private String arrayToString(String[] $a)
	{
		String s = "";
		for (int i = 0; i < $a.length; i++)
		{
			s += $a[i].toString() + " | ";
		}
		return s;
	}
	
	private void logContents()
	{
		Log.i(Min3d.TAG, "TextureManager contents updated - " + arrayToString( getTextureIds() ) );		
	}
	
}
