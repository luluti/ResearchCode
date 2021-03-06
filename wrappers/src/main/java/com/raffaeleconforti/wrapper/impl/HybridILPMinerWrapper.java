package com.raffaeleconforti.wrapper.impl;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.wrapper.MiningAlgorithm;
import com.raffaeleconforti.wrapper.settings.MiningSettings;
import com.raffaeleconforti.wrapper.PetrinetWithMarking;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.causalactivitygraphcreator.algorithms.DiscoverCausalActivityGraphAlgorithm;
import org.processmining.causalactivitygraphcreator.parameters.DiscoverCausalActivityGraphParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.*;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;

import java.util.HashSet;

/**
 * Created by Adriano on 7/12/2016.
 */
public class HybridILPMinerWrapper implements MiningAlgorithm {

    @Override
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params) {
        PetrinetWithMarking petrinet = null;

        try {
            Object result[];

            LPFilter lpFilter = new LPFilter();
            lpFilter.setFilterType(LPFilterType.NONE);

            XEventClassifier eventClassifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());

            DiscoveryStrategy discoveryStrategy = new DiscoveryStrategy(DiscoveryStrategyType.CAUSAL);
            DiscoverCausalActivityGraphParameters graphParams = new DiscoverCausalActivityGraphParameters(log);

            graphParams.setClassifier(eventClassifier);
            graphParams.setMiner("Midi");
            graphParams.setConcurrencyRatio(0);
            graphParams.setIncludeThreshold(0);
            graphParams.setZeroValue(0);
            graphParams.setShowClassifierPanel(false);

            discoveryStrategy.setCausalActivityGraphParameters(graphParams);

//            ConvertCausalActivityMatrixToCausalActivityGraphPlugin causalGraphPlugin = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
//            CausalActivityMatrix matrix = new CausalActivityMatrixFactory().createCausalActivityMatrix();
//            matrix.init("causal-matrix", );
//            CausalActivityGraph causalActivityGraph = causalGraphPlugin.run(context, matrix, graphParams);

            DiscoverCausalActivityGraphAlgorithm algorithm = new DiscoverCausalActivityGraphAlgorithm();
            discoveryStrategy.setCausalActivityGraph(algorithm.apply(context, log, graphParams));

            HashSet<LPConstraintType> lpConstraints = new HashSet<LPConstraintType>();
            lpConstraints.add(LPConstraintType.NO_TRIVIAL_REGION);
            lpConstraints.add(LPConstraintType.THEORY_OF_REGIONS);

            XLogHybridILPMinerParametersImpl iParams = new XLogHybridILPMinerParametersImpl( context,
                                                                                            LPEngine.EngineType.LPSOLVE,
                                                                                            discoveryStrategy,
                                                                                            NetClass.PT_NET,
                                                                                            lpConstraints,
                                                                                            LPObjectiveType.WEIGHTED_ABSOLUTE_PARIKH,
                                                                                            LPVariableType.DUAL,
                                                                                            lpFilter,
                                                                                            true,
                                                                                            log,
                                                                                            eventClassifier);

            result = HybridILPMinerPlugin.applyParams(context, log, iParams);
            System.out.println("DEBUG - trying to set petrinet: " + result);
            if( (result[0] instanceof Petrinet) && (result[1] instanceof Marking) ) {
                petrinet = new PetrinetWithMarking( (Petrinet)result[0], (Marking)result[1], (Marking)result[1]);
            } else {
                System.out.println("ERROR - wrong parameter returned by the Hybrid ILP Miner");
            }

        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
            System.out.println("ERROR - Hybrid ILP Miner failed");
            return petrinet;
        }

        return petrinet;
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Hybrid ILP Miner";
    }

    @Override
    public String getAcronym() { return "HILP";}
}
