package com.raffaeleconforti.wrapper.impl.inductive;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.wrapper.LogPreprocessing;
import com.raffaeleconforti.wrapper.MiningAlgorithm;
import com.raffaeleconforti.wrapper.settings.MiningSettings;
import com.raffaeleconforti.wrapper.PetrinetWithMarking;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;

import java.io.*;

/**
 * Created by conforti on 20/02/15.
 */
@Plugin(name = "Inductive Miner IMf Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class InductiveMinerIMfWrapper implements MiningAlgorithm {

    MiningParameters miningParameters;

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@qut.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Inductive Miner IMf Wrapper", requiredParameterLabels = {0})
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log) {
        return minePetrinet(context, log, false, null);
    }

    @Override
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params) {
        LogPreprocessing logPreprocessing = new LogPreprocessing();
        log = logPreprocessing.preprocessLog(context, log);

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {}
        }));

        IMPetriNet miner = new IMPetriNet();
        if(miningParameters == null) {
            miningParameters = new MiningParametersIMf();
        }

        if( params != null ) {
            if( params.containsParam("noiseThresholdIMf") && params.getParam("noiseThresholdIMf") instanceof Float )
                miningParameters.setNoiseThreshold((Float)params.getParam("noiseThresholdIMf"));
        }

        Object[] result = miner.minePetriNetParameters(context, log, miningParameters);
        logPreprocessing.removedAddedElements((Petrinet) result[0]);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return new PetrinetWithMarking((Petrinet) result[0], (Marking) result[1], (Marking)result[2]);
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Inductive Miner - infrequent";
    }

    @Override
    public String getAcronym() { return "IMf";}
}
