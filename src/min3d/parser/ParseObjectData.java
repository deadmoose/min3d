package min3d.parser;

import java.util.ArrayList;

import min3d.core.Object3d;
import min3d.parser.AParser.BitmapAsset;
import min3d.parser.AParser.TextureAtlas;
import min3d.vos.Color4;
import min3d.vos.Face;
import min3d.vos.Number3d;
import min3d.vos.Uv;

public class ParseObjectData {
	protected ArrayList<ParseObjectFace> faces;
	protected int numFaces = 0;
	protected ArrayList<Number3d> vertices;
	protected ArrayList<Uv> texCoords;
	protected ArrayList<Number3d> normals;
	
	public String name;

	public ParseObjectData(ArrayList<Number3d> vertices, ArrayList<Uv> texCoords, ArrayList<Number3d> normals)
	{
		this.vertices = vertices;
		this.texCoords = texCoords;
		this.normals = normals;
		this.name = "";
		faces = new ArrayList<ParseObjectFace>();
	}
	
	public Object3d getParsedObject(TextureAtlas textureAtlas) {
		Object3d obj = new Object3d(numFaces * 3, numFaces);
		obj.name(name);

		int numFaces = faces.size();
		int faceIndex = 0;
		boolean hasBitmaps = textureAtlas.hasBitmaps();

		for (int i = 0; i < numFaces; i++) {
			ParseObjectFace face = faces.get(i);
			BitmapAsset ba = textureAtlas
					.getBitmapAssetByName(face.materialKey);

			for (int j = 0; j < face.faceLength; j++) {
				Number3d newVertex = vertices.get(face.v[j]);
				Uv newUv = face.hasuv ? texCoords.get(face.uv[j]).clone()
						: new Uv();
				Number3d newNormal = face.hasn ? normals.get(face.n[j])
						: new Number3d();
				Color4 newColor = new Color4(255, 255, 0, 255);

				if(hasBitmaps)
				{
					newUv.u = ba.uOffset + newUv.u * ba.uScale;
					newUv.v = ba.vOffset + ((newUv.v + 1) * ba.vScale) - 1;
				}

				obj.meshData().addVertex(newVertex, newUv, newNormal, newColor);
			}

			if (face.faceLength == 3) {
				obj.faces().add(
						new Face(faceIndex, faceIndex + 1, faceIndex + 2));
			} else if (face.faceLength == 4) {
				obj.faces().add(
						new Face(faceIndex, faceIndex + 1, faceIndex + 3));
				obj.faces().add(
						new Face(faceIndex + 1, faceIndex + 2, faceIndex + 3));
			}

			faceIndex += face.faceLength;
		}

		if (hasBitmaps) {
			obj.textures().addById("atlas");
		}

		cleanup();

		return obj;
	}
	
	protected void cleanup() {
		faces.clear();
	}
}
