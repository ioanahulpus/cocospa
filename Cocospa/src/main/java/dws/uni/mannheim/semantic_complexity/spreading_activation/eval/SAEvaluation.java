package dws.uni.mannheim.semantic_complexity.spreading_activation.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import com.opencsv.CSVWriter;

import java.util.Set;

import javax.naming.directory.DirContext;

import au.com.bytecode.opencsv.CSVReader;
import dws.uni.mannheim.semantic_complexity.spreading_activation.DecaySetting;
import dws.uni.mannheim.semantic_complexity.spreading_activation.Mode;

public class SAEvaluation {
   
  
  Map<String, Mode> modesIndex;
  
  

  //\alpha
//  final static double GRAPH_DECAY_01  = 0.1;
  // final static double ACTIVATION_GRAPH_DECAY_015 = 0.15;
 // final static double GRAPH_DECAY_02  = 0.2;
    final static double GRAPH_DECAY_025 = 0.25;
//  final static double GRAPH_DECAY_03  = 0.3;
//  final static double GRAPH_DECAY_04  = 0.4;
  final static double GRAPH_DECAY_05  = 0.5;
  final static double GRAPH_DECAY_075  = 0.75;
 // final static double GRAPH_DECAY_09  = 0.9;
  //\betha
//  final static double FIRING_THRESHOLD_00005 = 0.0005;
//  final static double FIRING_THRESHOLD_0001  = 0.001;
//  final static double FIRING_THRESHOLD_00015  = 0.0015;
  final static double FIRING_THRESHOLD_00025  = 0.0025;
//  final static double FIRING_THRESHOLD_0003  = 0.003;
  final static double FIRING_THRESHOLD_0005  = 0.005;
  final static double FIRING_THRESHOLD_00075  = 0.0075;
   final static double FIRING_THRESHOLD_001   = 0.01;
  //\gamma
  final static double TOKEN_DECAY_0995 = 0.995;
  final static double TOKEN_DECAY_085  = 0.85;
  final static double TOKEN_DECAY_075   = 0.75;

  final static double SENT_DECAY_09 = 0.9;
  final static double SENT_DECAY_07  = 0.7;
  final static double SENT_DECAY_05   = 0.5;

  final static double PAR_DECAY_08 = 0.8;
  final static double PAR_DECAY_05  = 0.5;
  final static double PAR_DECAY_025   = 0.25;
   
   
  void  initModes(){
      Set<Mode> modes = new HashSet<>();
      
      DecaySetting NO_DECAY =  new DecaySetting(1.0, 1.0, 1.0);
      DecaySetting NO_SENT_REMEMBER = new DecaySetting(1.0, 0.0, 0.0);
      DecaySetting NO_PAR_REMEMBER = new DecaySetting(1.0, 1.0, 0.0);
      

/*      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
 /*     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 
      
      
   */   
      
      
 /*     
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 

/////////////////////////
      
 /*     modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 

///////////////////
      
  /*    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
/*      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
 */     
   
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 


////////////
      
 /*     
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));
*/

      
 /*     modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 
  */    
      
      modesIndex = new HashMap<>();
      for (Mode m: modes) {
         System.out.println(m.getModeName());
         modesIndex.put(m.getModeName(), m);
      }
      System.out.println("Modes set size: " + modes.size());
      System.out.println("Modes index size: " + modesIndex.size());

   }
  
  public Double[] sumup(Double[] v1, Double[] v2) {
     Double[] res = new Double[v1.length];
     for (int i = 0; i<v1.length; i++)
        res[i] = v1[i]+v2[i];
     return res;
     
  }
  
  
  public double percentCorrectPairs (double[] values) {
     int countOrdered = 0;
     for (int i=0; i<values.length-1; i++) {
        for (int j=i+1; j<values.length; j++) {
           if (values[i] <= values[j]) countOrdered ++;
        }
     }
     double result =  2.0*countOrdered/(values.length*(values.length-1));
     return result;
  }
   
