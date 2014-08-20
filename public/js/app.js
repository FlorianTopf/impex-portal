/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // used for the sessionStorage
    // currently saved method state
    var MethodState = (function () {
        function MethodState(path, params) {
            this.path = path;
            this.params = params;
        }
        return MethodState;
    })();
    portal.MethodState = MethodState;

    var App = (function () {
        function App() {
            this.name = 'app';
            this.abstract = true;
            this.controller = portal.ConfigCtrl;
            this.template = '<ui-view/>';
            this.resolve = {
                config: [
                    'configService',
                    function (ConfigService) {
                        return ConfigService.loadConfig();
                    }
                ],
                userData: [
                    'userService',
                    function (UserService) {
                        return UserService.loadUserData();
                    }
                ]
            };
        }
        return App;
    })();
    portal.App = App;

    var Portal = (function () {
        function Portal() {
            this.name = 'app.portal';
            this.url = '/portal';
            this.templateUrl = '/public/partials/portalMap.html';
            this.controller = portal.PortalCtrl;
        }
        return Portal;
    })();
    portal.Portal = Portal;

    var Registry = (function () {
        function Registry() {
            this.name = 'app.portal.registry';
            this.url = '/registry?id';
        }
        Registry.prototype.onEnter = function ($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/registryModal.html',
                controller: portal.RegistryCtrl,
                size: 'lg',
                resolve: {
                    id: function () {
                        return $stateParams.id;
                    }
                }
            }).result.then(function () {
                $state.transitionTo('app.portal');
            }, function () {
                $state.transitionTo('app.portal');
            });
        };
        return Registry;
    })();
    portal.Registry = Registry;

    var Methods = (function () {
        function Methods() {
            this.name = 'app.portal.methods';
            this.url = '/methods?id';
        }
        Methods.prototype.onEnter = function ($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/methodsModal.html',
                controller: portal.MethodsCtrl,
                size: 'lg',
                resolve: {
                    id: function () {
                        return $stateParams.id;
                    }
                }
            }).result.then(function () {
                $state.transitionTo('app.portal');
            }, function () {
                $state.transitionTo('app.portal');
            });
        };
        return Methods;
    })();
    portal.Methods = Methods;

    var MyData = (function () {
        function MyData() {
            this.name = 'app.portal.userdata';
            this.url = '/userdata';
        }
        MyData.prototype.onEnter = function ($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/userDataModal.html',
                controller: portal.UserDataCtrl,
                size: 'lg'
            }).result.then(function () {
                $state.transitionTo('app.portal');
            }, function () {
                $state.transitionTo('app.portal');
            });
        };
        return MyData;
    })();
    portal.MyData = MyData;

    var Databases = (function () {
        function Databases() {
            this.name = 'app.databases';
            this.url = '/databases';
            this.templateUrl = '/public/partials/databaseMap.html';
            this.controller = portal.PortalCtrl;
        }
        return Databases;
    })();
    portal.Databases = Databases;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var Database = (function () {
        function Database(id, type, name, description, dns, methods, tree, protocol, info) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.description = description;
            this.dns = dns;
            this.methods = methods;
            this.tree = tree;
            this.protocol = protocol;
            this.info = info;
        }
        return Database;
    })();
    portal.Database = Database;

    var Tool = (function () {
        function Tool(name, description, url, info) {
            this.name = name;
            this.description = description;
            this.url = url;
            this.info = info;
        }
        return Tool;
    })();
    portal.Tool = Tool;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var portal;
