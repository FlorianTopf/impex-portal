/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var App = (function () {
        function App() {
            this.name = 'app';
            this.abstract = true;
            //public url: string = ''
            this.controller = portal.ConfigCtrl;
            this.template = '<ui-view/>';
            //public templateUrl: string =  '/public/partials/config.html'
            this.resolve = {
                config: [
                    'configService',
                    function (ConfigService) {
                        return ConfigService.loadConfig();
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

    // @TODO let's see what the user object needs to have
    var User = (function () {
        function User(id) {
            this.id = id;
            this.results = [];
            this.selections = [];
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
        function ConfigService($resource) {
            this.url = '/';
            this.config = null;
            // creates an action descriptor
            this.configAction = {
                method: 'GET',
                isArray: false
            };
            this.resource = $resource;
        }
        // returns the resource handler
        ConfigService.prototype.getConfig = function () {
            return this.resource(this.url + 'config?', { fmt: '@fmt' }, { getConfig: this.configAction });
        };

        // returns promise for resource handler
        ConfigService.prototype.loadConfig = function () {
            return this.getConfig().get({ fmt: 'json' }).$promise;
        };

        ConfigService.prototype.getDatabase = function (id) {
            return this.config.databases.filter(function (e) {
                return e.id == id;
            })[0];
        };
        ConfigService.$inject = ['$resource'];
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
                /* params: {
                id: '@id',
                fmt: '@fmt'
                },*/
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
        RegistryService.prototype.getRepository = function () {
            return this.resource(this.url + 'registry/repository?', { id: '@id', fmt: '@fmt' }, { getRepository: this.registryAction });
        };

        RegistryService.prototype.getSimulationModel = function () {
            return this.resource(this.url + 'registry/simulationmodel?', { id: '@id', fmt: '@fmt' }, { getSimulationModel: this.registryAction });
        };

        RegistryService.prototype.getSimulationRun = function () {
            return this.resource(this.url + 'registry/simulationrun?', { id: '@id', fmt: '@fmt' }, { getSimulationRun: this.registryAction });
        };

        RegistryService.prototype.getNumericalOutput = function () {
            return this.resource(this.url + 'registry/numericaloutput?', { id: '@id', fmt: '@fmt' }, { getNumericalOutput: this.registryAction });
        };

        RegistryService.prototype.getGranule = function () {
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
        function MethodsService($resource) {
            this.url = '';
            this.methods = null;
            this.loading = false;
            this.status = '';
            this.showError = false;
            // action descriptor for registry actions
            this.methodsAction = {
                method: 'GET',
                isArray: false
            };
            this.resource = $resource;
        }
        // generic method for requesting
        MethodsService.prototype.getMethodsAPI = function () {
            return this.resource(this.url + this.url + 'api-docs/methods', { getMethods: this.methodsAction });
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

        // generic method for requesting standard services (GET + params)
        MethodsService.prototype.requestMethod = function (path, params) {
            return this.resource(path, params, { requestMethod: this.methodsAction });
        };
        MethodsService.$inject = ['$resource'];
        return MethodsService;
    })();
    portal.MethodsService = MethodsService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // @TODO let's see what the user service needs to have
    var UserService = (function () {
        function UserService($localStorage) {
            this.user = null;
            this.localStorage = null;
            this.localStorage = $localStorage;

            // initialise needed keys (doesn't overwrite existing ones)
            this.localStorage.$default({
                results: null,
                selections: null
            });
        }
        UserService.prototype.createId = function () {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1) + Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        };
        UserService.$inject = ['$localStorage'];
        return UserService;
    })();
    portal.UserService = UserService;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var ConfigCtrl = (function () {
        function ConfigCtrl($scope, $timeout, configService, userService, $state, config) {
            this.scope = $scope;
            this.scope.vm = this;
            this.timeout = $timeout;
            this.configService = configService;
            this.userService = userService;
            this.state = $state;

            this.configService.config = config;

            // @TODO this comes from the server in the future (add in resolver)
            // maybe make a combined promise / resolve
            this.userService.user = new portal.User(this.userService.createId());

            if (this.userService.localStorage.results != null)
                this.userService.user.results = this.userService.localStorage.results;

            if (this.userService.localStorage.selections != null)
                this.userService.user.selections = this.userService.localStorage.selections;
        }
        ConfigCtrl.$inject = ['$scope', '$timeout', 'configService', 'userService', '$state', 'config'];
        return ConfigCtrl;
    })();
    portal.ConfigCtrl = ConfigCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var PortalCtrl = (function () {
        function PortalCtrl($scope, $location, $timeout, $interval, $window, configService, registryService, userService, $state, $modal) {
            var _this = this;
            this.ready = false;
            this.showError = false;
            this.scope = $scope;
            this.scope.vm = this;
            this.location = $location;
            this.timeout = $timeout;
            this.interval = $interval;
            this.window = $window;
            this.configService = configService;
            this.config = this.configService.config;
            this.registryService = registryService;
            this.userService = userService;
            this.state = $state;
            this.modal = $modal;

            this.timeout(function () {
                _this.ready = true;
            });
        }
        PortalCtrl.$inject = [
            '$scope',
            '$location',
            '$timeout',
            '$interval',
            '$window',
            'configService',
            'registryService',
            'userService',
            '$state',
            '$modal'
        ];
        return PortalCtrl;
    })();
    portal.PortalCtrl = PortalCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    // @TODO introduce error/offline handling later
    var RegistryCtrl = (function () {
        function RegistryCtrl($scope, $location, $timeout, $window, configService, registryService, userService, $state, $modalInstance, id) {
            var _this = this;
            this.isFirstOpen = true;
            this.database = null;
            this.initialising = false;
            this.loading = false;
            this.transFinished = true;
            this.scope = $scope;
            $scope.regvm = this;
            this.location = $location;
            this.timeout = $timeout;
            this.window = $window;
            this.configService = configService;
            this.registryService = registryService;
            this.userService = userService;
            this.state = $state;
            this.modalInstance = $modalInstance;

            this.database = this.configService.getDatabase(id);

            // watches changes of variable
            //(is changed each time modal is opened)
            this.scope.$watch('this.database', function () {
                _this.getRepository(_this.database.id);
                if (_this.isFirstOpen)
                    _this.getSimulationModel(_this.database.id);
            });
        }
        RegistryCtrl.prototype.getRepository = function (id) {
            var _this = this;
            this.scope.$broadcast('clear-registry');
            this.initialising = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            var cacheId = "repo-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.getRepository().get({ fmt: 'json', id: id }).$promise;

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

        RegistryCtrl.prototype.getSimulationModel = function (id) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-models');
            this.loading = true;

            var cacheId = "model-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.getSimulationModel().get({ fmt: 'json', id: id }).$promise;

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

        RegistryCtrl.prototype.getSimulationRun = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-runs', element);
            this.loading = true;

            var cacheId = "run-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.getSimulationRun().get({ fmt: 'json', id: id }).$promise;

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

        RegistryCtrl.prototype.getNumericalOutput = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-numerical-outputs', element);
            this.loading = true;

            var cacheId = "output-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.getNumericalOutput().get({ fmt: 'json', id: id }).$promise;

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

        RegistryCtrl.prototype.getGranule = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-granules', element);
            this.loading = true;

            var cacheId = "granule-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                this.registryPromise = this.registryService.getGranule().get({ fmt: 'json', id: id }).$promise;

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

        // methods for modal
        RegistryCtrl.prototype.saveRegistry = function () {
            this.modalInstance.close();
            this.scope.$broadcast('clear-registry-error');

            // @TODO just for the moment
            this.scope.$broadcast('clear-registry');
        };

        RegistryCtrl.prototype.cancelRegistry = function () {
            this.modalInstance.dismiss();
            this.scope.$broadcast('clear-registry-error');

            // @TODO just for the moment
            this.scope.$broadcast('clear-registry');
        };
        RegistryCtrl.$inject = [
            '$scope',
            '$location',
            '$timeout',
            '$window',
            'configService',
            'registryService',
            'userService',
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

    // @TODO introduce error/offline handling later
    var MethodsCtrl = (function () {
        function MethodsCtrl($scope, $location, $timeout, $window, configService, methodsService, userService, $state, $modalInstance, id) {
            this.database = null;
            this.initialising = false;
            this.status = '';
            this.showError = false;
            this.request = {};
            // helpers for methods modal
            this.dropdownStatus = {
                isopen: false,
                active: 'Choose Method'
            };
            this.scope = $scope;
            $scope.methvm = this;
            this.location = $location;
            this.timeout = $timeout;
            this.window = $window;
            this.configService = configService;
            this.methodsService = methodsService;
            this.userService = userService;
            this.state = $state;
            this.modalInstance = $modalInstance;

            this.database = this.configService.getDatabase(id);

            if (this.methodsService.methods)
                this.methods = this.methodsService.getMethods(this.database);
else
                this.loadMethodsAPI();
        }
        MethodsCtrl.prototype.retry = function () {
            this.showError = false;
            this.loadMethodsAPI();
        };

        MethodsCtrl.prototype.loadMethodsAPI = function () {
            var _this = this;
            this.initialising = true;
            this.status = '';
            this.methodsService.getMethodsAPI().get(function (data, status) {
                return _this.handleAPIData(data, status);
            }, function (error) {
                return _this.handleAPIError(error);
            });
        };

        MethodsCtrl.prototype.handleAPIData = function (data, status) {
            this.initialising = false;
            this.status = 'success';

            // we always get the right thing
            this.methodsService.methods = data;
            this.methods = this.methodsService.getMethods(this.database);
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
        MethodsCtrl.prototype.handleServiceData = function (data, status) {
            console.log('success: ' + JSON.stringify(data.message));

            this.methodsService.loading = false;
            this.methodsService.status = 'success';

            // @TODO we change id creation later
            var id = this.userService.createId();

            if (data.message.hasOwnProperty('VOTABLE')) {
                //console.log(JSON.stringify(data.message.VOTABLE.RESOURCE[0].FIELD))
            }

            this.userService.user.results.push(new portal.Result(this.database.id, id, this.currentMethod.path, data));

            //refresh localStorage
            this.userService.localStorage.results = this.userService.user.results;

            this.scope.$broadcast('update-results', id);
        };

        MethodsCtrl.prototype.handleServiceError = function (error) {
            console.log('failure: ' + error.status);

            this.methodsService.loading = false;
            this.methodsService.showError = true;
            if (error.status == 404)
                this.methodsService.status = error.status + ' resource not found';
else {
                var response = error.data;
                this.methodsService.status = response.message;
            }
        };

        // method for submission
        MethodsCtrl.prototype.submitMethod = function () {
            var _this = this;
            console.log('submitted ' + this.currentMethod.path + ' ' + this.request['id']);

            this.methodsService.loading = true;
            this.methodsService.status = '';
            this.methodsService.showError = false;

            this.methodsService.requestMethod(this.currentMethod.path, this.request).get(function (data, status) {
                return _this.handleServiceData(data, status);
            }, function (error) {
                return _this.handleServiceError(error);
            });
        };

        // @TODO move this to directive later
        MethodsCtrl.prototype.setActive = function (method) {
            var _this = this;
            this.dropdownStatus.active = this.trimPath(method.path);
            this.currentMethod = method;

            // there is only one operation per method
            this.currentMethod.operations[0].parameters.forEach(function (p) {
                _this.request[p.name] = p.defaultValue;
            });

            if (this.currentMethod.operations[0].parameters.filter(function (e) {
                return e.name === 'id';
            }).length != 0) {
                // there is only one id param per method
                var param = this.currentMethod.operations[0].parameters.filter(function (e) {
                    return e.name === 'id';
                })[0];
                this.scope.$broadcast('set-applyable-elements', param.description);
            } else {
                // if there is no id, broadcast empty string
                this.scope.$broadcast('set-applyable-elements', '');
            }
        };

        MethodsCtrl.prototype.isActive = function (path) {
            return this.dropdownStatus.active === this.trimPath(path);
        };

        MethodsCtrl.prototype.getActive = function () {
            return this.dropdownStatus.active;
        };

        MethodsCtrl.prototype.trimPath = function (path) {
            var splitPath = path.split('/').reverse();
            return splitPath[0];
        };

        // methods for modal
        MethodsCtrl.prototype.saveMethods = function () {
            this.modalInstance.close();
            this.methodsService.showError = false;
        };

        MethodsCtrl.prototype.cancelMethods = function () {
            this.modalInstance.dismiss();
            this.methodsService.showError = false;
        };

        // method for applying a selection to the current method
        MethodsCtrl.prototype.applySelection = function (resourceId) {
            console.log("applySelection " + resourceId);
            this.request['id'] = resourceId;
        };
        MethodsCtrl.$inject = [
            '$scope',
            '$location',
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

    // @TODO introduce error/offline handling later
    var UserDataCtrl = (function () {
        function UserDataCtrl($scope, $location, $timeout, $window, userService, $state, $modalInstance) {
            this.initialising = false;
            this.showError = false;
            this.scope = $scope;
            $scope.datavm = this;
            this.location = $location;
            this.timeout = $timeout;
            this.window = $window;
            this.userService = userService;
            this.state = $state;
            this.modalInstance = $modalInstance;
        }
        // methods for modal
        UserDataCtrl.prototype.saveData = function () {
            this.modalInstance.close();
        };

        UserDataCtrl.prototype.cancelData = function () {
            this.modalInstance.dismiss();
        };
        UserDataCtrl.$inject = [
            '$scope',
            '$location',
            '$timeout',
            '$window',
            'userService',
            '$state',
            '$modalInstance'
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
            this.selectables = [];
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
                _this.selectables = _this.registryService.selectables[id];
                _this.repositoryId = id;
            });

            this.myScope.$on('registry-error', function (e, msg) {
                _this.showError = true;
                _this.status = msg;
            });

            this.myScope.$on('clear-registry-error', function (e) {
                _this.showError = false;
                _this.status = '';
            });

            this.myScope.$on('clear-registry', function (e) {
                console.log("clearing registry");
                _this.activeItems = {};
                _this.showError = false;
                _this.repositories = [];
                _this.simulationModels = [];
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
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
                _this.activeItems['SimulationRun'] = null;
                _this.activeItems['NumericalOutput'] = null;
                _this.activeItems['Granule'] = null;
                _this.showError = false;
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            this.myScope.$on('clear-numerical-outputs', function (e, element) {
                _this.setActive('SimulationRun', element);
                _this.activeItems['NumericalOutput'] = null;
                _this.activeItems['Granule'] = null;
                _this.showError = false;
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            this.myScope.$on('clear-granules', function (e, element) {
                _this.setActive('NumericalOutput', element);
                _this.activeItems['Granule'] = null;
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

        RegistryDir.prototype.isSelectable = function (type) {
            return this.selectables.indexOf(type) != -1;
        };

        // @FIXME we should allow multiple selections here!
        RegistryDir.prototype.setActive = function (type, element) {
            this.activeItems[type] = element;
        };

        // @FIXME we should allow multiple selections here!
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

        // @FIXME we should allow multiple selections here!
        RegistryDir.prototype.saveSelection = function (type) {
            // @TODO we change id creation later
            var id = this.userService.createId();

            this.userService.user.selections.push(new portal.Selection(this.repositoryId, id, type, this.activeItems[type]));

            // refresh localStorage
            this.userService.localStorage.selections = this.userService.user.selections;

            this.myScope.$broadcast('update-selections', id);
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
        function UserDataDir(userService, $state) {
            var _this = this;
            this.repositoryId = null;
            this.isCollapsed = {};
            // current resource selection which is fully displayed
            this.currentSelection = [];
            // currently applyable elements (according to current method)
            this.applyableElements = [];
            this.isApplyable = false;
            this.userService = userService;
            this.stateService = $state;
            this.templateUrl = '/public/partials/templates/userdataDir.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        UserDataDir.prototype.injection = function () {
            return [
                'userService',
                '$state',
                function (userService, $state) {
                    return new UserDataDir(userService, $state);
                }
            ];
        };

        UserDataDir.prototype.linkFn = function ($scope, element, attributes) {
            var _this = this;
            $scope.userdirvm = this;
            this.myScope = $scope;
            this.user = this.userService.user;

            attributes.$observe('db', function (id) {
                _this.repositoryId = id;
            });

            if (this.user.selections) {
                this.user.selections.map(function (e) {
                    _this.isCollapsed[e.id] = true;
                });
            }

            if (this.user.results) {
                this.user.results.map(function (e) {
                    _this.isCollapsed[e.id] = true;
                });
            }

            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-elements', function (e, elements) {
                _this.applyableElements = elements.split(',');
                _this.isApplyable = false;
                if (_this.currentSelection.length == 1) {
                    _this.applyableElements.forEach(function (e) {
                        console.log("Element " + e);
                        if (_this.currentSelection[0].type === e)
                            _this.isApplyable = true;
                    });
                }
            });

            this.myScope.$on('update-selections', function (e, id) {
                // reset expanded selection
                _this.currentSelection = [];
                _this.isCollapsed[id] = false;

                for (var rId in _this.isCollapsed) {
                    if (rId != id)
                        _this.isCollapsed[rId] = true;
                }
                _this.currentSelection.push(_this.user.selections.filter(function (e) {
                    return e.id == id;
                })[0]);
            });

            this.myScope.$on('update-results', function (e, id) {
                _this.isCollapsed[id] = false;

                for (var rId in _this.isCollapsed) {
                    if (rId != id)
                        _this.isCollapsed[rId] = true;
                }

                // reset expanded selection
                _this.currentSelection = [];
            });

            // watch event when all content is loading into the dir
            this.myScope.$watch('$includeContentLoaded', function (e) {
                for (var id in _this.isCollapsed)
                    _this.isCollapsed[id] = true;

                // reset expanded selections
                _this.currentSelection = [];

                // reset also applyables
                _this.applyableElements = [];
                _this.isApplyable = false;
            });
        };

        UserDataDir.prototype.toggleResultDetails = function (id) {
            // reset expanded selection
            this.currentSelection = [];
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

        UserDataDir.prototype.toggleSelectionDetails = function (id) {
            // reset expanded selection
            this.currentSelection = [];
            if (this.isCollapsed[id]) {
                this.isCollapsed[id] = false;

                for (var rId in this.isCollapsed) {
                    if (rId != id)
                        this.isCollapsed[rId] = true;
                }

                var selection = this.user.selections.filter(function (e) {
                    return e.id == id;
                })[0];
                if (this.applyableElements.indexOf(selection.type) != -1) {
                    this.isApplyable = true;
                    console.log("isApplyable");
                }
                this.currentSelection.push(selection);
            } else {
                this.isCollapsed[id] = true;

                // set isApplyable to false
                this.isApplyable = false;
            }
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
            this.currentSelection = [];

            // delete collapsed info
            delete this.isCollapsed[id];
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

    var impexPortal = angular.module('portal', ['ui.bootstrap', 'ui.router', 'ngResource', 'ngStorage']);

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