   public void evalPerMode() {
      
      Set<String> titles = new HashSet<String>();
      try{
         File file = new File("all5levels_1.txt");
         FileReader fileReader = new FileReader(file);
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         String line;
         while ((line = bufferedReader.readLine()) != null) {
            titles.add(line);
         }
         fileReader.close();
      }catch(Exception ex){
         ex.printStackTrace();
      }
      File dirRawRes = new File ("results/spreading_Activation/raw");
      Map<String, Map<Mode, Double[]>> kendallscoresPerTitlePerMode = new HashMap<>();
      Map<String, Map<Mode, Double[]>> spearmanscoresPerTitlePerMode = new HashMap<>();
      try {   
   //   Map<String, Map<Mode, Double[][]>> rawResultsperTitlePerMode = new HashMap<>();
      for (String title : titles) {
         Map<Mode, Double[][]> rawResultsPerMode = new HashMap<>();         
         for (File f: dirRawRes.listFiles()) {
            if (f.getName().startsWith(title)) {
               CSVReader reader = new CSVReader(new FileReader(f));
               String[] values ;
               while(null!=(values = reader.readNext())) {
                  Double[] resultsPerNewselaLevel = new Double[8];
                  double graphdecay = Double.valueOf(values[1]);
                  double firingThresh = Double.valueOf(values[2]);
                  double rdToken = Double.valueOf(values[3]);
                  double rdSent = Double.valueOf(values[4]);
                  double rdPar = Double.valueOf(values[5]);
                  boolean usesExcl = Boolean.valueOf(values[6]);
                  boolean usesImp = Boolean.valueOf(values[7]);
                  double AE = Double.valueOf(values[8]);
                  double AEoS = Double.valueOf(values[9]);
                  double AEoP = Double.valueOf(values[10]);
                  double AEoD = Double.valueOf(values[11]);
                  String modeName = values[12];
                  int newselaLevel = Integer.valueOf(values[13]);
                  if (newselaLevel<=4) {
                  //double readingTokenDecay, double readingSentDecay, double readingParDecay, boolean usesImportance, boolean usesExclusivity
                     if (modesIndex.containsKey(modeName)) {
                  Mode m = this.modesIndex.get(modeName);
                  resultsPerNewselaLevel[0] = AE;
                  resultsPerNewselaLevel[1] = AEoS;
                  resultsPerNewselaLevel[2] = AEoP;
                  resultsPerNewselaLevel[3] = AEoD;
                  resultsPerNewselaLevel[4] = AE+AEoS;
                  resultsPerNewselaLevel[5] = AE+AEoS+AEoP;
                  resultsPerNewselaLevel[6] = AE+AEoP;
                  resultsPerNewselaLevel[7] = AE+AEoS+AEoP+AEoD;
                  if (!rawResultsPerMode.containsKey(m)) {
                     rawResultsPerMode.put(m, new Double[5][8]);
                  }
                  Double[][] tempResPerMode = rawResultsPerMode.get(m);
                  tempResPerMode[newselaLevel] = resultsPerNewselaLevel;
                  rawResultsPerMode.put(m, tempResPerMode);            
                  }
                     else {System.out.println("Mode: " + modeName + " not in index?!");
                  }
               }
            }
         }
       //  rawResultsperTitlePerMode.put(title, rawResultsPerMode);
            if (rawResultsPerMode.size() > 0) {
         Map<Mode, Double[]> kendallscoresPerMode = new HashMap<>();
         Map<Mode, Double[]> spearmanscoresPerMode = new HashMap<>();
         for (Entry<Mode, Double[][]> rawRes : rawResultsPerMode.entrySet()) {
            Double[][] values = rawRes.getValue();
            Double[] kendallscores = new Double[8];
            Double[] spearmanscores = new Double[8];
            double[] newsela = new double[5];
            
            for (int i=0; i<values.length; i++) {
               newsela[i] = i ;
            }
            for (int j = 0; j<values[0].length; j++) {
               double[] rawscores = new double[5];
               for (int i = 0; i< values.length; i++) {
                  rawscores[i]= values[i][j];
               }
               KendallsCorrelation corrk = new KendallsCorrelation();
               double kendallscore = corrk.correlation(newsela, rawscores);
               kendallscores[j] = kendallscore;
               //scores[j] = this.percentCorrectPairs(rawscores);
               SpearmansCorrelation corrs = new SpearmansCorrelation();
               double spearcorr = corrs.correlation(newsela, rawscores);
               spearmanscores[j] = spearcorr;
            }   
            kendallscoresPerMode.put(rawRes.getKey(), kendallscores);
            spearmanscoresPerMode.put(rawRes.getKey(), spearmanscores);
         }
         kendallscoresPerTitlePerMode.put(title, kendallscoresPerMode);
         spearmanscoresPerTitlePerMode.put(title, spearmanscoresPerMode);
      }
         }
      }
      
   }catch (Exception ex) {
         ex.printStackTrace();
      }
      
      Map<Mode, Double[]> avgKendallScorePerMode = new HashMap<>();
      int n=0;
      for(Entry<String, Map<Mode, Double[]>> kpte: kendallscoresPerTitlePerMode.entrySet()) {
         n++;
         for (Entry<Mode, Double[]> e: kpte.getValue().entrySet()) {
            if (!avgKendallScorePerMode.containsKey(e.getKey())) {
               avgKendallScorePerMode.put(e.getKey(),  e.getValue());
            }
            else
               avgKendallScorePerMode.put(e.getKey(), this.sumup(avgKendallScorePerMode.get(e.getKey()), e.getValue()));
         }
      }
      for (Mode m: avgKendallScorePerMode.keySet()) {
            Double[] avg =  avgKendallScorePerMode.get(m);
            for(int i = 0; i< avg.length; i++) {
               avg[i] = avg[i]/n;
            }
            avgKendallScorePerMode.put(m,avg);
      }
      
      
      Map<Mode, Double[]> avgSpearmanScorePerMode = new HashMap<>();
      int s=0;
      for(Entry<String, Map<Mode, Double[]>> kpte: spearmanscoresPerTitlePerMode.entrySet()) {
         s++;
         for (Entry<Mode, Double[]> e: kpte.getValue().entrySet()) {
            if (!avgSpearmanScorePerMode.containsKey(e.getKey())) {
               avgSpearmanScorePerMode.put(e.getKey(),  e.getValue());
            }
            else
               avgSpearmanScorePerMode.put(e.getKey(), this.sumup(avgSpearmanScorePerMode.get(e.getKey()), e.getValue()));
         }
      }
      for (Mode m: avgSpearmanScorePerMode.keySet()) {
            Double[] avg =  avgSpearmanScorePerMode.get(m);
            for(int i = 0; i< avg.length; i++) {
               avg[i] = avg[i]/s;
            }
            avgSpearmanScorePerMode.put(m,avg);
      }
      
      
      try {
         CSVWriter writer = new CSVWriter(new FileWriter(new File ("results/spreading_Activation/eval", "spearmanPerMode.csv")));
         for (Entry<Mode, Double[]> e: avgSpearmanScorePerMode.entrySet()) {
            String[] out = new String[17];
            out[0] = e.getKey().getModeName();
            out[1] = String.valueOf(e.getKey().getGraphDecay());
            out[2] = String.valueOf(e.getKey().getFiringThreshold());
            out[3] = String.valueOf(e.getKey().getReadingTokenDecay());
            out[4] = String.valueOf(e.getKey().getReadingSentenceDecay());
            out[5] = String.valueOf(e.getKey().getReadingParagraphDecay());
            out[6] = String.valueOf(e.getKey().usesExclusivity());
            out[7] = String.valueOf(e.getKey().usesImportance());
            for (int i = 0; i<e.getValue().length; i++) {
               out[8+i] = String.valueOf(e.getValue()[i]);
            }
            writer.writeNext(out);
         }
         writer.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
      }
      
      
      try {
         CSVWriter writer = new CSVWriter(new FileWriter(new File ("results/spreading_Activation/eval", "kendallPerMode.csv")));
         for (Entry<Mode, Double[]> e: avgKendallScorePerMode.entrySet()) {
            String[] out = new String[17];
            out[0] = e.getKey().getModeName();
            out[1] = String.valueOf(e.getKey().getGraphDecay());
            out[2] = String.valueOf(e.getKey().getFiringThreshold());
            out[3] = String.valueOf(e.getKey().getReadingTokenDecay());
            out[4] = String.valueOf(e.getKey().getReadingSentenceDecay());
            out[5] = String.valueOf(e.getKey().getReadingParagraphDecay());
            out[6] = String.valueOf(e.getKey().usesExclusivity());
            out[7] = String.valueOf(e.getKey().usesImportance());
            for (int i = 0; i<e.getValue().length; i++) {
               out[8+i] = String.valueOf(e.getValue()[i]);
            }
            writer.writeNext(out);
         }
         writer.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
      }
      
      
      try {
      
      CSVWriter writer = new CSVWriter(new FileWriter(new File ("results/spreading_Activation/eval", "spearmanPerFilePerMode.csv")));
      for (Entry<String, Map<Mode, Double[]>> e : spearmanscoresPerTitlePerMode.entrySet()) {
         for(Entry<Mode, Double[]> ee: e.getValue().entrySet()) {
            String[] out = new String[17];
            out[0] = e.getKey();
            out[1] = ee.getKey().getModeName();
            out[2] = String.valueOf(ee.getKey().getGraphDecay());
            out[3] = String.valueOf(ee.getKey().getFiringThreshold());
            out[4] = String.valueOf(ee.getKey().getReadingTokenDecay());
            out[5] = String.valueOf(ee.getKey().getReadingSentenceDecay());
            out[6] = String.valueOf(ee.getKey().getReadingParagraphDecay());
            out[7] = String.valueOf(ee.getKey().usesExclusivity());
            out[8] = String.valueOf(ee.getKey().usesImportance());
            for (int i = 0; i<ee.getValue().length; i++) {
               out[9+i] = String.valueOf(ee.getValue()[i]);
            }
            writer.writeNext(out);
           }
         
      }
      writer.close();
      }catch(Exception ex) {
         ex.printStackTrace();
      }
      
      
      try {
         
         CSVWriter writer = new CSVWriter(new FileWriter(new File ("results/spreading_Activation/eval", "kendallPerFilePerMode.csv")));
         for (Entry<String, Map<Mode, Double[]>> e : kendallscoresPerTitlePerMode.entrySet()) {
            for(Entry<Mode, Double[]> ee: e.getValue().entrySet()) {
               String[] out = new String[17];
               out[0] = e.getKey();
               out[1] = ee.getKey().getModeName();
               out[2] = String.valueOf(ee.getKey().getGraphDecay());
               out[3] = String.valueOf(ee.getKey().getFiringThreshold());
               out[4] = String.valueOf(ee.getKey().getReadingTokenDecay());
               out[5] = String.valueOf(ee.getKey().getReadingSentenceDecay());
               out[6] = String.valueOf(ee.getKey().getReadingParagraphDecay());
               out[7] = String.valueOf(ee.getKey().usesExclusivity());
               out[8] = String.valueOf(ee.getKey().usesImportance());
               for (int i = 0; i<ee.getValue().length; i++) {
                  out[9+i] = String.valueOf(ee.getValue()[i]);
               }
               writer.writeNext(out);
              }
            
         }
         writer.close();
         }catch(Exception ex) {
            ex.printStackTrace();
         }
}
   
   
   public static void main(String[] args) {
      SAEvaluation eval = new SAEvaluation();
      eval.initModes();
      eval.evalPerMode();
   }
}
