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
        function Tool(name, description, dns, info) {
            this.name = name;
            this.description = description;
            this.dns = dns;
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

    var InputPopulation = (function () {
        function InputPopulation(name, set, parameterKey, description, caveats, simulatedRegion, processType, units, unitsConversion, processCoefficient, processCoeffType, processModel, modelUrl) {
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
        return InputPopulation;
    })();
    portal.InputPopulation = InputPopulation;

    var InputProcess = (function () {
        function InputProcess(name, set, parameterKey, description, caveats, simulatedRegion, qualifier, particleType, chemicalFormula, atomicNumber, populationMassNumber, populationChargeState, populationDensity, populationTemperature, populationFlowSpeed, distribution, productionRate, totalProductionRate, inputTableURL, profile, modelUrl) {
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
        return InputProcess;
    })();
    portal.InputProcess = InputProcess;

    // numerical output element
    var NumericalOutput = (function () {
        function NumericalOutput(resourceId, resourceHeader, accessInformation, measurementType, simulatedRegion, inputResourceId, parameter, spatialDescription, temporalDescription) {
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
    })();
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

    var ConfigService = (function () {
        function ConfigService($resource) {
            this.url = '/';
            this.config = null;
            // creates an action descriptor
            this.configAction = {
                method: 'GET',
                params: {
                    fmt: '@fmt'
                },
                isArray: false
            };
            this.resource = $resource;
        }
        // returns the resource handler
        ConfigService.prototype.getConfig = function () {
            return this.resource(this.url + 'config?', { fmt: '@fmt' }, { getConfig: this.configAction });
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
                params: {
                    id: '@id',
                    fmt: '@fmt'
                },
                isArray: false
            };
            // cache for the elements (identified by request id)
            this.cachedElements = {};
            this.resource = $resource;
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

    var ConfigCtrl = (function () {
        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        function ConfigCtrl($scope, $http, $location, $window, configService) {
            this.showError = false;
            $scope.vm = this;
            this.configService = configService;
            this.location = $location;
            this.http = $http;
            this.window = $window;

            this.load();
        }
        ConfigCtrl.prototype.retry = function () {
            this.showError = false;
            this.load();
        };

        ConfigCtrl.prototype.load = function () {
            var _this = this;
            this.configService.getConfig().get({ fmt: 'json' }, function (data, status) {
                return _this.handleData(data, status);
            }, function (data, status) {
                return _this.handleError(data, status);
            });
        };

        ConfigCtrl.prototype.handleData = function (data, status) {
            this.status = "success";
            this.configService.config = data.impexconfiguration;
            if (this.configService.config)
                this.location.path('/portal');
else
                this.handleError(data, status);
        };

        ConfigCtrl.prototype.handleError = function (data, status) {
            console.log("config error");
            if (this.window.confirm('connection timed out. retry?'))
                this.load();
else {
                this.showError = true;
                this.status = data + " " + status;
            }
        };
        ConfigCtrl.$inject = ['$scope', '$http', '$location', '$window', 'configService'];
        return ConfigCtrl;
    })();
    portal.ConfigCtrl = ConfigCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var PortalCtrl = (function () {
        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        function PortalCtrl($scope, $http, $location, $timeout, $interval, $window, configService, registryService, $modal) {
            var _this = this;
            this.configAble = true;
            this.ready = false;
            this.loading = false;
            this.initialising = false;
            this.transFinished = true;
            this.scope = $scope;
            this.scope.vm = this;
            this.http = $http;
            this.location = $location;
            this.configService = configService;
            this.config = this.configService.config;
            this.registryService = registryService;
            this.timeout = $timeout;
            this.interval = $interval;
            this.window = $window;
            this.modal = $modal;

            if (this.config == null) {
                $location.path('/config');
            } else {
                this.timeout(function () {
                    _this.ready = true;
                });
            }
        }
        PortalCtrl.prototype.getRepository = function (id) {
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
                console.log("Not-Cached=" + cacheId);
                this.registryPromise = this.registryService.getRepository().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (res) {
                    var result = res.spase;
                    _this.registryService.cachedElements[cacheId] = result.resources.map(function (r) {
                        return r.repository;
                    });
                    _this.scope.$broadcast('update-repositories', cacheId);
                    _this.initialising = false;
                });
            } else {
                console.log("Cached=" + cacheId);
                this.scope.$broadcast('update-repositories', cacheId);
                this.initialising = false;
            }
        };

        PortalCtrl.prototype.getSimulationModel = function (id) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-models');
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            var cacheId = "model-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                console.log("Not-Cached=" + cacheId);
                this.registryPromise = this.registryService.getSimulationModel().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (res) {
                    var result = res.spase;
                    _this.registryService.cachedElements[cacheId] = result.resources.map(function (r) {
                        return r.simulationModel;
                    });
                    _this.scope.$broadcast('update-simulation-models', cacheId);
                    _this.loading = false;
                });
            } else {
                console.log("Cached=" + cacheId);
                this.scope.$broadcast('update-simulation-models', cacheId);
                this.loading = false;
            }
        };

        PortalCtrl.prototype.getSimulationRun = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-simulation-runs', element);
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            var cacheId = "run-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                console.log("Not-Cached=" + cacheId);
                this.registryPromise = this.registryService.getSimulationRun().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (res) {
                    var result = res.spase;
                    _this.registryService.cachedElements[cacheId] = result.resources.map(function (r) {
                        return r.simulationRun;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-simulation-runs', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no simulation run');
                    _this.loading = false;
                });
            } else {
                console.log("Cached=" + cacheId);
                this.scope.$broadcast('update-simulation-runs', cacheId);
                this.loading = false;
            }
        };

        PortalCtrl.prototype.getNumericalOutput = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-numerical-outputs', element);
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            var cacheId = "output-" + id;
            if (!(cacheId in this.registryService.cachedElements)) {
                console.log("Not-Cached=" + cacheId);
                this.registryPromise = this.registryService.getNumericalOutput().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (res) {
                    var result = res.spase;
                    _this.registryService.cachedElements[cacheId] = result.resources.map(function (r) {
                        return r.numericalOutput;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-numerical-outputs', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no numerical output');
                    _this.loading = false;
                });
            } else {
                console.log("Cached=" + cacheId);
                this.scope.$broadcast('update-numerical-outputs', cacheId);
                this.loading = false;
            }
        };

        PortalCtrl.prototype.getGranule = function (id, element, $event) {
            var _this = this;
            this.scope.$broadcast('clear-granules', element);
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);
            var cacheId = "granule-" + id;

            if (!(cacheId in this.registryService.cachedElements)) {
                console.log("Not-Cached=" + cacheId);
                this.registryPromise = this.registryService.getGranule().get({ fmt: 'json', id: id }).$promise;

                this.registryPromise.then(function (res) {
                    var result = res.spase;
                    console.log("granule=" + result.resources[0].granule.resourceId.split('/').reverse()[0]);
                    _this.registryService.cachedElements[cacheId] = result.resources.map(function (r) {
                        return r.granule;
                    });
                    _this.loading = false;
                    _this.scope.$broadcast('update-granules', cacheId);
                }, function (err) {
                    _this.scope.$broadcast('registry-error', 'no granule');
                    _this.loading = false;
                });
            } else {
                console.log("Cached=" + cacheId);
                this.scope.$broadcast('update-granules', cacheId);
                this.loading = false;
            }
        };

        PortalCtrl.prototype.open = function (size) {
            var modalInstance = this.modal.open({
                templateUrl: '/public/partials/templates/modal.html',
                controller: portal.ModalCtrl,
                size: size
            });

            modalInstance.result.then(function (ok) {
                return console.log(ok);
            }, function (cancel) {
                return console.log(cancel);
            });
        };
        PortalCtrl.$inject = [
            '$scope',
            '$http',
            '$location',
            '$timeout',
            '$interval',
            '$window',
            'configService',
            'registryService',
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

    var ModalCtrl = (function () {
        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        function ModalCtrl($scope, $http, $location, $window, configService, $modalInstance) {
            this.showError = false;
            $scope.modvm = this;
            this.configService = configService;
            this.location = $location;
            this.http = $http;
            this.window = $window;
            this.modalInstance = $modalInstance;
        }
        ModalCtrl.prototype.ok = function () {
            this.modalInstance.close();
        };

        ModalCtrl.prototype.cancel = function () {
            this.modalInstance.dismiss();
        };
        ModalCtrl.$inject = ['$scope', '$http', '$location', '$window', 'configService', '$modalInstance'];
        return ModalCtrl;
    })();
    portal.ModalCtrl = ModalCtrl;
})(portal || (portal = {}));
/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var DatabasesDir = (function () {
        function DatabasesDir($timeout, configService) {
            var _this = this;
            this.configService = configService;
            this.timeout = $timeout;
            this.templateUrl = '/public/partials/templates/databases.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        DatabasesDir.prototype.injection = function () {
            return [
                '$timeout',
                'configService',
                function ($timeout, configService) {
                    return new DatabasesDir($timeout, configService);
                }
            ];
        };

        DatabasesDir.prototype.linkFn = function ($scope, element, attributes) {
            $scope.databasesvm = this;
            this.myScope = $scope;
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
        function RegistryDir($timeout, configService, registryService) {
            var _this = this;
            this.oneAtATime = true;
            this.error = false;
            this.errorMessage = "no resources found";
            // container for intermediate results
            this.repositories = [];
            this.simulationModels = [];
            this.simulationRuns = [];
            this.numericalOutputs = [];
            this.granules = [];
            this.activeItems = {};
            this.configService = configService;
            this.registryService = registryService;
            this.timeout = $timeout;
            this.templateUrl = '/public/partials/templates/registry.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
        // @TODO here we add dependencies for the used elements in bootstrap-ui
        RegistryDir.prototype.injection = function () {
            return [
                '$timeout',
                'configService',
                'registryService',
                function ($timeout, configService, registryService) {
                    return new RegistryDir($timeout, configService, registryService);
                }
            ];
        };

        RegistryDir.prototype.setActive = function (type, element) {
            this.activeItems[type] = element;
        };

        RegistryDir.prototype.isActive = function (type, element) {
            return this.activeItems[type] === element;
        };

        RegistryDir.prototype.linkFn = function ($scope, element, attributes) {
            var _this = this;
            $scope.regvm = this;
            this.myScope = $scope;

            $scope.$on('registry-error', function (e, msg) {
                _this.error = true;
                _this.errorMessage = msg;
            });

            $scope.$on('clear-registry', function (e) {
                console.log("clearing registry");
                _this.activeItems = {};
                _this.error = false;
                _this.repositories = [];
                _this.simulationModels = [];
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            $scope.$on('clear-simulation-models', function (e) {
                _this.error = false;
                _this.simulationModels = [];
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            $scope.$on('clear-simulation-runs', function (e, element) {
                _this.setActive("model", element);
                _this.error = false;
                _this.simulationRuns = [];
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            $scope.$on('clear-numerical-outputs', function (e, element) {
                _this.setActive("run", element);
                _this.error = false;
                _this.numericalOutputs = [];
                _this.granules = [];
            });

            $scope.$on('clear-granules', function (e, element) {
                _this.setActive("output", element);
                _this.error = false;
                _this.granules = [];
            });

            $scope.$on('update-repositories', function (e, id) {
                _this.repositories = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            $scope.$on('update-simulation-models', function (e, id) {
                _this.simulationModels = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            $scope.$on('update-simulation-runs', function (e, id) {
                _this.simulationRuns = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            $scope.$on('update-numerical-outputs', function (e, id) {
                _this.numericalOutputs = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });

            $scope.$on('update-granules', function (e, id) {
                _this.granules = _this.registryService.cachedElements[id].map(function (r) {
                    return r;
                });
            });
        };
        return RegistryDir;
    })();
    portal.RegistryDir = RegistryDir;
})(portal || (portal = {}));
/// <reference path='_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var impexPortal = angular.module('portal', ['ui.bootstrap', 'ngRoute', 'ngResource']);

    // here we may add options for bootstrap-ui
    // maybe include angular.ui.router
    //(see infoday examples, it supports application state, very nice"!)
    impexPortal.service('configService', portal.ConfigService);
    impexPortal.service('registryService', portal.RegistryService);

    impexPortal.controller('configCtrl', portal.ConfigCtrl);
    impexPortal.controller('portalCtrl', portal.PortalCtrl);
    impexPortal.controller('modalCtrl', portal.ModalCtrl);

    impexPortal.directive('databasesDir', portal.DatabasesDir.prototype.injection());
    impexPortal.directive('registryDir', portal.RegistryDir.prototype.injection());

    impexPortal.config([
        '$routeProvider',
        function ($routeProvider) {
            $routeProvider.when('/config', { templateUrl: '/public/partials/config.html', controller: 'configCtrl' }).when('/portal', { templateUrl: '/public/partials/portal.html', controller: 'portalCtrl' }).otherwise({ redirectTo: '/config' });
        }
    ]);

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
})(portal || (portal = {}));