(function (portal) {
    'use strict';

    // base element
    var SpaseElem = (function () {
        function SpaseElem(resourceId) {
            this.resourceId = resourceId;
        }
        return SpaseElem;
    })();
    portal.SpaseElem = SpaseElem;

    // repository element
    var Repository = (function (_super) {
        __extends(Repository, _super);
        function Repository(resourceId, resourceHeader, accessUrl) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
            this.resourceHeader = resourceHeader;
            this.accessUrl = accessUrl;
        }
        return Repository;
    })(SpaseElem);
    portal.Repository = Repository;

    var ResourceHeader = (function () {
        function ResourceHeader(resourceName, releaseDate, description, contact, informationUrl) {
            this.resourceName = resourceName;
            this.releaseDate = releaseDate;
            this.description = description;
            this.contact = contact;
            this.informationUrl = informationUrl;
        }
        return ResourceHeader;
    })();
    portal.ResourceHeader = ResourceHeader;

    var Contact = (function () {
        function Contact(personId, role) {
            this.personId = personId;
            this.role = role;
        }
        return Contact;
    })();
    portal.Contact = Contact;

    var AccessUrl = (function () {
        function AccessUrl(url, productKey) {
            this.url = url;
            this.productKey = productKey;
        }
        return AccessUrl;
    })();
    portal.AccessUrl = AccessUrl;

    // simulation model element
    var SimulationModel = (function (_super) {
        __extends(SimulationModel, _super);
        function SimulationModel(resourceId, resourceHeader, versions, simulationType) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
            this.resourceHeader = resourceHeader;
            this.versions = versions;
            this.simulationType = simulationType;
        }
        return SimulationModel;
    })(SpaseElem);
    portal.SimulationModel = SimulationModel;

    var ModelVersion = (function () {
        function ModelVersion(versionId, releaseDate, description) {
            this.versionId = versionId;
            this.releaseDate = releaseDate;
            this.description = description;
        }
        return ModelVersion;
    })();
    portal.ModelVersion = ModelVersion;

    var Versions = (function () {
        function Versions(modelVersion) {
            this.modelVersion = modelVersion;
        }
        return Versions;
    })();
    portal.Versions = Versions;

    // simulation run element
    var SimulationRun = (function (_super) {
        __extends(SimulationRun, _super);
        function SimulationRun(resourceId, resourceHeader, modelId, temporalDependence, simulatedRegion, likelhooRating, simulationTime, simulationDomain, // this is the only way we can deliver an Array
        // of different elements atm
        inputs) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
            this.resourceHeader = resourceHeader;
            this.modelId = modelId;
            this.temporalDependence = temporalDependence;
            this.simulatedRegion = simulatedRegion;
            this.likelhooRating = likelhooRating;
            this.simulationTime = simulationTime;
            this.simulationDomain = simulationDomain;
            this.inputs = inputs;
        }
        return SimulationRun;
    })(SpaseElem);
    portal.SimulationRun = SimulationRun;

    var SimulationTime = (function () {
        function SimulationTime(duration, timeStart, timeStop, timeStep) {
            this.duration = duration;
            this.timeStart = timeStart;
            this.timeStop = timeStop;
            this.timeStep = timeStep;
        }
        return SimulationTime;
    })();
    portal.SimulationTime = SimulationTime;

    var SimulationDomain = (function () {
        function SimulationDomain(description, caveats, spatialDimension, velocityDimension, fieldDimension, units, unitsConversion, coordinatesLabel, validMin, validMax, gridStructure, gridCellSize, symmetry, boundaryConditions) {
            this.description = description;
            this.caveats = caveats;
            this.spatialDimension = spatialDimension;
            this.velocityDimension = velocityDimension;
            this.fieldDimension = fieldDimension;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.coordinatesLabel = coordinatesLabel;
            this.validMin = validMin;
            this.validMax = validMax;
            this.gridStructure = gridStructure;
            this.gridCellSize = gridCellSize;
            this.symmetry = symmetry;
            this.boundaryConditions = boundaryConditions;
        }
        return SimulationDomain;
    })();
    portal.SimulationDomain = SimulationDomain;

    var BoundaryConditions = (function () {
        function BoundaryConditions(fieldBoundary, particleBoundary) {
            this.fieldBoundary = fieldBoundary;
            this.particleBoundary = particleBoundary;
        }
        return BoundaryConditions;
    })();
    portal.BoundaryConditions = BoundaryConditions;

    var ElementBoundary = (function () {
        function ElementBoundary(frontWall, backWall, sideWall, obstacle, caveats) {
            this.frontWall = frontWall;
            this.backWall = backWall;
            this.sideWall = sideWall;
            this.obstacle = obstacle;
            this.caveats = caveats;
        }
        return ElementBoundary;
    })();
    portal.ElementBoundary = ElementBoundary;

    var InputField = (function () {
        function InputField(name, set, parameterKey, description, caveats, simulatedRegion, coordinateSystem, qualifier, fieldQuantity, units, unitsConversion, inputLabel, fieldValue, inputTableUrl, validMin, validMax, fieldModel, modelUrl) {
            this.name = name;
            this.set = set;
            this.parameterKey = parameterKey;
            this.description = description;
            this.caveats = caveats;
            this.simulatedRegion = simulatedRegion;
            this.coordinateSystem = coordinateSystem;
            this.qualifier = qualifier;
            this.fieldQuantity = fieldQuantity;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.inputLabel = inputLabel;
            this.fieldValue = fieldValue;
            this.inputTableUrl = inputTableUrl;
            this.validMin = validMin;
            this.validMax = validMax;
            this.fieldModel = fieldModel;
            this.modelUrl = modelUrl;
        }
        return InputField;
    })();
    portal.InputField = InputField;

    var InputParameter = (function () {
        function InputParameter(name, description, caveats, simulatedRegion, qualifier, parameterQuantity, property) {
            this.name = name;
            this.description = description;
            this.caveats = caveats;
            this.simulatedRegion = simulatedRegion;
            this.qualifier = qualifier;
            this.parameterQuantity = parameterQuantity;
            this.property = property;
        }
        return InputParameter;
    })();
    portal.InputParameter = InputParameter;

    var InputProcess = (function () {
        function InputProcess(name, set, parameterKey, description, caveats, simulatedRegion, processType, units, unitsConversion, processCoefficient, processCoeffType, processModel, modelUrl) {
            this.name = name;
            this.set = set;
            this.parameterKey = parameterKey;
            this.description = description;
            this.caveats = caveats;
            this.simulatedRegion = simulatedRegion;
            this.processType = processType;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.processCoefficient = processCoefficient;
            this.processCoeffType = processCoeffType;
            this.processModel = processModel;
            this.modelUrl = modelUrl;
        }
        return InputProcess;
    })();
    portal.InputProcess = InputProcess;

    var InputPopulation = (function () {
        function InputPopulation(name, set, parameterKey, description, caveats, simulatedRegion, qualifier, particleType, chemicalFormula, atomicNumber, populationMassNumber, populationChargeState, populationDensity, populationTemperature, populationFlowSpeed, distribution, productionRate, totalProductionRate, inputTableURL, profile, modelUrl) {
            this.name = name;
            this.set = set;
            this.parameterKey = parameterKey;
            this.description = description;
            this.caveats = caveats;
            this.simulatedRegion = simulatedRegion;
            this.qualifier = qualifier;
            this.particleType = particleType;
            this.chemicalFormula = chemicalFormula;
            this.atomicNumber = atomicNumber;
            this.populationMassNumber = populationMassNumber;
            this.populationChargeState = populationChargeState;
            this.populationDensity = populationDensity;
            this.populationTemperature = populationTemperature;
            this.populationFlowSpeed = populationFlowSpeed;
            this.distribution = distribution;
            this.productionRate = productionRate;
            this.totalProductionRate = totalProductionRate;
            this.inputTableURL = inputTableURL;
            this.profile = profile;
            this.modelUrl = modelUrl;
        }
        return InputPopulation;
    })();
    portal.InputPopulation = InputPopulation;

    var RegionParameter = (function () {
        function RegionParameter(description, caveats, inputTableUrl, objectMass, period, property, radius, simulatedRegion, subLongitude) {
            this.description = description;
            this.caveats = caveats;
            this.inputTableUrl = inputTableUrl;
            this.objectMass = objectMass;
            this.period = period;
            this.property = property;
            this.radius = radius;
            this.simulatedRegion = simulatedRegion;
            this.subLongitude = subLongitude;
        }
        return RegionParameter;
    })();
    portal.RegionParameter = RegionParameter;

    // numerical output element
    var NumericalOutput = (function (_super) {
        __extends(NumericalOutput, _super);
        function NumericalOutput(resourceId, resourceHeader, accessInformation, measurementType, simulatedRegion, inputResourceId, parameter, spatialDescription, temporalDescription) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
            this.resourceHeader = resourceHeader;
            this.accessInformation = accessInformation;
            this.measurementType = measurementType;
            this.simulatedRegion = simulatedRegion;
            this.inputResourceId = inputResourceId;
            this.parameter = parameter;
            this.spatialDescription = spatialDescription;
            this.temporalDescription = temporalDescription;
        }
        return NumericalOutput;
    })(SpaseElem);
    portal.NumericalOutput = NumericalOutput;

    var AccessInformation = (function () {
        function AccessInformation(repositoryId, availability, accessUrl, format, encoding, dataExtent) {
            this.repositoryId = repositoryId;
            this.availability = availability;
            this.accessUrl = accessUrl;
            this.format = format;
            this.encoding = encoding;
            this.dataExtent = dataExtent;
        }
        return AccessInformation;
    })();
    portal.AccessInformation = AccessInformation;

    var DataExtent = (function () {
        function DataExtent(quantity, units, per) {
            this.quantity = quantity;
            this.units = units;
            this.per = per;
        }
        return DataExtent;
    })();
    portal.DataExtent = DataExtent;

    var ParameterType = (function () {
        function ParameterType(name, set, parameterKey, description, caveats, cadence, units, unitsConversion, coordinateSystem, renderingHints, structure, validMin, validMax, fillValue, property, field, wave, mixed, support, particle) {
            this.name = name;
            this.set = set;
            this.parameterKey = parameterKey;
            this.description = description;
            this.caveats = caveats;
            this.cadence = cadence;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.coordinateSystem = coordinateSystem;
            this.renderingHints = renderingHints;
            this.structure = structure;
            this.validMin = validMin;
            this.validMax = validMax;
            this.fillValue = fillValue;
            this.property = property;
            this.field = field;
            this.wave = wave;
            this.mixed = mixed;
            this.support = support;
            this.particle = particle;
        }
        return ParameterType;
    })();
    portal.ParameterType = ParameterType;

    var CoordinateSystem = (function () {
        function CoordinateSystem(coordinateRepresentation, coordinateSystemName) {
            this.coordinateRepresentation = coordinateRepresentation;
            this.coordinateSystemName = coordinateSystemName;
        }
        return CoordinateSystem;
    })();
    portal.CoordinateSystem = CoordinateSystem;

    var RenderingHints = (function () {
        function RenderingHints(axisLabel, displayType, index, renderingAxis, scaleMax, scaleMin, scaleType, valueFormat) {
            this.axisLabel = axisLabel;
            this.displayType = displayType;
            this.index = index;
            this.renderingAxis = renderingAxis;
            this.scaleMax = scaleMax;
            this.scaleMin = scaleMin;
            this.scaleType = scaleType;
            this.valueFormat = valueFormat;
        }
        return RenderingHints;
    })();
    portal.RenderingHints = RenderingHints;

    var Structure = (function () {
        function Structure(description, element, size) {
            this.description = description;
            this.element = element;
            this.size = size;
        }
        return Structure;
    })();
    portal.Structure = Structure;

    var Element = (function () {
        function Element(fillValue, index, name, parameterKey, qualifier, renderingHints, units, unitsConversion, validMax, validMin) {
            this.fillValue = fillValue;
            this.index = index;
            this.name = name;
            this.parameterKey = parameterKey;
            this.qualifier = qualifier;
            this.renderingHints = renderingHints;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.validMax = validMax;
            this.validMin = validMin;
        }
        return Element;
    })();
    portal.Element = Element;

    var Property = (function () {
        function Property(name, description, caveats, propertyQuantity, qualifier, units, unitsConversion, propertyLabel, propertyValue, propertyTableUrl, validMax, validMin, propertyModel, modelUrl) {
            this.name = name;
            this.description = description;
            this.caveats = caveats;
            this.propertyQuantity = propertyQuantity;
            this.qualifier = qualifier;
            this.units = units;
            this.unitsConversion = unitsConversion;
            this.propertyLabel = propertyLabel;
            this.propertyValue = propertyValue;
            this.propertyTableUrl = propertyTableUrl;
            this.validMax = validMax;
            this.validMin = validMin;
            this.propertyModel = propertyModel;
            this.modelUrl = modelUrl;
        }
        return Property;
    })();
    portal.Property = Property;

    var Field = (function () {
        function Field(fieldQuantity, frequencyRange, qualifier) {
            this.fieldQuantity = fieldQuantity;
            this.frequencyRange = frequencyRange;
            this.qualifier = qualifier;
        }
        return Field;
    })();
    portal.Field = Field;

    var FrequencyRange = (function () {
        function FrequencyRange(bin, high, low, spectralRange, units) {
            this.bin = bin;
            this.high = high;
            this.low = low;
            this.spectralRange = spectralRange;
            this.units = units;
        }
        return FrequencyRange;
    })();
    portal.FrequencyRange = FrequencyRange;

    var Bin = (function () {
        function Bin(bandName, high, low) {
            this.bandName = bandName;
            this.high = high;
            this.low = low;
        }
        return Bin;
    })();
    portal.Bin = Bin;

    var Wave = (function () {
        function Wave(energyRange, frequencyRange, qualifier, wavelengthRange, waveQuantity, waveType) {
            this.energyRange = energyRange;
            this.frequencyRange = frequencyRange;
            this.qualifier = qualifier;
            this.wavelengthRange = wavelengthRange;
            this.waveQuantity = waveQuantity;
            this.waveType = waveType;
        }
        return Wave;
    })();
    portal.Wave = Wave;

    var EnergyRange = (function () {
        function EnergyRange(bin, high, low, units) {
            this.bin = bin;
            this.high = high;
            this.low = low;
            this.units = units;
        }
        return EnergyRange;
    })();
    portal.EnergyRange = EnergyRange;

    var WavelengthRange = (function () {
        function WavelengthRange(bin, high, low, spectralRange, units) {
            this.bin = bin;
            this.high = high;
            this.low = low;
            this.spectralRange = spectralRange;
            this.units = units;
        }
        return WavelengthRange;
    })();
    portal.WavelengthRange = WavelengthRange;

    var Mixed = (function () {
        function Mixed(mixedQuantity, particleType, qualifier) {
            this.mixedQuantity = mixedQuantity;
            this.particleType = particleType;
            this.qualifier = qualifier;
        }
        return Mixed;
    })();
    portal.Mixed = Mixed;

    var Support = (function () {
        function Support(supportQuantity, qualifier) {
            this.supportQuantity = supportQuantity;
            this.qualifier = qualifier;
        }
        return Support;
    })();
    portal.Support = Support;

    var Particle = (function () {
        function Particle(populationId, particleType, particleQuantity, chemicalFormula, atomicNumber, populationMassNumber, populationChargeState) {
            this.populationId = populationId;
            this.particleType = particleType;
            this.particleQuantity = particleQuantity;
            this.chemicalFormula = chemicalFormula;
            this.atomicNumber = atomicNumber;
            this.populationMassNumber = populationMassNumber;
            this.populationChargeState = populationChargeState;
        }
        return Particle;
    })();
    portal.Particle = Particle;

    var SpatialDescription = (function () {
        function SpatialDescription(dimension, coordinateSystem, coordinatesLabel, units, cubesDescription, cutsDescription) {
            this.dimension = dimension;
            this.coordinateSystem = coordinateSystem;
            this.coordinatesLabel = coordinatesLabel;
            this.units = units;
            this.cubesDescription = cubesDescription;
            this.cutsDescription = cutsDescription;
        }
        return SpatialDescription;
    })();
    portal.SpatialDescription = SpatialDescription;

    var CubesDescription = (function () {
        function CubesDescription(regionBegin, regionEnd) {
            this.regionBegin = regionBegin;
            this.regionEnd = regionEnd;
        }
        return CubesDescription;
    })();
    portal.CubesDescription = CubesDescription;

    var CutsDescription = (function () {
        function CutsDescription(planetNormalVector, planePoint) {
            this.planetNormalVector = planetNormalVector;
            this.planePoint = planePoint;
        }
        return CutsDescription;
    })();
    portal.CutsDescription = CutsDescription;

    var TemporalDescription = (function () {
        function TemporalDescription(cadence, exposure, timespan) {
            this.cadence = cadence;
            this.exposure = exposure;
            this.timespan = timespan;
        }
        return TemporalDescription;
    })();
    portal.TemporalDescription = TemporalDescription;

    var TimeSpan = (function () {
        function TimeSpan(startDate, stopDate, note) {
            this.startDate = startDate;
            this.stopDate = stopDate;
            this.note = note;
        }
        return TimeSpan;
    })();
    portal.TimeSpan = TimeSpan;

    // granule element
    var Granule = (function (_super) {
        __extends(Granule, _super);
        function Granule(resourceId, releaseDate, parentId, source, regionBegin, regionEnd, startDate, stopDate) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
            this.releaseDate = releaseDate;
            this.parentId = parentId;
            this.source = source;
            this.regionBegin = regionBegin;
            this.regionEnd = regionEnd;
            this.startDate = startDate;
            this.stopDate = stopDate;
        }
        return Granule;
    })(SpaseElem);
    portal.Granule = Granule;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var Api = (function () {
        function Api(path, operations) {
            this.path = path;
            this.operations = operations;
        }
        return Api;
    })();
    portal.Api = Api;

    var Operation = (function () {
        function Operation(method, summary, notes, type, nickname, // not needed at IMPEx
        authorisations, parameters, responseMessages) {
            this.method = method;
            this.summary = summary;
            this.notes = notes;
            this.type = type;
            this.nickname = nickname;
            this.authorisations = authorisations;
            this.parameters = parameters;
            this.responseMessages = responseMessages;
        }
        return Operation;
    })();
    portal.Operation = Operation;

    var Parameter = (function () {
        function Parameter(name, description, defaultValue, required, type, paramType, allowMultiple) {
            this.name = name;
            this.description = description;
            this.defaultValue = defaultValue;
            this.required = required;
            this.type = type;
            this.paramType = paramType;
            this.allowMultiple = allowMultiple;
        }
        return Parameter;
    })();
    portal.Parameter = Parameter;

    var ResponseMessage = (function () {
        function ResponseMessage(code, message) {
            this.code = code;
            this.message = message;
        }
        return ResponseMessage;
    })();
    portal.ResponseMessage = ResponseMessage;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // currently saved results
    var Result = (function () {
        function Result(repositoryId, id, method, content) {
            this.repositoryId = repositoryId;
            this.id = id;
            this.method = method;
            this.content = content;
        }
        return Result;
    })();
    portal.Result = Result;

    // currently saved selections
    var Selection = (function () {
        function Selection(repositoryId, id, type, elem) {
            this.repositoryId = repositoryId;
            this.id = id;
            this.type = type;
            this.elem = elem;
        }
        return Selection;
    })();
    portal.Selection = Selection;

    var User = (function () {
        function User(id) {
            this.id = id;
            this.results = [];
            this.selections = [];
            this.voTables = [];
            this.activeSelection = [];
        }
        return User;
    })();
    portal.User = User;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var ConfigService = (function () {
        function ConfigService($resource, $http) {
            this.url = '/';
            this.config = null;
            this.aliveMap = {};
            this.filterRegions = [];
            // creates an action descriptor
            this.configAction = {
                method: 'GET',
                isArray: false
            };
            this.resource = $resource;
            this.http = $http;
        }
        // checks if a database and its methods are alive
        ConfigService.prototype.isAlive = function (dbName) {
            var _this = this;
            if ((dbName.indexOf('FMI-HYBRID') != -1) || (dbName.indexOf('FMI-GUMICS') != -1))
                var callName = 'FMI';
else
                var callName = dbName;

            this.http.get(this.url + 'methods/' + callName + "/isAlive", { timeout: 10000 }).success(function (data, status) {
                _this.aliveMap[dbName] = data;
                //console.log("Hello "+dbName+" "+this.aliveMap[dbName])
            }).error(function (data, status) {
                _this.aliveMap[dbName] = false;
            });
        };

        // returns the resource handler
        ConfigService.prototype.Config = function () {
            return this.resource(this.url + 'config?', { fmt: '@fmt' }, { getConfig: this.configAction });
        };

        // returns promise for resource handler
        ConfigService.prototype.loadConfig = function () {
            return this.Config().get({ fmt: 'json' }).$promise;
        };

        ConfigService.prototype.getDatabase = function (id) {
            return this.config.databases.filter(function (e) {
                return e.id == id;
            })[0];
        };

        // Filter routines
        ConfigService.prototype.getRegions = function () {
            var _this = this;
            this.http.get(this.url + 'filter/region', { timeout: 10000 }).success(function (data, status) {
                return _this.filterRegions = data;
            }).error(function (data, status) {
                return _this.filterRegions = [];
            });
        };

        ConfigService.prototype.filterRegion = function (name) {
            return this.http.get(this.url + 'filter/region/' + name, { timeout: 10000 });
        };
        ConfigService.$inject = ['$resource', '$http'];
        return ConfigService;
    })();
    portal.ConfigService = ConfigService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var RegistryService = (function () {
        function RegistryService($resource) {
            this.url = '/';
            // action descriptor for registry actions
            this.registryAction = {
                method: 'GET',
                isArray: false
            };
            // cache for the elements (identified by request id)
            this.cachedElements = {};
            // defines which elements are selectables in the registry
            this.selectables = {};
            this.resource = $resource;

            this.selectables['spase://IMPEX/Repository/FMI/HYB'] = ['NumericalOutput'];
            this.selectables['spase://IMPEX/Repository/FMI/GUMICS'] = ['NumericalOutput'];
            this.selectables['spase://IMPEX/Repository/LATMOS'] = ['SimulationRun', 'NumericalOutput'];
            this.selectables['spase://IMPEX/Repository/SINP'] = ['SimulationModel', 'NumericalOutput'];
        }
        RegistryService.prototype.Repository = function () {
            return this.resource(this.url + 'registry/repository?', { id: '@id', fmt: '@fmt' }, { getRepository: this.registryAction });
        };

        RegistryService.prototype.SimulationModel = function () {
            return this.resource(this.url + 'registry/simulationmodel?', { id: '@id', fmt: '@fmt' }, { getSimulationModel: this.registryAction });
        };

        RegistryService.prototype.SimulationRun = function () {
            return this.resource(this.url + 'registry/simulationrun?', { id: '@id', fmt: '@fmt' }, { getSimulationRun: this.registryAction });
        };

        RegistryService.prototype.NumericalOutput = function () {
            return this.resource(this.url + 'registry/numericaloutput?', { id: '@id', fmt: '@fmt' }, { getNumericalOutput: this.registryAction });
        };

        RegistryService.prototype.Granule = function () {
            return this.resource(this.url + 'registry/granule?', { id: '@id', fmt: '@fmt' }, { getGranule: this.registryAction });
        };
        RegistryService.$inject = ['$resource'];
        return RegistryService;
    })();
    portal.RegistryService = RegistryService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var MethodsService = (function () {
        function MethodsService($rootScope, $resource, growl) {
            this.url = '/';
            this.methods = null;
            this.loading = {};
            this.status = '';
            this.showError = {};
            this.showSuccess = {};
            // action descriptor for GET methods actions
            this.methodsAction = {
                method: 'GET',
                isArray: false
            };
            // action descriptor for POST methods actions
            this.postMethodAction = {
                method: 'POST'
            };
            this.resource = $resource;
            this.growl = growl;
            this.scope = $rootScope;
        }
        // generic method for requesting API
        MethodsService.prototype.MethodsAPI = function () {
            return this.resource(this.url + 'api-docs/methods', {}, { getMethods: this.methodsAction });
        };

        MethodsService.prototype.notify = function (status, id) {
            if (status == 'loading') {
                this.loading[id] = true;
                this.showError[id] = false;
                this.showSuccess[id] = false;
                this.scope.$broadcast('service-loading', id);
                this.growl.info(this.status);
            } else if (status == 'success') {
                this.loading[id] = false;
                this.showSuccess[id] = true;
                this.scope.$broadcast('service-success', id);
                this.growl.success(this.status);
            } else if (status == 'error') {
                this.loading[id] = false;
                this.showError[id] = true;
                this.scope.$broadcast('service-error', id);
                this.growl.error(this.status);
            }
        };

        MethodsService.prototype.getMethodsAPI = function () {
            return this.MethodsAPI().get().$promise;
        };

        MethodsService.prototype.getMethods = function (db) {
            if (db.name.indexOf("FMI") != -1) {
                return this.methods.apis.filter(function (e) {
                    if (e.path.indexOf("FMI") != -1)
                        return true;
else
                        return false;
                });
            } else {
                return this.methods.apis.filter(function (e) {
                    if (e.path.indexOf(db.name) != -1)
                        return true;
else
                        return false;
                });
            }
        };

        // generic method for requesting standard services (GET + params / POST)
        MethodsService.prototype.requestMethod = function (path, params) {
            return this.resource(path, params, { getMethod: this.methodsAction, postMethod: this.postMethodAction });
        };
        MethodsService.$inject = ['$rootScope', '$resource', 'growl'];
        return MethodsService;
    })();
    portal.MethodsService = MethodsService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var UserService = (function () {
        function UserService($localStorage, $sessionStorage, $resource) {
            this.url = '/';
            this.user = null;
            this.localStorage = null;
            this.sessionStorage = null;
            // creates an action descriptor for list
            this.userListAction = {
                method: 'GET',
                isArray: true
            };
            // creates an action descriptor for delete
            this.userDeleteAction = {
                method: 'DELETE'
            };
            this.localStorage = $localStorage;
            this.sessionStorage = $sessionStorage;

            // initialise needed keys (doesn't overwrite existing ones)
            this.localStorage.$default({
                results: null,
                selections: null
            });

            // saves current method state
            this.sessionStorage.$default({
                methods: {}
            });

            this.resource = $resource;
        }
        UserService.prototype.createId = function () {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1) + Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        };

        // returns the resource handler for userdata
        UserService.prototype.UserData = function () {
            return this.resource(this.url + 'userdata/:name', { name: '@name' }, {
                listUserData: this.userListAction,
                deleteUserData: this.userDeleteAction
            });
        };

        // returns promise for load resources
        UserService.prototype.loadUserData = function () {
            return this.UserData().query().$promise;
        };

        // calls delete on a specific userdata file
        UserService.prototype.deleteUserData = function (name) {
            return this.UserData().delete({}, { 'name': name });
        };
        UserService.$inject = ['$localStorage', '$sessionStorage', '$resource'];
        return UserService;
    })();
    portal.UserService = UserService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var ConfigCtrl = (function () {
        function ConfigCtrl($scope, $interval, configService, userService, $state, config, userData) {
            var _this = this;
            this.scope = $scope;
            this.scope.vm = this;
            this.interval = $interval;
            this.configService = configService;
            this.userService = userService;
            this.state = $state;
            this.configService.config = config;

            // only for simulations atm
            this.configService.config.databases.filter(function (e) {
                return e.type == 'simulation';
            }).forEach(function (e) {
                _this.configService.aliveMap[e.name] = false;
                _this.configService.isAlive(e.name);
            });

            // @TODO this routine must be changed (if we use filters in parallel)
            // set interval to check if methods are still alive => every 10 minutes (600k ms)
            this.interval(function () {
                return _this.configService.config.databases.filter(function (e) {
                    return e.type == 'simulation';
                }).forEach(function (e) {
                    _this.configService.isAlive(e.name);
                });
            }, 600000);

            // read all regions at startup (@TODO set an interval for refresh?)
            this.configService.getRegions();

            // @TODO user info comes from the server in the future (add in resolver too)
            this.userService.user = new portal.User(this.userService.createId());

            if (userData.length > 0)
                this.userService.user.voTables = userData;

            if (this.userService.localStorage.results != null)
                this.userService.user.results = this.userService.localStorage.results;

            if (this.userService.localStorage.selections != null)
                this.userService.user.selections = this.userService.localStorage.selections;
        }
        ConfigCtrl.$inject = [
            '$scope',
            '$interval',
            'configService',
            'userService',
            '$state',
            'config',
            'userData'
        ];
        return ConfigCtrl;
    })();
    portal.ConfigCtrl = ConfigCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var PortalCtrl = (function () {
        function PortalCtrl($scope, $timeout, configService, methodsService, $state, growl) {
            var _this = this;
            this.ready = false;
            this.scope = $scope;
            this.scope.vm = this;
            this.timeout = $timeout;
            this.configService = configService;
            this.methodsService = methodsService;
            this.state = $state;
            this.growl = growl;

            this.timeout(function () {
                _this.ready = true;
                _this.growl.warning('Configuration loaded, waiting for isAlive...');
            });

            this.scope.$on('service-loading', function (e, id) {
                console.log('loading at ' + id);
            });

            this.scope.$on('service-success', function (e, id) {
                console.log('success at ' + id);
            });

            this.scope.$on('service-error', function (e, id) {
                console.log('error at ' + id);
            });
        }
        PortalCtrl.$inject = ['$scope', '$timeout', 'configService', 'methodsService', '$state', 'growl'];
        return PortalCtrl;
    })();
    portal.PortalCtrl = PortalCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var RegistryCtrl = (function () {
        function RegistryCtrl($scope, $timeout, configService, registryService, $state, $modalInstance, id) {
            var _this = this;
            this.isFirstOpen = true;
            this.initialising = false;
            this.loading = false;
            this.scope = $scope;
            $scope.regvm = this;
            this.timeout = $timeout;
            this.configService = configService;
            this.registryService = registryService;
            this.state = $state;
            this.modalInstance = $modalInstance;
            this.database = this.configService.getDatabase(id);

            // watches changes of variable
            // (is changed each time modal is opened)
            this.scope.$watch('this.database', function () {
                _this.getRepository(_this.database.id);
                if (_this.isFirstOpen)
                    _this.getSimulationModel(_this.database.id);
            });
        }
        RegistryCtrl.prototype.getRepository = function (id) {
            var _this = this;
            this.initialising = true;

            var cacheId = "repo-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.Repository().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (spase) {
                    _this.registryService.cachedElements[cacheId] = spase.resources.map(function (r) {
                        return r.repository;
                    });
                    _this.scope.$broadcast('update-repositories', cacheId);
                    _this.initialising = false;
                });
            } else {
                this.scope.$broadcast('update-repositories', cacheId);
                this.initialising = false;
            }
        };

        // takes repository id
        RegistryCtrl.prototype.getSimulationModel = function (id) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-models');
            this.loading = true;
            var cacheId = "model-" + id;

            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.SimulationModel().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (spase) {
                    _this.registryService.cachedElements[cacheId] = spase.resources.map(function (r) {
                        return r.simulationModel;
                    });
                    _this.scope.$broadcast('update-simulation-models', cacheId);
                    _this.loading = false;
                });
            } else {
                this.scope.$broadcast('update-simulation-models', cacheId);
                this.loading = false;
            }
        };

        // takes simulation model
        RegistryCtrl.prototype.getSimulationRun = function (element) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-runs', element);
            this.loading = true;
            var cacheId = "run-" + element.resourceId;

            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.SimulationRun().get({ fmt: 'json', id: element.resourceId }).$promise;

                this.registryPromise.then(function (spase) {
                    _this.registryService.cachedElements[cacheId] = spase.resources.map(function (r) {
                        return r.simulationRun;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-simulation-runs', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no simulation run found');
                    _this.loading = false;
                });
            } else {
                this.scope.$broadcast('update-simulation-runs', cacheId);
                this.loading = false;
            }
        };

        // takes simulation run
        RegistryCtrl.prototype.getNumericalOutput = function (element) {
            var _this = this;
            this.scope.$broadcast('clear-numerical-outputs', element);
            this.loading = true;
            var cacheId = "output-" + element.resourceId;

            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.NumericalOutput().get({ fmt: 'json', id: element.resourceId }).$promise;

                this.registryPromise.then(function (spase) {
                    _this.registryService.cachedElements[cacheId] = spase.resources.map(function (r) {
                        return r.numericalOutput;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-numerical-outputs', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no numerical output found');
                    _this.loading = false;
                });
            } else {
                this.scope.$broadcast('update-numerical-outputs', cacheId);
                this.loading = false;
            }
        };

        // takes numerical output
        RegistryCtrl.prototype.getGranule = function (element) {
            var _this = this;
            this.scope.$broadcast('clear-granules', element);
            this.loading = true;
            var cacheId = "granule-" + element.resourceId;

            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.Granule().get({ fmt: 'json', id: element.resourceId }).$promise;

                this.registryPromise.then(function (spase) {
                    _this.registryService.cachedElements[cacheId] = spase.resources.map(function (r) {
                        return r.granule;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-granules', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no granule found');
                    _this.loading = false;
                });
            } else {
                this.scope.$broadcast('update-granules', cacheId);
                this.loading = false;
            }
        };

        // method for modal
        RegistryCtrl.prototype.saveRegistry = function (save) {
            if (save)
                this.modalInstance.close();
else
                this.modalInstance.dismiss();
        };
        RegistryCtrl.$inject = [
            '$scope',
            '$timeout',
            'configService',
            'registryService',
            '$state',
            '$modalInstance',
            'id'
        ];
        return RegistryCtrl;
    })();
    portal.RegistryCtrl = RegistryCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // @TODO this Controller needs major refactoring
    // move stuff to directives
    var MethodsCtrl = (function () {
        function MethodsCtrl($scope, $timeout, $window, configService, methodsService, userService, $state, $modalInstance, id) {
            var _this = this;
            this.methods = [];
            this.initialising = false;
            this.status = '';
            this.showError = false;
            this.currentMethod = null;
            this.request = {};
            // needed for VOTableURL => move to directive later
            this.votableRows = [];
            this.votableColumns = null;
            this.votableMetadata = [];
            this.selected = [];
            // helpers for methods modal
            this.dropdownStatus = {
                isopen: false,
                active: 'Choose Method'
            };
            this.scope = $scope;
            $scope.methvm = this;
            this.timeout = $timeout;
            this.window = $window;
            this.configService = configService;
            this.methodsService = methodsService;
            this.userService = userService;
            this.state = $state;
            this.modalInstance = $modalInstance;
            this.applyableModels = {};
            this.database = this.configService.getDatabase(id);

            if (this.methodsService.methods) {
                this.methods = this.methodsService.getMethods(this.database);

                if (this.database.id in this.userService.sessionStorage.methods) {
                    // we must do it with timeout (since we send broadcasts and dir is not fully loaded)
                    this.timeout(function () {
                        return _this.setActive(_this.methods.filter(function (m) {
                            return m.path == _this.userService.sessionStorage.methods[_this.database.id].path;
                        })[0]);
                    });
                }
            } else {
                this.loadMethodsAPI();
            }

            // fill manual applyables for SINP (no API info available) => @TODO move to MethodsService
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Earth/OnFly'] = [
                'getDataPointValueSINP',
                'calculateDataPointValue',
                'calculateDataPointValueSpacecraft',
                'calculateDataPointValueFixedTime',
                'calculateFieldline',
                'calculateCube',
                'calculateFieldline',
                'getSurfaceSINP'
            ];
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Mercury/OnFly'] = ['calculateDataPointValueNercury', 'calculateCubeMercury'];
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Saturn/OnFly'] = ['calculateDataPointValueSaturn', 'calculateCubeSaturn'];
        }
        MethodsCtrl.prototype.loadMethodsAPI = function () {
            var _this = this;
            this.initialising = true;
            this.status = '';
            this.showError = false;
            this.methodsService.getMethodsAPI().then(function (data) {
                return _this.handleAPIData(data);
            }, function (error) {
                return _this.handleAPIError(error);
            });
        };

        MethodsCtrl.prototype.handleAPIData = function (data) {
            var _this = this;
            this.initialising = false;
            this.status = 'success';

            // we always get the right thing
            this.methodsService.methods = data;
            this.methods = this.methodsService.getMethods(this.database);

            if (this.database.id in this.userService.sessionStorage.methods) {
                this.setActive(this.methods.filter(function (m) {
                    return m.path == _this.userService.sessionStorage.methods[_this.database.id].path;
                })[0]);
            }
        };

        MethodsCtrl.prototype.handleAPIError = function (error) {
            this.initialising = false;
            if (this.window.confirm('connection timed out. retry?'))
                this.loadMethodsAPI();
else {
                this.showError = true;
                if (error.status = 404)
                    this.status = error.status + ' resource not found';
else
                    this.status = error.data + ' ' + error.status;
            }
        };

        // handling and saving the WS result
        MethodsCtrl.prototype.handleServiceData = function (data) {
            //console.log('success: '+JSON.stringify(data.message))
            // new result id
            var id = this.userService.createId();
            this.userService.user.results.push(new portal.Result(this.database.id, id, this.currentMethod.path, data));

            // refresh localStorage
            this.userService.localStorage.results = this.userService.user.results;
            this.scope.$broadcast('update-results', id);
            this.methodsService.status = 'Added service result to user data';

            // system notification
            this.methodsService.notify('success', this.database.id);
        };

        MethodsCtrl.prototype.handleServiceError = function (error) {
            if (error.status == 404) {
                this.methodsService.status = error.status + ' resource not found';
            } else if (error.status == 500) {
                this.methodsService.status = error.status + ' internal server error';
            } else {
                var response = error.data;
                this.methodsService.status = response.message;
            }

            // system notification
            this.methodsService.notify('error', this.database.id);
        };

        // method for submission
        MethodsCtrl.prototype.submitMethod = function () {
            var _this = this;
            //console.log('submitted '+this.currentMethod.path+' '+this.request['id'])
            this.methodsService.status = 'Loading data from service';

            // system notification
            this.methodsService.notify('loading', this.database.id);
            var httpMethod = this.currentMethod.operations[0].method;
            if (httpMethod == 'GET') {
                this.methodsPromise = this.methodsService.requestMethod(this.currentMethod.path, this.request).get().$promise;
                this.methodsPromise.then(function (data) {
                    return _this.handleServiceData(data);
                }, function (error) {
                    return _this.handleServiceError(error);
                });
                // only getVOTableURL atm
            } else if (httpMethod == 'POST') {
                this.methodsPromise = this.methodsService.requestMethod(this.currentMethod.path).save(this.request).$promise;
                this.methodsPromise.then(function (data) {
                    return _this.handleServiceData(data);
                }, function (error) {
                    return _this.handleServiceError(error);
                });
            }
        };

        // retry if alert is cancelled
        MethodsCtrl.prototype.retry = function () {
            this.loadMethodsAPI();
        };

        // set a method active and forward info to directives
        MethodsCtrl.prototype.setActive = function (method) {
            var _this = this;
            this.dropdownStatus.active = this.trimPath(method.path);
            this.currentMethod = method;

            // reset the request object
            this.request = {};

            // there is only one operation per method
            this.currentMethod.operations[0].parameters.forEach(function (p) {
                _this.request[p.name] = p.defaultValue;
            });

            if (this.database.id in this.userService.sessionStorage.methods) {
                if (this.userService.sessionStorage.methods[this.database.id].path == this.currentMethod.path) {
                    this.request = this.userService.sessionStorage.methods[this.database.id].params;

                    if ('Fields' in this.request) {
                        this.votableColumns = this.request['Fields'].length;
                        var rows = this.request['Fields'][0].data.length;
                        for (var j = 0; j < rows; j++)
                            this.votableRows[j] = [];
                        for (var i = 0; i < this.votableColumns; i++) {
                            this.votableMetadata[i] = [
                                { name: 'name', value: this.request['Fields'][i].name },
                                { name: 'ID', value: this.request['Fields'][i].ID },
                                { name: 'unit', value: this.request['Fields'][i].unit },
                                { name: 'datatype', value: this.request['Fields'][i].datatype },
                                { name: 'ucd', value: this.request['Fields'][i].ucd }
                            ];
                            this.selected[i] = this.votableMetadata[i][0];
                            for (var j = 0; j < rows; j++)
                                this.votableRows[j].push(this.request['Fields'][i].data[j]);
                        }
                    }
                } else {
                    this.userService.sessionStorage.methods[this.database.id] = new portal.MethodState(method.path, this.request);
                }
            } else {
                this.userService.sessionStorage.methods[this.database.id] = new portal.MethodState(method.path, this.request);
            }

            if (this.currentMethod.operations[0].parameters.filter(function (e) {
                return e.name == 'id';
            }).length != 0) {
                // there is only one id param per method
                var param = this.currentMethod.operations[0].parameters.filter(function (e) {
                    return e.name == 'id';
                })[0];
                this.scope.$broadcast('set-applyable-elements', param.description);
            } else {
                // if there is no id, broadcast empty string
                this.scope.$broadcast('set-applyable-elements', '');
            }

            if (this.currentMethod.operations[0].parameters.filter(function (e) {
                return e.name == 'votable_url';
            }).length != 0) {
                this.scope.$broadcast('set-applyable-votable', true);
            } else {
                // if there is no url field return false
                this.scope.$broadcast('set-applyable-votable', false);
            }

            if (this.currentMethod.path.indexOf('SINP') != -1) {
                for (var key in this.applyableModels) {
                    var methods = this.applyableModels[key];
                    var index = methods.indexOf(this.currentMethod.operations[0].nickname);
                    if (index != -1)
                        this.scope.$broadcast('set-applyable-models', key);
                }
            }
        };

        MethodsCtrl.prototype.isActive = function (path) {
            return this.dropdownStatus.active == this.trimPath(path);
        };

        MethodsCtrl.prototype.getActive = function () {
            return this.dropdownStatus.active;
        };

        MethodsCtrl.prototype.trimPath = function (path) {
            var splitPath = path.split('/').reverse();
            return splitPath[0];
        };

        MethodsCtrl.prototype.resetRequest = function () {
            var _this = this;
            // reset the request object
            this.request = {};

            // there is only one operation per method
            this.currentMethod.operations[0].parameters.forEach(function (p) {
                _this.request[p.name] = p.defaultValue;
            });

            // savely reset VOTableURL form
            this.votableColumns = null;
            this.votableRows = [];
            this.votableMetadata = [];
            this.selected = [];
            this.userService.sessionStorage.methods[this.database.id].params = this.request;
        };

        // method for applying a selection to the current method
        MethodsCtrl.prototype.applySelection = function (resourceId) {
            //console.log("applySelection "+resourceId)
            this.request['id'] = resourceId;
        };

        // method for applying a votable url to the current method
        MethodsCtrl.prototype.applyVOTable = function (url) {
            //console.log("applyVOTable "+url)
            this.request['votable_url'] = url;
        };

        MethodsCtrl.prototype.updateRequest = function (paramName) {
            this.userService.sessionStorage.methods[this.database.id].params[paramName] = this.request[paramName];
        };

        MethodsCtrl.prototype.updateRequestDate = function (paramName) {
            var iso = new Date(this.request[paramName]);
            this.request[paramName] = iso.toISOString();
            this.userService.sessionStorage.methods[this.database.id].params[paramName] = this.request[paramName];
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.refreshVotableHeader = function () {
            if (angular.isNumber(this.votableColumns)) {
                for (var i = 0; i < this.votableColumns; i++) {
                    this.votableMetadata[i] = [
                        { name: 'name', value: '' },
                        { name: 'ID', value: '' },
                        { name: 'unit', value: '' },
                        { name: 'datatype', value: '' },
                        { name: 'ucd', value: '' }
                    ];
                    this.selected[i] = null;
                }
                this.addVotableRow();
            }
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.updateVotableHeader = function (index) {
            var _this = this;
            this.votableMetadata[index] = this.votableMetadata[index].map(function (m) {
                if (m.name == _this.selected[index].name)
                    return _this.selected[index];
else
                    return m;
            });
            this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.addVotableRow = function () {
            var arr = [];
            for (var i = 0; i < this.votableColumns; i++) {
                arr[i] = "Field-" + (this.votableRows.length + 1) + "-" + (i + 1);
            }
            this.votableRows.push(arr);
            this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.deleteVotableRow = function (index) {
            this.votableRows.splice(index, 1);
            if (this.votableRows.length == 0)
                this.addVotableRow();
else
                this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.addVotableColumn = function () {
            var _this = this;
            this.votableColumns++;
            this.votableRows.forEach(function (r) {
                return r.push("Field-" + _this.votableColumns);
            });
            this.votableMetadata.push([
                { name: 'name', value: '' },
                { name: 'ID', value: '' },
                { name: 'unit', value: '' },
                { name: 'datatype', value: '' },
                { name: 'ucd', value: '' }
            ]);
            this.selected.push(null);
            this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.deleteVotableColumn = function (index) {
            this.votableColumns--;
            if (this.votableColumns == 0)
                this.resetVotable();
else {
                this.votableRows.forEach(function (r) {
                    return r.splice(index, 1);
                });
                this.votableMetadata.splice(index, 1);
                this.selected.splice(index, 1);
            }
            this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.resetVotable = function () {
            this.votableColumns = null;
            this.votableRows = [];
            this.votableMetadata = [];
            this.selected = [];
            this.updateVOtableRequest();
        };

        // used for getVOTableURL form
        MethodsCtrl.prototype.updateVOtableRequest = function () {
            var _this = this;
            // filling the fields
            this.request['Fields'] = this.votableMetadata.map(function (col, key) {
                var data = _this.votableRows.map(function (r) {
                    return r[key];
                });
                var result = {};
                result['data'] = data;
                col.forEach(function (i) {
                    if (i.value != '')
                        result[i.name] = i.value;
                });
                return result;
            });
        };

        // method for modal
        MethodsCtrl.prototype.saveMethods = function (save) {
            if (save)
                this.modalInstance.close();
else
                this.modalInstance.dismiss();
            this.showError = false;
        };
        MethodsCtrl.$inject = [
            '$scope',
            '$timeout',
            '$window',
            'configService',
            'methodsService',
            'userService',
            '$state',
            '$modalInstance',
            'id'
        ];
        return MethodsCtrl;
    })();
    portal.MethodsCtrl = MethodsCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var UserDataCtrl = (function () {
        function UserDataCtrl($scope, $timeout, userService, $state, $modalInstance, $upload, growl) {
            this.upload = [];
            this.initialising = false;
            this.selectedFiles = [];
            this.progress = [];
            this.scope = $scope;
            $scope.datavm = this;
            this.timeout = $timeout;
            this.userService = userService;
            this.state = $state;
            this.modalInstance = $modalInstance;
            this.uploader = $upload;
            this.growl = growl;
        }
        // @TODO add drag over later
        // see: https://github.com/danialfarid/angular-file-upload/blob/master/demo/war/js/upload.js
        UserDataCtrl.prototype.onFileSelect = function ($files) {
            this.selectedFiles = $files;
            this.progress = [];
            if (this.upload && this.upload.length > 0) {
                for (var i = 0; i < this.upload.length; i++) {
                    if (this.upload[i] != null) {
                        this.upload[i].abort();
                    }
                }
            }
            for (var i = 0; i < $files.length; i++) {
                this.handleUpload(i);
            }
        };

        UserDataCtrl.prototype.handleUpload = function (i) {
            var _this = this;
            this.progress[i] = 0;
            this.upload[i] = this.uploader.upload({
                url: '/userdata',
                method: 'POST',
                file: this.selectedFiles[i],
                fileFormDataName: 'votable'
            });
            this.upload[i].success(function (response) {
                _this.timeout(function () {
                    var votable = response;

                    // adding the info of the posted votable to userService
                    _this.userService.user.voTables.push(votable);
                    _this.scope.$broadcast('update-votables', votable.id);
                    _this.growl.success('Added VOTable to user data');
                });
            }).error(function (response) {
                if (response.status > 0) {
                    _this.growl.error(response.status + ': ' + response.data);
                }
            }).progress(function (evt) {
                _this.progress[i] = Math.min(100, 100.0 * evt.loaded / evt.total);
            });
        };

        // method for modal
        UserDataCtrl.prototype.saveData = function (save) {
            if (save)
                this.modalInstance.close();
else
                this.modalInstance.dismiss();
        };
        UserDataCtrl.$inject = [
            '$scope',
            '$timeout',
            'userService',
            '$state',
            '$modalInstance',
            '$upload',
            'growl'
        ];
        return UserDataCtrl;
    })();
    portal.UserDataCtrl = UserDataCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var DatabasesDir = (function () {
        function DatabasesDir(configService) {
            var _this = this;
            this.configService = configService;
            this.templateUrl = '/public/partials/templates/databasesDir.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        DatabasesDir.prototype.injection = function () {
            return [
                'configService',
                function (configService) {
                    return new DatabasesDir(configService);
                }
            ];
        };

        DatabasesDir.prototype.linkFn = function ($scope, element, attributes) {
            $scope.dbdirvm = this;
            this.myScope = $scope;
            this.config = this.configService.config;
        };
        return DatabasesDir;
    })();
    portal.DatabasesDir = DatabasesDir;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var RegistryDir = (function () {
        function RegistryDir(registryService, userService) {
            var _this = this;
            this.oneAtATime = true;
            this.showError = false;
            this.status = '';
            this.repositoryId = null;
            // container for intermediate results
            this.repositories = [];
            this.simulationModels = [];
            this.simulationRuns = [];
            this.numericalOutputs = [];
            this.granules = [];
            this.activeItems = {};
            this.registryService = registryService;
            this.userService = userService;
            this.templateUrl = '/public/partials/templates/registryDir.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        RegistryDir.prototype.injection = function () {
            return [
                'registryService',
                'userService',
                function (registryService, userService) {
                    return new RegistryDir(registryService, userService);
                }
            ];
        };

        RegistryDir.prototype.linkFn = function ($scope, element, attributes) {
            var _this = this;
            this.myScope = $scope;
            $scope.regdirvm = this;

            attributes.$observe('db', function (id) {
                _this.repositoryId = id;
            });

            this.myScope.$on('registry-error', function (e, msg) {
                _this.showError = true;
                _this.status = msg;
            });

            this.myScope.$watch('$includeContentLoaded', function (e) {
                //console.log("RegistryDir loaded")
                _this.activeItems = {};
                _this.showError = false;
                _this.status = '';
            });

            this.myScope.$on('clear-simulation-models', function (e) {
                _this.activeItems = {};
                _this.showError = false;
                _this.simulationModels = [];
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            this.myScope.$on('clear-simulation-runs', function (e, element) {
                _this.setActive('SimulationModel', element);
                _this.setInactive('SimulationRun');
                _this.setInactive('NumericalOutput');
                _this.setInactive('Granule');
                _this.showError = false;
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            this.myScope.$on('clear-numerical-outputs', function (e, element) {
                _this.setActive('SimulationRun', element);
                _this.setInactive('NumericalOutput');
                _this.setInactive('Granule');
                _this.showError = false;
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            this.myScope.$on('clear-granules', function (e, element) {
                _this.setActive('NumericalOutput', element);
                _this.setInactive('Granule');
                _this.showError = false;
                _this.granules = [];
            });

            this.myScope.$on('update-repositories', function (e, id) {
                _this.repositories = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            this.myScope.$on('update-simulation-models', function (e, id) {
                _this.simulationModels = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            this.myScope.$on('update-simulation-runs', function (e, id) {
                _this.simulationRuns = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            this.myScope.$on('update-numerical-outputs', function (e, id) {
                _this.numericalOutputs = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            this.myScope.$on('update-granules', function (e, id) {
                _this.granules = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });
        };

        RegistryDir.prototype.setActive = function (type, element) {
            this.activeItems[type] = element;

            // new selection id
            var id = this.userService.createId();
            this.userService.user.activeSelection = [new portal.Selection(this.repositoryId, id, type, element)];
            this.myScope.$broadcast('update-selections', id);
        };

        RegistryDir.prototype.setInactive = function (type) {
            this.activeItems[type] = null;
        };

        RegistryDir.prototype.isActive = function (type, element) {
            return this.activeItems[type] === element;
        };

        RegistryDir.prototype.trim = function (name, length) {
            if (typeof length === "undefined") { length = 25; }
            if (name.length > length)
                return name.slice(0, length).trim() + "...";
else
                return name.trim();
        };

        RegistryDir.prototype.format = function (name) {
            return name.split("_").join(" ").trim();
        };
        return RegistryDir;
    })();
    portal.RegistryDir = RegistryDir;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var UserDataDir = (function () {
        function UserDataDir(registryService, userService, $state, growl) {
            var _this = this;
            this.repositoryId = null;
            this.isCollapsed = {};
            // currently applyable elements (according to current method)
            this.applyableElements = [];
            this.isSelApplyable = false;
            this.isVOTApplyable = false;
            // for SINP models/outputs
            this.applyableModel = null;
            // active tabs (first by default)
            this.tabsActive = [];
            this.selectables = [];
            this.registryService = registryService;
            this.userService = userService;
            this.state = $state;
            this.growl = growl;
            this.templateUrl = '/public/partials/templates/userdataDir.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        UserDataDir.prototype.injection = function () {
            return [
                'registryService',
                'userService',
                '$state',
                'growl',
                function (registryService, userService, $state, growl) {
                    return new UserDataDir(registryService, userService, $state, growl);
                }
            ];
        };

        UserDataDir.prototype.linkFn = function ($scope, element, attributes) {
            var _this = this;
            $scope.userdirvm = this;
            this.myScope = $scope;
            this.user = this.userService.user;

            attributes.$observe('db', function (id) {
                _this.selectables = _this.registryService.selectables[id];
                _this.repositoryId = id;
            });

            // watch event when all content is loaded into the dir
            this.myScope.$watch('$includeContentLoaded', function (e) {
                if (_this.user.selections) {
                    _this.user.selections.forEach(function (e) {
                        _this.isCollapsed[e.id] = true;
                    });
                }

                if (_this.user.voTables) {
                    _this.user.voTables.forEach(function (e) {
                        _this.isCollapsed[e.id] = true;
                    });
                }

                if (_this.user.results) {
                    _this.user.results.forEach(function (e) {
                        _this.isCollapsed[e.id] = true;
                    });
                }

                // reset expanded selections
                _this.user.activeSelection = [];

                // reset applyables
                _this.applyableElements = [];
                _this.applyableModel = null;

                // init tabs
                _this.tabsActive = [true, false, false];
                _this.isSelApplyable = false;
                _this.isVOTApplyable = false;
            });

            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-elements', function (e, elements) {
                //console.log('set-applyable-elements')
                _this.applyableElements = elements.split(',').map(function (e) {
                    return e.trim();
                });
                _this.isSelApplyable = false;
                if (_this.user.activeSelection.length == 1) {
                    _this.applyableElements.forEach(function (e) {
                        if (_this.user.activeSelection[0].type == e)
                            _this.isSelApplyable = true;
                    });
                }
            });

            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-votable', function (e, b) {
                //console.log('set-applyable-votable')
                _this.isVOTApplyable = b;
            });

            // comes from MethodsCtrl => needed for SINP models/output
            this.myScope.$on('set-applyable-models', function (e, m) {
                //console.log(m)
                //console.log('set-applyable-models')
                _this.applyableModel = m;
                if (_this.user.activeSelection.length == 1) {
                    var element = _this.user.activeSelection[0];
                    var output = _this.applyableModel.replace('SimulationModel', 'NumericalOutput');
                    var index = element.elem.resourceId.indexOf(output.replace('OnFly', ''));
                    if (_this.applyableModel == element.elem.resourceId || index != -1)
                        _this.isSelApplyable = true;
else
                        _this.isSelApplyable = false;
                }
            });

            // comes from RegistryDir
            this.myScope.$on('update-selections', function (e, id) {
                _this.isCollapsed[id] = false;

                for (var rId in _this.isCollapsed) {
                    if (rId != id)
                        _this.isCollapsed[rId] = true;
                }

                // set selections tab active
                _this.tabsActive = _this.tabsActive.map(function (t) {
                    return t = false;
                });
                _this.tabsActive[0] = true;
            });

            // comes from UserDataCtrl
            this.myScope.$on('update-votables', function (e, id) {
                // reset expanded selection
                _this.user.activeSelection = [];
                _this.isCollapsed[id] = false;

                for (var rId in _this.isCollapsed) {
                    if (rId != id)
                        _this.isCollapsed[rId] = true;
                }

                // set votable tab active
                _this.tabsActive = _this.tabsActive.map(function (t) {
                    return t = false;
                });
                _this.tabsActive[1] = true;
            });

            // comes from MethodsCtrl
            this.myScope.$on('update-results', function (e, id) {
                // reset expanded selection
                _this.user.activeSelection = [];
                _this.isCollapsed[id] = false;

                for (var rId in _this.isCollapsed) {
                    if (rId != id)
                        _this.isCollapsed[rId] = true;
                }

                // set result tab active
                _this.tabsActive = _this.tabsActive.map(function (t) {
                    return t = false;
                });
                _this.tabsActive[2] = true;
            });
        };

        UserDataDir.prototype.isSelectable = function (type) {
            if (type == 'SimulationModel' && this.repositoryId.indexOf('SINP') != -1) {
                if (this.user.activeSelection[0].elem.resourceId.indexOf('Static') != -1)
                    return false;
else
                    return true;
            } else if (type == 'Granule') {
                return true;
            } else if (this.state.current.name == 'app.portal.userdata') {
                // there is nothing to select in my data modal
                return false;
            } else
                return this.selectables.indexOf(type) != -1;
        };

        UserDataDir.prototype.isSelSaved = function (id) {
            if (this.user.selections.filter(function (s) {
                return s.id == id;
            }).length == 1)
                return true;
else
                return false;
        };

        UserDataDir.prototype.saveSelection = function (type, elem) {
            // get active selection
            var selection = this.user.activeSelection.filter(function (e) {
                return e.elem === elem;
            })[0];
            this.isCollapsed[selection.id] = false;

            for (var rId in this.isCollapsed) {
                if (rId != selection.id)
                    this.isCollapsed[rId] = true;
            }

            this.userService.user.selections.push(selection);

            // refresh localStorage
            this.userService.localStorage.selections = this.userService.user.selections;

            // set selections tab active
            this.tabsActive = this.tabsActive.map(function (t) {
                return t = false;
            });
            this.tabsActive[0] = true;
            this.growl.success('Saved selection to user data.');
        };

        UserDataDir.prototype.toggleSelectionDetails = function (id) {
            // reset expanded selection
            this.user.activeSelection = [];
            if (this.isCollapsed[id]) {
                this.isCollapsed[id] = false;

                for (var rId in this.isCollapsed) {
                    if (rId != id)
                        this.isCollapsed[rId] = true;
                }

                // get selection
                var selection = this.user.selections.filter(function (e) {
                    return e.id == id;
                })[0];

                if (this.applyableElements.indexOf(selection.type) != -1) {
                    this.isSelApplyable = true;

                    if (this.applyableModel) {
                        var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput');
                        var index = selection.elem.resourceId.indexOf(output.replace('OnFly', ''));
                        if (this.applyableModel == selection.elem.resourceId || index != -1)
                            this.isSelApplyable = true;
else
                            this.isSelApplyable = false;
                    }
                }
                this.user.activeSelection = [selection];
            } else {
                this.isCollapsed[id] = true;

                // set isApplyable to false
                this.isSelApplyable = false;
            }
        };

        UserDataDir.prototype.toggleDetails = function (id) {
            // reset expanded selection
            this.user.activeSelection = [];
            if (this.isCollapsed[id]) {
                this.isCollapsed[id] = false;

                for (var rId in this.isCollapsed) {
                    if (rId != id)
                        this.isCollapsed[rId] = true;
                }
            } else {
                this.isCollapsed[id] = true;
            }
        };

        UserDataDir.prototype.deleteSelection = function (id) {
            // update local selections
            this.user.selections = this.user.selections.filter(function (e) {
                return e.id != id;
            });

            // update global service/localStorage
            this.userService.user.selections = this.userService.user.selections.filter(function (e) {
                return e.id != id;
            });
            this.userService.localStorage.selections = this.userService.user.selections;

            // safely clear currentSelection
            this.user.activeSelection = [];

            // delete collapsed info
            delete this.isCollapsed[id];
            this.growl.info('Deleted selection from user data');
        };

        UserDataDir.prototype.deleteVOTable = function (vot) {
            // update local votables
            this.user.voTables = this.user.voTables.filter(function (e) {
                return e.id != vot.id;
            });

            // update global service
            this.userService.user.voTables = this.userService.user.voTables.filter(function (e) {
                return e.id != vot.id;
            });

            // delete file on server
            this.userService.deleteUserData(vot.url.split('/').reverse()[0]);

            // delete collapsed info
            delete this.isCollapsed[vot.id];
            this.growl.info('Deleted VOTable from user data');
        };

        UserDataDir.prototype.deleteResult = function (id) {
            // update local selection
            this.user.results = this.user.results.filter(function (e) {
                return e.id != id;
            });

            // update global service/localStorage
            this.userService.user.results = this.userService.user.results.filter(function (e) {
                return e.id != id;
            });
            this.userService.localStorage.results = this.userService.user.results;

            // delete collapsed info
            delete this.isCollapsed[id];
            this.growl.info('Deleted result from user data');
        };
        return UserDataDir;
    })();
    portal.UserDataDir = UserDataDir;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // @TODO implement loading visualisation
    var SelectionDir = (function () {
        function SelectionDir() {
            this.template = "<ul>" + "<member-dir ng-repeat='(key, elem) in selection' name='key' member='elem' " + "ng-if='!(elem | isEmpty)'></member-dir>" + "</ul>";
            this.restrict = 'E';
            this.replace = true;
            this.scope = {
                selection: '='
            };
        }
        SelectionDir.prototype.injection = function () {
            return [
                function () {
                    return new SelectionDir();
                }
            ];
        };
        return SelectionDir;
    })();
    portal.SelectionDir = SelectionDir;

    var MemberDir = (function () {
        function MemberDir($compile) {
            var _this = this;
            this.compileService = $compile;
            this.template = '<li></li>';
            this.restrict = 'E';
            this.replace = true;
            this.scope = {
                name: '=',
                member: '='
            };
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        MemberDir.prototype.injection = function () {
            return [
                '$compile',
                function ($compile) {
                    return new MemberDir($compile);
                }
            ];
        };

        MemberDir.prototype.linkFn = function ($scope, element, attributes) {
            var _this = this;
            element.append("<strong>" + this.beautify($scope.name) + "</strong> : ");
            if (angular.isArray($scope.member)) {
                angular.forEach($scope.member, function (m, i) {
                    if (angular.isString(m) || angular.isNumber(m))
                        element.append(m + " ");
else if (angular.isObject(m)) {
                        // this is a really cool hack
                        _this.compileService("<br/><selection-dir selection='" + JSON.stringify($scope.member[i]) + "'></selection-dir>")($scope, function (cloned, scope) {
                            element.append(cloned);
                        });
                    }
                });
            } else if (angular.isObject($scope.member)) {
                element.append("<br/><selection-dir selection='member'></selection-dir>");
                this.compileService(element.contents())($scope);
            } else if (this.validateUrl($scope.member)) {
                element.append("<a href='" + $scope.member + "' target='_blank'>" + $scope.member + "</a><br/>");
            } else {
                element.append($scope.member + "<br/>");
            }
        };

        MemberDir.prototype.beautify = function (str) {
            if (str.indexOf("_") != -1) {
                var split = str.split("_");
                str = split[0] + split[1].charAt(0).toUpperCase() + split[1].slice(1);
            }
            var array = str.match(/([A-Z]?[^A-Z]*)/g).slice(0, -1);
            var first = array[0].charAt(0).toUpperCase() + array[0].slice(1);
            array.shift();
            return (first + " " + array.join(" ")).trim();
        };

        MemberDir.prototype.validateUrl = function (str) {
            var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
            if (!pattern.test(str)) {
                return false;
            } else {
                return true;
            }
        };
        return MemberDir;
    })();
    portal.MemberDir = MemberDir;
})(portal || (portal = {}));
/// <reference path='_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var impexPortal = angular.module('portal', [
        'ui.bootstrap',
        'ui.bootstrap.datetimepicker',
        'ui.router',
        'ngResource',
        'ngStorage',
        'angularFileUpload',
        'angular-growl'
    ]);

    impexPortal.service('configService', portal.ConfigService);
    impexPortal.service('registryService', portal.RegistryService);
    impexPortal.service('methodsService', portal.MethodsService);
    impexPortal.service('userService', portal.UserService);

    impexPortal.controller('configCtrl', portal.ConfigCtrl);
    impexPortal.controller('portalCtrl', portal.PortalCtrl);
    impexPortal.controller('registryCtrl', portal.RegistryCtrl);
    impexPortal.controller('methodsCtrl', portal.MethodsCtrl);
    impexPortal.controller('userDataCtrl', portal.UserDataCtrl);

    impexPortal.directive('databasesDir', portal.DatabasesDir.prototype.injection());
    impexPortal.directive('registryDir', portal.RegistryDir.prototype.injection());
    impexPortal.directive('userDataDir', portal.UserDataDir.prototype.injection());
    impexPortal.directive('selectionDir', portal.SelectionDir.prototype.injection());

    // is in SelectionDir.ts
    impexPortal.directive('memberDir', portal.MemberDir.prototype.injection());

    impexPortal.config([
        '$stateProvider',
        '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.otherwise('/portal');

            $stateProvider.state(new portal.App()).state(new portal.Portal()).state(new portal.Registry()).state(new portal.Methods()).state(new portal.MyData()).state(new portal.Databases());
        }
    ]);

    // global tooltip config
    impexPortal.config([
        '$tooltipProvider',
        function ($tooltipProvider) {
            $tooltipProvider.options({
                'placement': 'bottom',
                'animation': 'false',
                'popupDelay': '0',
                'appendToBody': 'true'
            });
        }
    ]);

    // global growl config
    impexPortal.config([
        'growlProvider',
        function (growlProvider) {
            growlProvider.globalTimeToLive(4000);
            growlProvider.onlyUniqueMessages(false);
        }
    ]);

    // custom filter for checking if an array is empty
    impexPortal.filter('isEmpty', function () {
        var bar;
        return function (obj) {
            for (bar in obj) {
                if (obj.hasOwnProperty(bar)) {
                    return false;
                }
            }
            return true;
        };
    });

    impexPortal.run([
        '$rootScope',
        '$window',
        function ($rootScope, $window) {
            $rootScope.windowWidth = $window.outerWidth;
            angular.element($window).bind('resize', function () {
                $rootScope.windowWidth = $window.outerWidth;
                $rootScope.$apply('windowWidth');
            });
        }
    ]);

    impexPortal.run([
        '$rootScope',
        '$state',
        '$window',
        function ($rootScope, $state, $window) {
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                //console.log("FROM "+JSON.stringify(fromState)+JSON.stringify(fromParams))
                //console.log("TO "+JSON.stringify(toState)+JSON.stringify(toParams))
            });

            // @TODO when we use resolver, we must check errors on promises here!
            $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
                if ($window.confirm('connection timed out. retry?'))
                    $state.transitionTo(toState, toParams);
            });
        }
    ]);
})(portal || (portal = {}));
