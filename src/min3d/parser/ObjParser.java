package min3d.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import min3d.Min3d;
import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.vos.Color4;
import min3d.vos.Face;
import min3d.vos.Number3d;
import min3d.vos.Uv;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Parses Wavefront OBJ files.
 * Basic version, this is still a work in progress!
 * 
 * TODO: proper error handling
 * TODO: handle multiple objects
 * TODO: handle groups
 * TODO: a lot more :-) * 
 * 
 * @author dennis.ippel
 * @see http://en.wikipedia.org/wiki/Obj
 *
 */

public class ObjParser extends AParser implements IParser {
	private final String VERTEX = "v";
	private final String FACE = "f";
	private final String TEXCOORD = "vt";
	private final String NORMAL = "vn";
	private final String MATERIAL_LIB = "mtllib";
	private final String USE_MATERIAL = "usemtl";
	private final String NEW_MATERIAL = "newmtl";
	private final String DIFFUSE_TEX_MAP = "map_Kd";
	
	private ArrayList<ObjFace> faces;
	private HashMap<String, ObjMaterial> materialMap;
	private TextureAtlas textureAtlas;	
	
	/**
	 * Creates a new OBJ parser instance
	 * @param resources
	 * @param resourceID
	 */
	public ObjParser(Resources resources, String resourceID)
	{
		this.resources = resources;
		this.resourceID = resourceID;
		if(resourceID.indexOf(":") > - 1)
			this.packageID = resourceID.split(":")[0];
	}
	
