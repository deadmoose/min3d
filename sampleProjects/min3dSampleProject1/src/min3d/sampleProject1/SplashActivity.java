package min3d.sampleProject1;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Main menu activity
 * 
 * @author Lee
 */
public class SplashActivity extends ListActivity 
{
	private String[] _strings = {
		"\"Hello, Jupiter\"", 
		"Minimal example", 
		"Vertex colors", 
		"Texture",
		"Points versus triangles",
		"Camera, frustum (trackball)",
		"Animating vertices",
		"Rendering subset of faces",
		"Assigning textures dynamically",
		"Texture wrapping",
		"Multiple textures",
		"Texture offset",
		"Load model from .obj file"
		// "Object from scratch"
	};
	
	private Class<?>[] _classes = { 
		ExampleRotatingPlanets.class, 
		ExampleMostMinimal.class,
		ExampleVertexColors.class,
		ExampleTextures.class,
		ExamplePointsVersusTriangles.class,
		ExampleCamera.class,
		ExampleAnimatingVertices.class,
		ExampleSubsetOfFaces.class,
		ExampleAssigningTexturesDynamically.class,
		ExampleTextureWrap.class,
		ExampleMultiTexture.class,
		ExampleTextureOffset.class,
		ExampleLoadObjFile.class
		// ExampleFromScratch.class
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
	    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _strings));
	    
	    TextView tv = (TextView) this.findViewById(R.id.splashTitle);
	    Linkify.addLinks(tv, 0x07);
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
}
