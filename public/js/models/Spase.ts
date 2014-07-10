/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes an angular resource
    export interface ISpase extends ng.resource.IResource<ISpase> {
        version: string
        resources: Array<ISpaseElem>
        lang: string
    }
    
    // this is the only way we can deliver an Array 
    // of different elements atm
    export interface ISpaseElem {
        repository?: Repository
        simulationModel?: SimulationModel
        simulationRun?: SimulationRun
        numericalOutput?: NumericalOutput
        granule?: Granule
    }

    // base element
    export class SpaseElem {
        constructor(
            public resourceId: string){}
    }
    
    
    // repository element
    export class Repository extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public accessUrl: AccessUrl){ super(resourceId) } 
    }   
    
    export class ResourceHeader {
        constructor(
            public resourceName: string,
            public releaseDate: string,
            public description: string,
            public contact: Array<Contact>,
            public informationUrl: Array<string>){}
    }
    
    export class Contact {
        constructor(
            public personId: string,
            public role: string){}
    }
    
    export class AccessUrl {
        constructor(
            public url: string,
            public productKey: Array<string>){}
    }
    
    
    // simulation model element
    export class SimulationModel extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public versions: string,
            public simulationType: string){ super(resourceId) } 
    }  
    
    export class ModelVersion {
        constructor(
            public versionId: string,
            public releaseDate: string,
            public description: string){}
    }
    
    export class Versions {
        constructor(
            public modelVersion: ModelVersion){}
    }
    
    
    // simulation run element
    export class SimulationRun extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public modelId: string,
            public temporalDependence: string,
            public simulatedRegion: Array<string>,
            public likelhooRating: string,
            public simulationTime: SimulationTime,
            public simulationDomain: SimulationDomain,
            // this is the only way we can deliver an Array 
            // of different elements atm
            public inputs: Array<IInput>){ super(resourceId) }
    }  
    
    export class SimulationTime {
        constructor(
            public duration: string,
            public timeStart: string,
            public timeStop: string,
            public timeStep: string){}
    }
    
    export class SimulationDomain {
        constructor(
            public description: string,
            public caveats: string,
            public spatialDimension: string, 
            public velocityDimension: string, 
            public fieldDimension: string,
            public units: string,
            public unitsConversion: string, 
            public coordinatesLabel: Array<string>, 
            public validMin: string, 
            public validMax: string, 
            public gridStructure: string,
            public gridCellSize: Array<number>, 
            public symmetry: string,
            public boundaryConditions?: BoundaryConditions){}
    }
    
    export class BoundaryConditions {
        constructor(
            public fieldBoundary: ElementBoundary,
            public particleBoundary: ElementBoundary){}
    }
    
    export class ElementBoundary {
        constructor(
            public frontWall: string, 
            public backWall: string, 
            public sideWall: string,
            public obstacle: string,
            public caveats: string){}
    }
    
    export interface IInput {
        inputField?: InputField
        inputParameter?: InputParameter
        inputPopulation?: InputPopulation
        inputProcess?: InputProcess
    }
    
    export class InputField {
        constructor(
            public name: string,
            public set: Array<string>,
            public parameterKey: string,
            public description: string,
            public caveats: string,
            public simulatedRegion: Array<string>,
            public coordinateSystem: CoordinateSystem,
            public qualifier: Array<string>,
            public fieldQuantity: string,
            public units: string,
            public unitsConversion: string,
            public inputLabel: Array<string>,
            public fieldValue: Array<string>,
            public inputTableUrl: string,
            public validMin: string,
            public validMax: string,
            public fieldModel: string,
            public modelUrl: string){}
    }
    
    export class InputParameter {
        constructor(
            public name: string,
            public description: string,
            public caveats: string,
            public simulatedRegion: Array<string>,
            public qualifier: Array<string>,
            public parameterQuantity: string,
            public property: Array<Property>){}
    }

    export class InputPopulation {
        constructor(
            public name: string,
            public set: Array<string>,
            public parameterKey: string,
            public description: string,
            public caveats: string,
            public simulatedRegion: Array<string>,
            public processType: string,
            public units: string,
            public unitsConversion: string,
            public processCoefficient: string,
            public processCoeffType: string,
            public processModel: string,
            public modelUrl: string){}
    }
    
    export class InputProcess {
        constructor(
            public name: string,
            public set: Array<string>,
            public parameterKey: string,
            public description: string,
            public caveats: string,
            public simulatedRegion: Array<string>,
            public qualifier: string,
            public particleType: string,
            public chemicalFormula: string,
            public atomicNumber: string,
            public populationMassNumber: string,
            public populationChargeState: string,
            public populationDensity: string,
            public populationTemperature: string,
            public populationFlowSpeed: string,
            public distribution: string,
            public productionRate: string,
            public totalProductionRate: string,
            public inputTableURL: string,
            public profile: string,
            public modelUrl: string){}
    }

    
    // numerical output element
    export class NumericalOutput extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public accessInformation: Array<AccessInformation>,
            public measurementType: Array<string>,
            public simulatedRegion: Array<string>,
            public inputResourceId: Array<string>,
            public parameter: Array<ParameterType>,
            public spatialDescription?: SpatialDescription,
            public temporalDescription?: TemporalDescription){ super(resourceId) }
    }
    
    export class AccessInformation {
        constructor(
            public repositoryId: string,
            public availability: string,
            public accessUrl: Array<AccessUrl>,
            public format: string,
            public encoding: string,
            public dataExtent: DataExtent){}
    }

    export class DataExtent {
        constructor(
            public quantity: number,
            public units?: string, 
            public per?: string){}
    }
    
    export class ParameterType {
        constructor(
            public name: string,
            public set: Array<string>,
            public parameterKey: string,
            public description: string,
            public caveats: string,
            public cadence: string,
            public units: string,
            public unitsConversion: string,
            public coordinateSystem: CoordinateSystem,
            public renderingHints: Array<RenderingHints>,
            public structure: Structure,
            public validMin: string,
            public validMax: string,
            public fillValue: string,
            public property: Array<Property>,
            public field?: Field,
            public wave?: Wave,
            public mixed?: Mixed,
            public support?: Support,
            public particle?: Particle){}
    }
    
    export class CoordinateSystem {
        constructor(
            public coordinateRepresentation: string,
            public coordinateSystemName: string){}
    }
    
    export class RenderingHints {
        constructor(
            public axisLabel: string,
            public displayType: string,
            public index: Array<number>,
            public renderingAxis: string,
            public scaleMax: number,
            public scaleMin: number,
            public scaleType: string,
            public valueFormat: string){}
    }
     
    export class Structure {
        constructor(
            public description: string,
            public element: Element,
            public size: Array<number>){}
    }
    
    export class Element {
        constructor(
            public fillValue: string,
            public index: Array<number>,
            public name: string,
            public parameterKey: string,
            public qualifier: string,
            public renderingHints: RenderingHints,
            public units: string,
            public unitsConversion: string,
            public validMax: string,
            public validMin: string){}
    }
    
    export class Property {
        constructor(
            public name: string,
            public description: string,
            public caveats: string,
            public propertyQuantity: string,
            public qualifier: Array<string>,
            public units: string,
            public unitsConversion: string,
            public propertyLabel: Array<string>,
            public propertyValue: Array<string>,
            public propertyTableUrl: string,
            public validMax: string,
            public validMin: string,
            public propertyModel: string,
            public modelUrl: string){}
    }
    
    export class Field {
        constructor(
            public fieldQuantity: string,
            public frequencyRange: FrequencyRange,
            public qualifier: Array<string>){}
    }
    
    export class FrequencyRange {
        constructor(
            public bin: Bin,
            public high: number,
            public low: number,
            public spectralRange: string,
            public units: string){}
    }
    
    export class Bin {
        constructor(
            public bandName: string,
            public high: number,
            public low: number){}
    } 
    
    export class Wave {
        constructor(
            public energyRange: EnergyRange,
            public frequencyRange: FrequencyRange,
            public qualifier: Array<string>,
            public wavelengthRange: WavelengthRange,
            public waveQuantity: string,
            public waveType: string){}
    }
    
    export class EnergyRange {
        constructor(
            public bin: Bin,
            public high: number,
            public low: number,
            public units: string){}
    }
    
    export class WavelengthRange {
        constructor(
            public bin: Bin,
            public high: number,
            public low: number,
            public spectralRange: string,
            public units: string){}
    }
    
    export class Mixed {
        constructor(
            public mixedQuantity: string,
            public particleType: string,
            public qualifier: Array<string>){}
    }
    
    export class Support {
        constructor(
            public supportQuantity: string,
            public qualifier: Array<string>){}
    }
    
    export class Particle {
        constructor(
            public populationId: string,
            public particleType: Array<string>,
            public particleQuantity: string,
            public chemicalFormula: string,
            public atomicNumber: Array<number>,
            public populationMassNumber: Array<number>,
            public populationChargeState: Array<number>){}
    }    
    
    export class SpatialDescription {
        constructor(
            public dimension: string,
            public coordinateSystem: CoordinateSystem,
            public coordinatesLabel: Array<string>,
            public units: string,
            public cubesDescription?: CubesDescription,
            public cutsDescription?: CutsDescription){}
    }
    
    export class CubesDescription {
        constructor(
            public regionBegin: Array<number>,
            public regionEnd: Array<number>){}
    }
    
    export class CutsDescription {
        constructor(
            public planetNormalVector: Array<number>,
            public planePoint: Array<number>){}
    }
        
    export class TemporalDescription {
        constructor(
            public cadence: string,
            public exposure: string,
            public timespan: TimeSpan){}
    }
    
    export class TimeSpan {
        constructor(
           public startDate: string,
           public stopDate: string,
           public note: Array<string>){}
    }

 
    // granule element
    export class Granule extends SpaseElem {
        constructor(
            public resourceId: string,
            public releaseDate: string,
            public parentId: string,
            public source: string,
            public regionBegin?: string,
            public regionEnd?: string, 
            public startDate?: string,
            public stopDate?: string){ super(resourceId) }
    }    
      
}
