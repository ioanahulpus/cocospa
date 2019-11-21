package dws.uni.mannheim.relatedness;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import dws.uni.mannheim.semantic_complexity.spreading_activation.Mode;


/*
graph_decay = [0.25, 0.5, 0.75]
firing_threshold = [0.0025, 0.005, 0.0075, 0.01]
token_decay = [0.995, 0.85, 0.75, 1] 
sent_decay = [0.9, 0.7, 0.5, 1]
par_decay = [0.8, 0.5, 0.25, 1]

use_importance = true
use_exclusivity = true

nr_threads = 5

 */

public class SettingsReader
{
    
    public double GRAPH_DECAY = 0.5;
    public double FIRING_THRESH = 0.01;
    public double TOKEN_DECAY = 0.85;
    public double SENT_DECAY = 0.7;
    public double PAR_DECAY = 0.5;

    public boolean USE_IMPORTANCE = true;
    public boolean USE_EXCLUSIVITY = false;

    public int NR_THREADS = 3;

    
    
    public SettingsReader(String settingsFilePath) throws Exception
    {
        FileInputStream in = new FileInputStream(new File(settingsFilePath));
        StringBuilder sb = new StringBuilder(512);
        try
        {
            Reader r = new InputStreamReader(in, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1)
            {
                sb.append((char) c);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        String text = sb.toString();
        text = text.toLowerCase();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; ++i)
        {
            
            if (lines[i].trim().isEmpty() || lines[i].charAt(0) == '#'){
                continue;
            }
            String[] parts = lines[i].split("=");
            if (parts.length != 2)
            {
                throw new Exception("Can't understand setting on line " + i
                        + " from settings file: " + lines[i]);
            }
            parts[1] = parts[1].trim();
            switch (parts[0].trim())
            {
            case "graph_decay":
                this.GRAPH_DECAY = Double.parseDouble(parts[1]);
                break;
            case "firing_threshold":
                this.FIRING_THRESH = Double.parseDouble(parts[1]);
                break;
            case "token_decay":
                this.TOKEN_DECAY = Double.parseDouble(parts[1]);
                break;
            case "sentence_decay":
                this.SENT_DECAY = Double.parseDouble(parts[1]);
                break;
            case "paragraph_decay":
                this.PAR_DECAY = Double.parseDouble(parts[1]);
                break;
            case "use_importance":
                this.USE_IMPORTANCE = Boolean.parseBoolean(parts[1]);
                break;
            case "use_exclusivity":
                this.USE_EXCLUSIVITY = Boolean.parseBoolean(parts[1]);
                break;
            case "nr_threads":
                this.NR_THREADS = Integer.parseInt(parts[1]);
                break;
            default:
                System.out.println("No such setting " + parts[0]);
            }
            
        }

    }

}
