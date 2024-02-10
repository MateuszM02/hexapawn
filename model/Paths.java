package model;

import java.awt.Image;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public abstract class Paths 
{
    // 1. variable declarations -------------------------------------------------------------------

    private static final String randomMovesDatapath = "data/fullTree3.txt";
    private static final String topMovesDatapath = "data/topTree3.txt";   
    private static final String whitePawnLocalPath = "data/whitePawn.png";
    private static final String blackPawnLocalPath = "data/blackPawn.png";
    private static final String whitePawnWebPath = "https://i.imgur.com/PtwBdaG.png";
    private static final String blackPawnWebPath = "https://i.imgur.com/rd67Lzv.png";
    private static final String settings = "data/settings.txt";

    // 2. decision tree file operations -----------------------------------------------------------

    public static String getTreePath(boolean isRandom)
    {
        return isRandom ? Paths.randomMovesDatapath : Paths.topMovesDatapath;
    }

    /**
     * imports decision tree from file
     * 
     * @param datapath path of file with serialized decision tree
     * @return root of decision tree
     */
    public static Board getBotTree(String datapath) 
    {
        return __getFromFile(Board.class, datapath);
    }

    /**
     * saves given decision tree to file
     * 
     * @param root     root of decision tree to be saved
     * @param datapath path of file in which we will save serialized decision tree
     */
    public static void setBotTree(Board root, String datapath) 
    {
        __saveToFile(root, datapath);
    }

    // 3. icon file imports -----------------------------------------------------------------------

    private static ImageIcon __getPawnIcon(String localPath, String webPath)
    {
        try 
        {
            File file = new File(localPath);
            // if icon doesn't exist locally, import from web and save to file
            if (!file.exists()) 
            {
                file.createNewFile();
                URL url = new URI(webPath).toURL();
                InputStream in = url.openStream();
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) 
                {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            }
            Image image = ImageIO.read(file);
            return new ImageIcon(image);
        } 
        catch (IOException e)
        {
            System.err.println(e.getMessage() + ": " + localPath);
            return null;
        }
        catch (URISyntaxException e)
        {
            e.getStackTrace();
            return null;
        }
    }

    public static ImageIcon getWhitePawnIcon()
    {
        return __getPawnIcon(Paths.whitePawnLocalPath, Paths.whitePawnWebPath);
    }

    public static ImageIcon getBlackPawnIcon()
    {
        return __getPawnIcon(Paths.blackPawnLocalPath, Paths.blackPawnWebPath);
    }

    // 4. settings file imports -------------------------------------------------------------------

    public static void initSettings()
    {
        if (new File(Paths.settings).exists()) // get settings from file if it exists
            Settings.instance = __getFromFile(Settings.class, Paths.settings);
        else // file doesnt exist, create settings with default values
            Settings.instance = new Settings();
    }

    public static void SaveSettings()
    {
        __saveToFile(Settings.instance, Paths.settings);
    }

    // 5. general case read file ------------------------------------------------------------------

    private static <T> T __getFromFile(Class<T> clazz, String path)
    {
        File file = new File(path);
        T result = null;
        try 
        {
            if (!(clazz == Board.class || clazz == Settings.class))
            {
                throw new ClassNotFoundException("Wrong class: " + clazz.getName());
            }
            if (!file.exists()) 
            {
                file.createNewFile();
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                
                if (clazz == Board.class)
                    out.writeObject(new Board());
                else if (clazz == Settings.class)
                    out.writeObject(new Settings());
                out.close();
            } 
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            result = (T) in.readObject();
            in.close();
            return result;
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        return result;
    }

    // 6. general case save file ------------------------------------------------------------------

    private static <T extends Serializable> void __saveToFile(T element, String datapath) 
    {
        File file = new File(datapath);
        try 
        {
            if (!file.exists()) 
                file.createNewFile();
            
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(element);
            out.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}