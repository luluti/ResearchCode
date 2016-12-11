package com.raffaeleconforti.wrapper;

import com.raffaeleconforti.memorylog.XAttributeLiteralImpl;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import com.raffaeleconforti.memorylog.XFactoryMemoryImpl;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.log.logfilters.LogFilter;
import org.processmining.plugins.log.logfilters.LogFilterException;
import org.processmining.plugins.log.logfilters.XTraceEditor;

import java.util.ArrayList;

/**
 * Created by conforti on 28/01/2016.
 */
public class LogPreprocessing {

    XEvent start;
    XEvent end;
    final XFactory factory = new XFactoryMemoryImpl();
    final XConceptExtension xce = XConceptExtension.instance();
    final XTimeExtension xte = XTimeExtension.instance();
    final XLifecycleExtension xle = XLifecycleExtension.instance();

    public XLog preprocessLog(UIPluginContext context, XLog log) {
        ArrayList<String> classifiers = new ArrayList<String>();
        for(XEventClassifier c : log.getClassifiers()) {
            for(String s : c.getDefiningAttributeKeys()) {
                classifiers.add(s);
            }
        }

        start = createStartEvent(classifiers);
        end = createEndEvent(classifiers);
        return addArtificialStartAndEndEvents(context, log);
    }

    public void removedAddedElements(Petrinet petrinet) {
        for (Transition t : petrinet.getTransitions()) {
            if (t.getLabel().contains("###$$$%%%$$$###START###$$$%%%$$$###")) {
                t.setInvisible(true);
                t.getAttributeMap().put("ProM_Vis_attr_label", "source");
            } else if (t.getLabel().contains("###$$$%%%$$$###END###$$$%%%$$$###")) {
                t.setInvisible(true);
                t.getAttributeMap().put("ProM_Vis_attr_label", "sink");
            } else if (t.getLabel().toLowerCase().endsWith("+complete")) {
                t.getAttributeMap().put("ProM_Vis_attr_label", t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+complete")));
            } else if (t.getLabel().toLowerCase().endsWith("+start")) {
                t.getAttributeMap().put("ProM_Vis_attr_label", t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+start")));
            }
        }
    }

    private XEvent createStartEvent(ArrayList<String> classifiers) {
        XEvent start = factory.createEvent();
        for(String s : classifiers) {
            XAttributeLiteralImpl a = new XAttributeLiteralImpl(s, "###$$$%%%$$$###START###$$$%%%$$$###");
            start.getAttributes().put(s, a);
        }
        xce.assignName(start, "###$$$%%%$$$###START###$$$%%%$$$###");
        xte.assignTimestamp(start, 1L);
        xle.assignStandardTransition(start, XLifecycleExtension.StandardModel.COMPLETE);
        return start;
    }

    private XEvent createEndEvent(ArrayList<String> classifiers) {
        XEvent end = factory.createEvent();
        for (String s : classifiers) {
            XAttributeLiteralImpl a = new XAttributeLiteralImpl(s, "###$$$%%%$$$###END###$$$%%%$$$###");
            end.getAttributes().put(s, a);
        }
        xce.assignName(end, "###$$$%%%$$$###END###$$$%%%$$$###");
        xte.assignTimestamp(end, Long.MAX_VALUE);
        xle.assignStandardTransition(end, XLifecycleExtension.StandardModel.COMPLETE);
        return end;
    }

    private XLog addArtificialStartAndEndEvents(UIPluginContext context, XLog log) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(0, start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }

    private XLog addArtificialStartAndEndEvents(UIPluginContext context, XLog log, XEventClassifier xEventClassifier) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log, xEventClassifier),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(0, start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log, xEventClassifier),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }

    private XLog removeArtificialStartAndEndEvents(UIPluginContext context, XLog log) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.remove(start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.remove(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }
}
