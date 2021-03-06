%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD Organizing Principles']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>Organizing principles of BARD</h2></article>
            <aside class="span2"></aside>
        </div>

    </div>
</div>


<article class="hero-block">
    <div class="container-fluid">
        <div class="lowkey-hero-area">
            <div class="row-fluid">
                <aside class="span2"></aside>
                <article class="span8">


                    <h3>
                        Overview
                    </h3>

                    <p>
                        BARD is organized in a novel way that lets users easily find information about projects, protocols, and experimental results, all while keeping the data organized in a manner that facilitates broader analyses across many datasets. The organizing principle is based around three terms: <a href="#assay_definitions">Assay Definitions</a>, <a href="#experiments">Experiments</a>,  and <a href="#projects">Projects</a>. The relationship of these terms provides both the experimental details associated with any given data point while also providing context for the intended purpose of experiments that were run by data depositors.
                    </p>

                    <IMG SRC="${resource(dir: 'images/bardHomepage', file: 'rdm_overview.png')}" ALIGN="top">

                    <h3 ID="assay_definitions">
                        Assay Definitions
                    </h3>

                    <p>
                        Assay definitions contain information about the protocol used to generate data, as well as the intended biological purpose of that protocol. Assay definitions are a unique, abstract description of a protocol that can be used to gain a particular biological insight; therefore they do not have directly attached data and they do not have any fixed relationship to other protocols. For example, an assay definition would not be termed a “counterscreen assay” or a “confirmatory assay” because depending on how the assay definition is used in relation to others it could be either; it is also not directly associated with any particular set of compounds tested. Thus the same assay definition can be used across many screening campaigns (e.g., a standard cytotoxicity protocol). The biology target associated with an assay definition only describes the hypothesis tested by that specific protocol,  additional inferences (such as the specificity of a tested compound) require additional information obtained by executing other assay definitions.
                    </p>

                    <p>
                        Assay definitions include details such as what was added to each reaction container or well (termed <strong>assay components</strong>); treatment variables such as time, temperature, or volume; how the signal was detected; and the information content of the measured signal (single parameter vs. multiplex, ratio, profile, etc.) In some cases, users can choose to define certain details at the time of data deposition instead of setting one fixed description, such as when the same protocol is sometimes run in 384 well format or 1536 well format.
                    </p>

                    <p>
                        Assay definitions are identified by an <strong>ADID</strong>.
                    </p>



                    <h3 ID="experiments">
                        Experiments
                    </h3>

                    <p>
                        Experiments are the primary way in which data points are deposited and grouped together in BARD. An experiment is one discrete dataset as defined by the submitter, describing the measurements generated by executing a protocol against a set of compounds. Each experiment is therefore associated with exactly one <a href="#assay_definitions">assay definition</a> that provides the context for all the results deposited in that experiment. This association is how BARD finds the experimental results associated with search terms used in queries.
                    </p>

                    <p>
                        Experiments have one or more PubChem SIDs that identify which molecular substances were tested.  In addition, experiments have measurement identifiers that specify what calculated values are being reported for those results, such as percent inhibition, IC50, etc. In most cases these measurements are related: in the case of IC50, many percent measurement values are aggregated to report an IC50. This relationship is important to define when planning to submit results for an experiment. It is used to generate the template for depositing results to BARD and indicates which of these results is considered the priority measurement(s) displayed by BARD. Often, the priority measurement is the result value(s) that a user would examine to determine whether they consider a compound “active.”
                    </p>

                    <p>
                        Experiments are identified by an <strong>EID</strong>. Compounds tested in experiments are identified by their PubChem substance ID, <strong>SID</strong>, and associated PubChem compound structure ID, <strong>CID</strong>.
                    </p>



                    <h3 ID="projects">
                        Projects
                    </h3>
                    <p>
                        Projects are groups of experiments that were executed for a larger purpose such as development of a molecular probe. Projects in BARD capture the order in which experiments were run and the purpose of those experiments in relation to each other and the larger project goal (e.g., “primary screen”, “counterscreen”, “confirmatory screen”). Projects also capture the biological purpose of the group of experiments taken as a whole. No one assay definition can identify the properties of compounds desired for probe or drug development; therefore, goals such as the relevant disease or specific biological target are defined by the project. Auxiliary information such as grant numbers can also be recorded within the project annotations.
                    </p>

                    <p>
                        Note that assay definitions are not directly associated with projects. As described above, the same protocol may be used for a different purpose across multiple projects. For example, the primary screen of one project may be a counterscreen of another project. Projects are defined only by the group of experiments associated to that project. These experiments are associated with assay definitions used to generate the data that are submitted with an experiment. It is only through this indirect relationship that assay definitions are connected to projects. Queries that identify matching projects will return all that project’s experiments and all those experiments’ assay definitions.
                    </p>


                    <p>
                        Projects are identified by a <strong>PID</strong>.
                    </p>

                    <h3 ID="panels">
                        Panels
                    </h3>


                    <p>
                        A panel is a collection of assay definitions.  The grouping can be used for any reason, but one major example is multiplexed protocols,
                        in which multiple biological targets are measured in the same well or with a common protocol, e.g. gene expression array or kinase panel.
                        Another reason to use a panel is because a group of assay definitions are carried out together by a vendor (e.g. a contract research
                        organization's characterization panel).  A set of experiments that were generated for the assay definitions in a panel can be grouped
                        together into panel experiments.  Panel experiments can be used to represent multiple experiments in a project diagram, and in the future
                        can be used for quickly displaying the results of these experiments next to each other.
                    </p>

                    <p>
                        Panels are identified by a <strong>PLID</strong>.
                    </p>





                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>>