	@Override
	public void parse() {
		InputStream fileIn = resources.openRawResource(resources.getIdentifier(resourceID, null, null));
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileIn));
		String line;
		vertices = new ArrayList<Number3d>();
		faces = new ArrayList<ObjFace>();
		texCoords = new ArrayList<Uv>();
		normals = new ArrayList<Number3d>();
		materialMap = new HashMap<String, ObjMaterial>();
		textureAtlas = new TextureAtlas();
		
		Log.d(Min3d.TAG, "Start parsing object" + resourceID);
		
		try {
			while((line = buffer.readLine()) != null)
			{
				// remove duplicate whitespace
				line = line.replaceAll("\\s+", " ");
				String[] parts = line.split(" ");
				if(parts.length == 0) continue;
				String type = parts[0];
				
				if(type.equals(VERTEX))
				{
					Number3d vertex = new Number3d();
					vertex.x = Float.valueOf(parts[1]).floatValue(); 
					vertex.y = Float.valueOf(parts[2]).floatValue();
					vertex.z = Float.valueOf(parts[3]).floatValue();
					vertices.add(vertex);
				} else if(type.equals(TEXCOORD)) {
					Uv texCoord = new Uv();
					texCoord.u = Float.valueOf(parts[1]).floatValue();
					texCoord.v = Float.valueOf(parts[2]).floatValue() * -1f;
					texCoords.add(texCoord);
				} else if(type.equals(NORMAL)) {
					Number3d normal = new Number3d();
					normal.x = Float.valueOf(parts[1]).floatValue(); 
					normal.y = Float.valueOf(parts[2]).floatValue();
					normal.z = Float.valueOf(parts[3]).floatValue();
					normals.add(normal);
				} else if(type.equals(FACE)) {
					faces.add(new ObjFace(parts, currentMaterialKey));
					if(parts.length == 4) {
						numFaces++;
					} else if(parts.length == 5) {
						numFaces+=2;
					}
				} else if(type.equals(MATERIAL_LIB)) {
					readMaterialLib(parts[1]);
				} else if(type.equals(USE_MATERIAL)) {
					currentMaterialKey = parts[1];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object3dContainer getParsedObject()
	{
		Log.d(Min3d.TAG, "Start object creation");
		Object3dContainer obj = new Object3dContainer(numFaces * 3, numFaces);
		
		int numFaces = faces.size();
		int faceIndex = 0;

		textureAtlas.generate();
		
		for(int i=0; i<numFaces; i++)
		{
			ObjFace face = faces.get(i);
			BitmapAsset ba = textureAtlas.getBitmapAssetByName(face.materialKey);
			
			for(int j=0; j<face.faceLength; j++)
			{
				Number3d 	newVertex = vertices.get(face.v[j]);
				Uv 			newUv = face.hasvt ? texCoords.get(face.vt[j]).clone() : new Uv();
				Number3d 	newNormal = face.hasvn ? normals.get(face.vn[j]) : new Number3d();
				Color4		newColor = new Color4(255, 255, 0, 255);
				
				newUv.u = ba.uOffset + newUv.u * ba.uScale;
				newUv.v = ba.vOffset + ((newUv.v + 1) * ba.vScale) - 1;
				
				obj.meshData().addVertex(newVertex, newUv, newNormal, newColor);
			}
			
			if(face.faceLength == 3)
			{
				obj.faces().add(new Face(faceIndex, faceIndex+1, faceIndex+2));
			}
			else if(face.faceLength == 4)
			{
				obj.faces().add(new Face(faceIndex, faceIndex+1, faceIndex+3));	
				obj.faces().add(new Face(faceIndex+1, faceIndex+2, faceIndex+3));
			}
			
			faceIndex += face.faceLength;
		}

		Log.d(Min3d.TAG, "Object creation finished");
		
		Bitmap texture = textureAtlas.getBitmap();
		Shared.textureManager().addTextureId(texture, "atlas");
		/*
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("/data/screenshot.png");
			texture.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		texture.recycle();
		obj.textures().addById("atlas");
		
		cleanup();
		
		return obj;
	}
	
	private void readMaterialLib(String libID)
	{
		StringBuffer resourceID = new StringBuffer(packageID);
		StringBuffer libIDSbuf = new StringBuffer(libID);
		int dotIndex = libIDSbuf.lastIndexOf(".");
		if(dotIndex > -1)
			libIDSbuf = libIDSbuf.replace(dotIndex, dotIndex + 1, "_");
		
		resourceID.append(":raw/");
		resourceID.append(libIDSbuf.toString());
 
		InputStream fileIn = resources.openRawResource(resources.getIdentifier(resourceID.toString(), null, null));
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileIn));
		String line;
		String currentMaterial = "";
	
		try {
			while((line = buffer.readLine()) != null)
			{
				String[] parts = line.split(" ");
				if(parts.length == 0) continue;
				String type = parts[0];
				
				if(type.equals(NEW_MATERIAL))
				{
					if(parts.length > 1)
					{
						currentMaterial = parts[1];
						materialMap.put(currentMaterial, new ObjMaterial(currentMaterial));
					}
				} else if(type.equals(DIFFUSE_TEX_MAP)) {
					if(parts.length > 1)
					{
						materialMap.get(currentMaterial).diffuseTextureMap = parts[1];
						StringBuffer texture = new StringBuffer(packageID);
						texture.append(":drawable/");

						StringBuffer textureName = new StringBuffer(parts[1]);
						dotIndex = textureName.lastIndexOf(".");
						if(dotIndex > -1)
							texture.append(textureName.substring(0, dotIndex));
						else
							texture.append(textureName);

						int bmResourceID = resources.getIdentifier(texture.toString(), null, null);
						Bitmap b = Utils.makeBitmapFromResourceId(bmResourceID);
						textureAtlas.addBitmapAsset(new BitmapAsset(b, currentMaterial));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void cleanup()
	{
		vertices.clear();
		faces.clear();
		texCoords.clear();
		normals.clear();
		materialMap.clear();
	}
	
	private class ObjFace {
		public short[] v;
		public short[] vt;
		public short[] vn;
		public int faceLength;
		public boolean hasvt;
		public boolean hasvn;
		public String materialKey;

        public ObjFace(String[] parts, String materialKey)
        {
        	this.materialKey = materialKey;
        	faceLength = parts.length - 1;
        	int partLength = parts[1].split("/").length;
        	boolean emptyVt = parts[1].indexOf("//") > -1;
        	hasvt = partLength >= 2 && !emptyVt;
        	hasvn = partLength == 3;        	
        	
        	v = new short[faceLength];
        	if(hasvt) vt = new short[faceLength];
        	if(hasvn) vn = new short[faceLength];
        	
        	for(int i=1; i<faceLength+1; i++)
        	{
        		String[] subParts = parts[i].split("/");
        		int index = i-1;
        		v[index] = (short) (Short.valueOf(subParts[0]).shortValue() - (short)1);
        		if(hasvt) vt[index] = (short) (Short.valueOf(subParts[1]).shortValue() - (short)1);
        		if(hasvn) vn[index] = (short) (Short.valueOf(subParts[2]).shortValue() - (short)1);
        	}
        }
    }
	
	private class ObjMaterial
	{
		public String name;
		public String diffuseTextureMap;
		public float offsetU;
		public float offsetV;
		
		public ObjMaterial(String name)
		{
			this.name = name;
		}
	}
}
