COMMENT

   **************************************************
   File generated by: neuroConstruct v1.4.1 
   **************************************************

   This file holds the implementation in NEURON of the Cell Mechanism:
   NMDA (Type: Synaptic mechanism, Model: Template based ChannelML file)

   with parameters: 
   /channelml/@units = SI Units 
   /channelml/notes = ChannelML file describing a synaptic mechanism 
   /channelml/synapse_type/@name = NMDA 
   /channelml/synapse_type/status/@value = stable 
   /channelml/synapse_type/status/contributor/name = Padraig Gleeson 
   /channelml/synapse_type/notes = Example of an NMDA receptor synaptic mechanism, based on Maex DeSchutter 1998, Gabbiani et al, 1994 
   /channelml/synapse_type/publication[1]/fullTitle = Gabbiani F, Midtgaard J, Knopfel T. Synaptic integration in a model of cerebellar granule cells. 
   /channelml/synapse_type/publication[1]/pubmedRef = http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pubmed&amp;cmd=Retrieve&amp;dopt=AbstractPlus&amp;list_uids=7527078 
   /channelml/synapse_type/publication[2]/fullTitle = Maex, R and De Schutter, E.              Synchronization of Golgi and Granule Cell Firing in a Detailed Network Model of the              cerebellar G ... 
   /channelml/synapse_type/publication[2]/pubmedRef = http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=9819260&amp;dopt=Abstract 
   /channelml/synapse_type/neuronDBref/modelName = Receptor properties 
   /channelml/synapse_type/neuronDBref/uri = http://senselab.med.yale.edu/senselab/NeuronDB/receptors2.asp 
   /channelml/synapse_type/blocking_syn/@max_conductance = 1.873087796e-10 
   /channelml/synapse_type/blocking_syn/@rise_time = 1e-3 
   /channelml/synapse_type/blocking_syn/@decay_time = 13.3333e-3 
   /channelml/synapse_type/blocking_syn/@reversal_potential = 0 
   /channelml/synapse_type/blocking_syn/block/@species = mg 
   /channelml/synapse_type/blocking_syn/block/@conc = 1.2 
   /channelml/synapse_type/blocking_syn/block/@eta = 0.5206857564 
   /channelml/synapse_type/blocking_syn/block/@gamma = 62 

// File from which this was generated: /home/padraig/neuroConstruct/testProjects/TestHDF5/cellMechanisms/NMDA/NMDA.xml

// XSL file with mapping to simulator: /home/padraig/neuroConstruct/testProjects/TestHDF5/cellMechanisms/NMDA/ChannelML_v1.8.1_NEURONmod.xsl

ENDCOMMENT


?  This is a NEURON mod file generated from a ChannelML file

?  Unit system of original ChannelML file: SI Units

COMMENT
    ChannelML file describing a synaptic mechanism
ENDCOMMENT

? Creating NMDA like synaptic mechanism, based on NEURON source impl of Exp2Syn
    

TITLE Channel: NMDA

COMMENT
    Example of an NMDA receptor synaptic mechanism, based on Maex DeSchutter 1998, Gabbiani et al, 1994
ENDCOMMENT


UNITS {
    (nA) = (nanoamp)
    (mV) = (millivolt)
    (uS) = (microsiemens)
}

    
NEURON {
    POINT_PROCESS NMDA
    RANGE tau_rise, tau_decay 
    GLOBAL total
    

    RANGE mg_conc, eta, gamma, gblock
    GLOBAL total


    RANGE i, e, gmax
    NONSPECIFIC_CURRENT i
    RANGE g, factor

}

PARAMETER {
    gmax = 0.0001873087796
    tau_rise = 1 (ms) <1e-9,1e9>
    tau_decay = 13.3333 (ms) <1e-9,1e9>
    e = 0  (mV)
mg_conc = 1.2 
              
    eta = 0.5206857564 
              
    gamma = 0.062
}


ASSIGNED {
    v (mV)
    i (nA)
    g (uS)
    factor 
    total (uS)
    gblock
}

STATE {
    A (uS)
    B (uS)
}

INITIAL {
    LOCAL tp
    total = 0
    
    if (tau_rise == 0) {
        tau_rise = 1e-9  : will effectively give a single exponential timecourse synapse
    }
    
    if (tau_rise/tau_decay > .999999) {
        tau_rise = .999999*tau_decay : will result in an "alpha" synapse waveform
    }
    A = 0
    B = 0
    tp = (tau_rise*tau_decay)/(tau_decay - tau_rise) * log(tau_decay/tau_rise)
    factor = -exp(-tp/tau_rise) + exp(-tp/tau_decay)
    factor = 1/factor
}

BREAKPOINT {
    SOLVE state METHOD cnexp
    gblock = 1 / (1+ (mg_conc * eta * exp(-1 * gamma * v)))
    g = gmax * gblock * (B - A)
    i = g*(v - e)
        

}


DERIVATIVE state {
    A' = -A/tau_rise
    B' = -B/tau_decay 
}

NET_RECEIVE(weight (uS)) {
    
    state_discontinuity(A, A + weight*factor
)
    state_discontinuity(B, B + weight*factor
)

    
    
}

