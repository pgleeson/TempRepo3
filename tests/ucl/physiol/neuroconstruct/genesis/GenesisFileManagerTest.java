/**
 *  neuroConstruct
 *  Software for developing large scale 3D networks of biologically realistic neurons
 * 
 *  Copyright (c) 2009 Padraig Gleeson
 *  UCL Department of Neuroscience, Physiology and Pharmacology
 *
 *  Development of this software was made possible with funding from the
 *  Medical Research Council and the Wellcome Trust
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package ucl.physiol.neuroconstruct.genesis;

import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import test.MainTest;
import ucl.physiol.neuroconstruct.cell.compartmentalisation.GenesisCompartmentalisation;
import ucl.physiol.neuroconstruct.project.*;
import ucl.physiol.neuroconstruct.simulation.*;
import ucl.physiol.neuroconstruct.utils.GeneralUtils;
import static org.junit.Assert.*;

/**
 *
 * @author padraig
 */
public class GenesisFileManagerTest {

    public GenesisFileManagerTest() 
    {
    }


    @Before
    public void setUp() 
    {
        System.out.println("---------------   setUp() GenesisFileManagerTest");
    }
    
    private ProjectManager loadProject(String projectFile) throws ProjectFileParsingException
    {

        ProjectManager pm = new ProjectManager();
        
        pm.loadProject(new File(projectFile));
        
        return pm;
    }


    @Test public void testGenerateMooseScripts() throws ProjectFileParsingException, InterruptedException,  IOException, SimulationDataException, GenesisException
    {
        System.out.println("---  testGenerateMooseScripts...");

        generateScripts(true);
    }

    @Test public void testGenerateGenesisScripts() throws ProjectFileParsingException, InterruptedException,  IOException, SimulationDataException, GenesisException
    {
        System.out.println("---  testGenerateGenesisScripts...");

        generateScripts(false);
    }


    private void generateScripts(boolean moose) throws ProjectFileParsingException, InterruptedException,  IOException, SimulationDataException, GenesisException
    {
        String sim = moose ? "MOOSE":"GENESIS";

        System.out.println("generateScripts for "+sim);

        if (GeneralUtils.isWindowsBasedPlatform() && moose)
        {
            System.out.println("---  Not testing "+sim+" on Windows just yet......");
        }
        
        ProjectManager pm = loadProject("testProjects/TestGenNetworks/TestGenNetworks.neuro.xml");
        
        Project proj = pm.getCurrentProject();
        SimConfig sc = proj.simConfigInfo.getDefaultSimConfig();
                
        pm.doGenerate(sc.getName(), 1234);
        
        int wait = 1000;
        
        if (GeneralUtils.isWindowsBasedPlatform())
            wait = 4000;
        
        while(pm.isGenerating())
        {
            Thread.sleep(wait);
        }
        
        int numGen = proj.generatedCellPositions.getNumberInAllCellGroups();
        
        System.out.println("Project: "+ proj.getProjectFileName()+" loaded and "
                +numGen+" cells generated");
        
        String simName = "TestSim_"+sim;
        
        proj.simulationParameters.setReference(simName);
        
        File simDir = new File(ProjectStructure.getSimulationsDir(proj.getProjectMainDirectory()), simName);
        
        //if (simDir.exists())
        //    simDir.delete();
        
        proj.genesisSettings.setGraphicsMode(GenesisSettings.GraphicsMode.NO_CONSOLE);
        
        proj.genesisFileManager.setQuitAfterRun(true);

        proj.genesisSettings.setMooseCompatMode(moose);

        for(int i=0;i<=1;i++)
        {
            proj.genesisSettings.setCopySimFiles((i==0));

            proj.genesisFileManager.generateTheGenesisFiles(sc, null, new GenesisCompartmentalisation(), 1234);

            File mainFile = new File(proj.genesisFileManager.getMainGenesisFileName());

            assertTrue(mainFile.exists());

            System.out.println("Created files for "+sim+", including: "+mainFile);

            proj.genesisFileManager.runGenesisFile();

      
            System.out.println("Run "+sim+" files");


            Thread.sleep(wait);

            SimulationData simData = new SimulationData(simDir, false);

            File timesFile = simData.getTimesFile();

            while (!timesFile.exists())
            {
                System.out.println("Waiting for file to be created: "+ timesFile.getAbsolutePath());
                Thread.sleep(wait);
            }

            assertTrue(timesFile.exists());

            Thread.sleep(wait);

            simData.initialise();

            while(!simData.isDataLoaded())
            {
                System.out.println("Waiting for data to be loaded");
                Thread.sleep(wait);
            }

            int numRecordings = simData.getCellSegRefs(false).size();

            assertEquals(numRecordings, numGen);


            System.out.println("Have found "+ numRecordings+" recordings in dir: "+ simData.getSimulationDirectory().getAbsolutePath());


            File simDataDir = new File(ProjectStructure.getSimulationsDir(proj.getProjectMainDirectory()), simName);

            SimulationData sd = new SimulationData(simDataDir);

            sd.initialise();

            while(!sd.isDataLoaded())
            {
                System.out.println("Waiting for data to be loaded");
                Thread.sleep(wait);
            }

            Thread.sleep(wait);

            System.out.println("Data saved: "+ sd.getCellSegRefs(true));

            String ref = "Pacemaker_0";


            double[] volts = sd.getVoltageAtAllTimes(ref);
			double[] times = sd.getAllTimes();


            System.out.println("volts: "+ volts.length+ ", times: "+ times.length);

            
            //assertEquals(volts.length, 1 + (sc.getSimDuration()/proj.simulationParameters.getDt()), 0);

            double[] spikeTimes = SpikeAnalyser.getSpikeTimes(volts, times, 0, 0, (float)times[times.length-1]);

            System.out.println("Num spikeTimes for "+sim+": "+ spikeTimes.length);

            int expectedNum = 76; // As checked through gui

            assertEquals(expectedNum, spikeTimes.length);

            System.out.println("+++++++ Done check for sim: "+ sim);

            
        }
        
    }
    

     public static void main(String[] args)
     {
        GenesisFileManagerTest ct = new GenesisFileManagerTest();
        Result r = org.junit.runner.JUnitCore.runClasses(ct.getClass());
        MainTest.checkResults(r);

     }

}