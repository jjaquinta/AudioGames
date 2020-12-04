package jo.audio.thieves.tools.editor.data;

import java.io.File;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.util.beans.PCSBean;

public class EditorSettings extends PCSBean
{
    private File                    mLibraryFile;
    private PLibrary                mLibrary;
    private String                  mSelectedCategory;
    private PSquare                 mSelectedSquare;
    private PApature                mSelectedApature;
    // persistent settings
    private PTemplate               mSelectedHouse;

    // utilities

    // getters and setters
    
    public File getLibraryFile()
    {
        return mLibraryFile;
    }
    public void setLibraryFile(File libraryFile)
    {
        queuePropertyChange("libraryFile", mLibraryFile, libraryFile);
        mLibraryFile = libraryFile;
        firePropertyChange();
    }
    public PLibrary getLibrary()
    {
        return mLibrary;
    }
    public void setLibrary(PLibrary library)
    {
        queuePropertyChange("library", mLibrary, library);
        mLibrary = library;
        firePropertyChange();
    }
    public PSquare getSelectedSquare()
    {
        return mSelectedSquare;
    }
    public void setSelectedSquare(PSquare selectedSquare)
    {
        queuePropertyChange("selectedSquare", mSelectedSquare, selectedSquare);
        mSelectedSquare = selectedSquare;
        firePropertyChange();
    }
    public PApature getSelectedApature()
    {
        return mSelectedApature;
    }
    public void setSelectedApature(PApature selectedApature)
    {
        queuePropertyChange("selectedApature", mSelectedApature, selectedApature);
        mSelectedApature = selectedApature;
        firePropertyChange();
    }
    public PTemplate getSelectedHouse()
    {
        return mSelectedHouse;
    }
    public void setSelectedHouse(PTemplate selectedHouse)
    {
        queuePropertyChange("selectedHouse", mSelectedHouse, selectedHouse);
        mSelectedHouse = selectedHouse;
        firePropertyChange();
    }
    public String getSelectedCategory()
    {
        return mSelectedCategory;
    }
    public void setSelectedCategory(String selectedCategory)
    {
        queuePropertyChange("selectedCategory", mSelectedCategory, selectedCategory);
        mSelectedCategory = selectedCategory;
        firePropertyChange();
    }
}
