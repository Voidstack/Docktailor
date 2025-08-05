
package com.enosi.docktailor.common.util;
import com.enosi.docktailor.common.log.Log;
import lombok.Setter;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * File-based Settings Provider.
 */
@Setter
public class FileSettingsProvider
    extends SettingsProviderBase
{
	protected static final Log log = Log.get("FileSettingsProvider");
	private File file;
	
	public FileSettingsProvider(File f)
	{
		setFile(f);
	}


    @Override
	protected void saveSettings()
	{
		try
		{
			String s = asString();
			CKit.write(file, s);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}

	@Override
	protected void saveSettings(String fileName){
		try
		{
			String s = asString();

			File target = (new File(fileName));
/*			FileSettingsProvider p = new FileSettingsProvider(target);
			p.loadQuiet();*/
			CKit.write(target, s);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}

	/**
	 * ATTENTION : la méthode ne reset pas la save mais simplement les données au runtime.
	 * Important pour éviter des bugs avec l'ouverture de fenêtre.
	 */
	@Override
	protected void resetRuntimeSettings() {
		data.clear();
	}

	public void load() throws Exception
	{
		try
		{
			String s = CKit.readString(file);
			loadFromString(s);
		}
		catch(FileNotFoundException ignore)
		{
		}
	}
	
	
	public void loadQuiet()
	{
		try
		{
			load();
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}
	
	
	public void load(File f) throws Exception
	{
		setFile(f);
		load();
	}
	
	
	public void loadQuiet(File f)
	{
		try
		{
			load(f);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}
}
