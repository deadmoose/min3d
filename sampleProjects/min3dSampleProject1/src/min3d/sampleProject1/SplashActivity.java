package min3d.sampleProject1;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;


/**
 * Main menu activity
 * 
 * @author Lee
 */
public class SplashActivity extends ListActivity  
{
	private final int CONTEXTID_VIEWFILE = 0;
	private final int CONTEXTID_CANCEL = 1;
	
	private String _basePath = "http://code.google.com/p/min3d/source/browse/trunk/sampleProjects/min3dSampleProject1/src/min3d/sampleProject1/"; 
	
	private String[] _sourceFiles = {
		"ExampleRotatingPlanets.java", 
		"ExampleMostMinimal.java",
		"ExampleVertexColors.java",
		"ExampleTextures.java",
		"ExampleVerticesVariations.java",
		"ExampleRenderType.java",
		"ExampleCamera.java",
		"ExampleMultipleLights.java",
		"ExampleAnimatingVertices.java",
		"ExampleSubsetOfFaces.java",
		"ExampleAssigningTexturesDynamically.java",
		"ExampleMipMap.java",
		"ExampleTextureWrap.java",
		"ExampleMultiTexture.java",
		"ExampleTextureOffset.java",
		"ExampleInsideLayout.java",
		"ExampleLoadObjFile.java",
		"ExampleLoadObjFileMultiple.java",
		"ExampleLoad3DSFile.java",
		"ExampleLoadMD2File"
	};
	
	private Class<?>[] _classes = { 
		ExampleRotatingPlanets.class, 
		ExampleMostMinimal.class,
		ExampleVertexColors.class,
		ExampleTextures.class,
		ExampleVerticesVariations.class,
		ExampleRenderType.class,
		ExampleCamera.class,
		ExampleMultipleLights.class,
		ExampleAnimatingVertices.class,
		ExampleSubsetOfFaces.class,
		ExampleAssigningTexturesDynamically.class,
		ExampleMipMap.class,
		ExampleTextureWrap.class,
		ExampleMultiTexture.class,
		ExampleTextureOffset.class,
		ExampleInsideLayout.class,
		ExampleLoadObjFile.class,
		ExampleLoadObjFileMultiple.class,
		ExampleLoad3DSFile.class,
		ExampleLoadMD2File.class
	};
	
	private String[] _strings = {
			"\"Hello, Jupiter\"", 
			"Minimal example", 
			"Vertex colors", 
			"Texture",
			"Usage of Vertices class",		
			"Triangles, lines, points",
			"Camera, frustum (trackball)",
			"Multiple lights",
			"Animating vertices",
			"Rendering subset of faces",
			"Assigning textures dynamically",
			"MIP Mapping (on vs. off)",
			"Texture wrapping",
			"Multiple textures",
			"Texture offset",
			"3D inside layout",
			"Load model from .obj file",
			"Load multiple models from .obj file",
			"Load model from .3ds file",
			"Load animated .md2 file"
			// "Object from scratch"
		};
		
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
	    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _strings));
	    
	    TextView tv = (TextView) this.findViewById(R.id.splashTitle);
	    Linkify.addLinks(tv, 0x07);
	    
	    registerForContextMenu(getListView());	    
	    
	    // xxx
    	// this.startActivity( new Intent(this, ScratchActivity.class ) );
    }
    
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
    	this.startActivity( new Intent(this, _classes[position] ) );
    }
    
    //
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);

        int i = 0;
        menu.add(0, 0, i++, "project home");
        menu.add(0, 1, i++, "author blog");

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	Intent i;
    	
        switch (item.getItemId()) 
        {
            case 0:
            	i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse( this.getResources().getString(R.string.projectUrl) ));
            	startActivity(i);                
            	return true;
                
            case 1:
            	i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse( this.getResources().getString(R.string.myBlogUrl) ));
            	startActivity(i);                
            	return true;
        }
        return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXTID_VIEWFILE, 0, "View source on Google Code");
		menu.add(0, CONTEXTID_CANCEL, 0, "Cancel");
    }

    @Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) 
		{
			case CONTEXTID_VIEWFILE:
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	String url = _basePath + _sourceFiles[ (int)info.id ];
            	i.setData(Uri.parse(url));
            	startActivity(i);                
				return true;
			case CONTEXTID_CANCEL:
				// do nothing
				return true;
				
			default:
				return super.onContextItemSelected(item);
		}
	}    
}
