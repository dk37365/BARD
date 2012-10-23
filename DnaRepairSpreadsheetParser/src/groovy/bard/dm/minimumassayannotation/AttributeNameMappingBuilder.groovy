package bard.dm.minimumassayannotation

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
class AttributeNameMappingBuilder {
    Map build() {
        Map attributeNameMapping = ['[detector] assay component (type in)': 'assay component',
                '[detector] assay component role': 'detection role',
                'assay component concentration (type in)': 'assay component concentration',
                'assay component concentration units': 'concentration unit',
                'biological component base name': 'assay component',
                'species': 'species name',
                'assay detection': 'detection method type',
                'assay target type': 'assay type',
                'component name (type in)': 'assay component name',
                'assay readout content': 'assay readout',
                'assay readout type': 'readout type',
                'biochemical': 'biochemical format',
                'biorad chemidocxrs imaging system': 'Biorad Chemidocxrs Imaging System',
                'cell based: lysed cell': 'cell-based format',
                'cell-based: live cell': 'cell-based format',
                'cells/ml': 'cells per milliliter',
                'chemically labeled protein': 'chemically labeled protein',
                'detection instrument': 'detection instrument name',
                'fluorescence:other': 'fluorescence',
                'fm': 'femtomolar',
                'grams-per-liter': 'gram per liter',
                'imaging methods': 'imaging method',
                'in cell analyzer': 'IN cell analyzer',
                'kodak biomax mr-1': 'Kodak Biomax MR-1',
                'luminescence:other': 'luminescence',
                'Microscope cover slip  22 m^2': 'Microscope Cover Slip  22 mm^2',
                'mm': 'millimolar',
                'moles-per-liter': 'molar',
                'ng/ml': 'nanogram per milliliter',
                'nm': 'nanomolar',
                'optical densitometry': 'optical densitometry',
                'oryctolagus cuniculus': 'Oryctolagus Cuniculus',
                'pm': 'picomolar',
                'radiometric': 'radiometry method',
                'signal direction': 'readout signal direction',
                'spectramax plus 384 microplate reader': 'Spectramax Plus 384 Microplate Reader',
                'typhoon 8600 variable mode imager': 'Typhoon 8600 Variable Mode Imager',
                'um': 'micromolar',
                'µm': 'micromolar',
                '# concentration points': 'number of points',
                '# replicates': 'number of replicates',
                'uniprot': 'UniProt',
                'Aequorea Victoria': 'Aequorea victoria',
                'DNA-small molecule': 'DNA-small molecule',
                'DNA-small molecule interaction assay': 'DNA-small molecule interaction assay',
                'IMAP Kinase assay kit': 'IMAP Kinase assay kit',
                'SUMOylation assay': 'SUMOylation assay',
                'fluorescence interference assay': 'fluorescence interference assay',
                'ubiquitination assay': 'ubiquitination assay',
                'CID': 'PubChem CID',
                'BD Bioscience LSR II': 'BD Bioscience LSR II',
                'BioTek Synergy II plate reader': 'BioTek Synergy II plate reader',
                'MOI': 'mode of infection',
                'Microbeta scintillation counter': 'Microbeta scintillation counter',
                'Streptomyces Avidinii': 'Streptomyces avidinii',
                't-4 bacteriophage': 't-4 bacteriophage',
                'ChemBank': 'ChemBank',
                'DTP.NCI': 'Developmental Therapeutics Program, National Cancer Institute',
                'Vanderbilt, Vanderbilt Chemistry': 'Vanderbilt Screening Center for GPCRs, Ion Channels and Transporters',
                'SRI, SRI Screening': 'Southern Research Institute',
                'gram per liter': 'milligram per milliliter',
                '47-mer dsDNA': '47-mer dsDNA',
                'log10 molar': 'log10 molar',
                'negative log10 molar': 'negative log10 molar',
                'number-per-liter': 'number per liter',
                'number-per-well': 'number per well',
                'uIU/mL': 'micro interational unit per milliliter',
                '--': '',
                'Result type': 'result detail',
                'cell line': 'cell line',
                'Fa2N-4': 'Fa2N-4',
                'flow cytometry |  side scatter': 'flow cytometry | side scatter',
//                'purified Salmon sperm DNA': 'purified DNA',
                'µL/well': 'uL/well',
                'µci/mL': 'micro curie per milliliter',
                'µg protein/mL': 'microgram per milliliter',
                'HDAC fluorometric detection Assay Kit': 'HDAC fluorometric detection assay kit',
                'ImageXpress Micro ': 'ImageXpress Micro',
                'single-feature extraction': 'single-feature extraction',
                'tracer': 'tracer',
//                'Tecan Safire 2': 'Tecan Safire 2 ',
                'Amoracia rusticana': 'Amoracia rusticana',
                'Differientated cultured cells': 'differentiated cultured cell',
                'IPTG': 'IPTG',
                'SuperSignal ELISA Femto Luminol/Enhancer Solution': 'SuperSignal ELISA Femto Luminol/Enhancer Solution',
                'U/ml': 'unit per milliliter',
                'ampicillin': 'ampicillin',
                'arabinose': 'arabinose',
                'chloramphenicol': 'chloramphenicol',
                'glucose': 'glucose',
                'uCi': 'microcurie',
                'uL': 'microliter',
                'ug/ml': 'microgram per milliliter',
                'µg/mL': 'microgram per milliliter',
                'OD500': 'optical density',
                'AbsAC40': 'AC40',
                'microscope cover slip  22 m^2': 'Microscope Cover Slip  22 mm^2',
                'Broad': 'Broad Institute',
                'Burnham': 'Sanford-Burnham Center for Chemical Genomics',
                'Emory': 'Emory University Molecular Libraries Screening Center',
                'Hopkins Screening': 'Johns Hopkins Ion Channel Center',
                'Penn': 'The Penn Center for Molecular Discovery',
                'Pitt': 'University of Pittsburgh Molecular Library Screening Center',
                'Scripps': 'The Scripps Research Institute Molecular Screening Center',
                'SRI': 'Southern Research Institute',
                'UNM': 'New Mexico Molecular Libraries Screening Center',
                'Vanderbilt': 'Vanderbilt Screening Center for GPCRs, Ion Channels and Transporters',
                'purified Salmon sperm DNA': 'purified DNA',
                'activity class': 'PubChem Outcome',
                'Activity Summary': 'activity summary',
                'Compound QC': 'QC analyst',
                'Compound Type': 'macromolecule type',
                'curve class': 'curve-fit class',
                'Curve Description': 'curve-fit class',
                'curve specification': 'curve-fit specification',
                'Cys_1hr-Read-Curve_Description': 'curve-fit class',
                'Data Type': '<do not use>',
                'description': 'comment',
                'effective concentration': 'screening concentration',
                'Efficacy (% of Control)': 'percent of control',
                'efflux ratio': 'percent efflux',
                'Fluorescence ratio': 'fluorescence ratio',
                'fluoresence ratio': 'fluorescence ratio',
                'Goodness of Fit': 'goodness of fit',
                'Inflection Point Concentration': 'inflection point concentration',
                'intensity ratio': 'percent intensity',
                'log virus titre change': 'log virus titer change',
                'permeability': 'permeability A-B',
                'phenotype': 'activity type',
                'run date': '<do not use>',
                'run identifier': '<do not use>',
                'SI': 'selectivity index',
                'Submission Date': '<do not use>',
                'Submitter': '<do not use>',
                'Untreated': 'untreated',
                'Verification': 'is verified',
                'verification': 'is verified',
                'CRE-460 Curve Description': 'curve-fit class',
                'CRE-530 Curve Description': 'curve-fit class',
                'CRE-ratio Curve Description': 'curve-fit class',
                'Data Verification': 'is verified',
                'ACTIVITY_RGS16': 'Activity type',
                'ACTIVITY_RGS19': 'Activity type',
                'ACTIVITY_RGS4': 'Activity type',
                'ACTIVITY_RGS7': 'Activity type',
                'ACTIVITY_RGS8': 'Activity type',
                'ng': 'nanogram',
                'ng/uL': 'microgram per milliliter']

        return attributeNameMapping
    }
}
