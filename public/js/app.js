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

    // @TODO finish this object (its a bit tricky)
    var InputPopulation = (function () {
        function InputPopulation(name) {
            this.name = name;
        }
        return InputPopulation;
    })();
    portal.InputPopulation = InputPopulation;

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
    // @TODO needs to be extended!
    var Granule = (function (_super) {
        __extends(Granule, _super);
        function Granule(resourceId) {
            _super.call(this, resourceId);
            this.resourceId = resourceId;
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
            // @TODO we will store here a map of loaded resources
            // we need do design an appropriate structure for that
            // then we can make a cache out of it
            this.repositories = [];
            this.simulationModels = [];
            this.simulationRuns = [];
            this.resource = $resource;
        }
        RegistryService.prototype.getRepository = function () {
            return this.resource(this.url + 'registry/repository?', { id: '@id', fmt: '@fmt' }, { getRepository: this.registryAction });
        };

        // @TODO we need to be able to provide an r (recursive) param
        RegistryService.prototype.getSimulationModel = function () {
            return this.resource(this.url + 'registry/simulationmodel?', { id: '@id', fmt: '@fmt' }, { getSimulationModel: this.registryAction });
        };

        // @TODO we need to be able to provide an r (recursive) param
        RegistryService.prototype.getSimulationRun = function () {
            return this.resource(this.url + 'registry/simulationrun?', { id: '@id', fmt: '@fmt' }, { getSimulationRun: this.registryAction });
        };

        // @TODO we need to be able to provide an r (recursive) param
        RegistryService.prototype.getNumericalOutput = function () {
            return this.resource(this.url + 'registry/numericaloutput?', { id: '@id', fmt: '@fmt' }, { getNumericalOutput: this.registryAction });
        };

        // @TODO we need to be able to provide an r (recursive) param
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
        ConfigCtrl.$inject = ['$scope', '$http', '$location', '$window', 'configService', '$timeout'];
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
        function PortalCtrl($scope, $http, $location, $timeout, $interval, $window, configService, registryService) {
            var _this = this;
            this.configAble = true;
            this.ready = false;
            this.loading = false;
            this.transFinished = true;
            this.oneAtATime = true;
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
            // @FIXME improve this
            this.registryService.repositories = [];
            this.registryService.simulationModels = [];
            this.registryService.simulationRuns = [];
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            // @FIXME dont know if this is the optimal way to do it
            this.registryPromiseRepo = this.registryService.getRepository().get({ fmt: 'json', id: id }).$promise;
            this.registryPromiseRepo.then(function (res) {
                var result = res.spase;

                // @TODO here we should save the thing in a map
                result.resources.forEach(function (res) {
                    console.log("repository=" + res.repository.resourceId);
                    _this.registryService.repositories.push(res.repository);
                });
                _this.loading = false;
            });
        };

        PortalCtrl.prototype.getSimulationModel = function (id) {
            var _this = this;
            // @FIXME improve this
            this.registryService.simulationModels = [];
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            // @FIXME dont know if this is the optimal way to do it
            this.registryPromiseModel = this.registryService.getSimulationModel().get({ fmt: 'json', id: id }).$promise;
            this.registryPromiseModel.then(function (res) {
                var result = res.spase;
                result.resources.forEach(function (res) {
                    console.log("model=" + res.simulationModel.resourceId);
                    _this.registryService.simulationModels.push(res.simulationModel);
                });
                _this.loading = false;
            });
        };

        PortalCtrl.prototype.getSimulationRun = function (id) {
            var _this = this;
            // @FIXME improve this
            this.registryService.simulationRuns = [];
            this.loading = true;
            this.transFinished = false;

            // aligned with standard transition time of accordion
            this.timeout(function () {
                _this.transFinished = true;
            }, 200);

            // @FIXME dont know if this is the optimal way to do it
            this.registryPromiseModel = this.registryService.getSimulationRun().get({ fmt: 'json', id: id }).$promise;
            this.registryPromiseRun = this.registryService.getSimulationRun().get({ fmt: 'json', id: id }).$promise;
            this.registryPromiseRun.then(function (res) {
                var result = res.spase;
                result.resources.forEach(function (res) {
                    if (res.simulationRun.simulationDomain.boundaryConditions)
                        console.log("run=" + res.simulationRun.resourceId + " " + res.simulationRun.simulationDomain.boundaryConditions.fieldBoundary.frontWall);
                    _this.registryService.simulationRuns.push(res.simulationRun);
                });
                _this.loading = false;
            });
        };

        // testing of angular resources
        PortalCtrl.prototype.doSomething = function () {
            /*
            this.registryPromiseOutput = this.registryService.getNumericalOutput().get(
            { fmt: 'json', id: 'impex://LATMOS/Hybrid/Gany_24_10_13' }).$promise
            this.registryPromiseOutput.then((res) => {
            var result = <ISpase>res.spase
            result.resources.forEach((res) => {
            console.log("outputId="+res.numericalOutput.resourceId)
            
            if(res.numericalOutput.spatialDescription)
            console.log("outputS="+
            res.numericalOutput.spatialDescription.coordinateSystem.coordinateRepresentation)
            
            if(res.numericalOutput.temporalDescription)
            console.log("outputT="+res.numericalOutput.temporalDescription.timespan.startDate)
            })
            }) */
        };
        PortalCtrl.$inject = [
            '$scope',
            '$http',
            '$location',
            '$timeout',
            '$interval',
            '$window',
            'configService',
            'registryService'
        ];
        return PortalCtrl;
    })();
    portal.PortalCtrl = PortalCtrl;
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
            this.configService = configService;
            this.registryService = registryService;
            this.timeout = $timeout;
            this.templateUrl = '/public/partials/templates/registry.html';
            this.restrict = 'E';
            this.link = function ($scope, element, attributes) {
                return _this.linkFn($scope, element, attributes);
            };
        }
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

        RegistryDir.prototype.linkFn = function ($scope, element, attributes) {
            $scope.registryvm = this;
            this.myScope = $scope;
        };
        return RegistryDir;
    })();
    portal.RegistryDir = RegistryDir;
})(portal || (portal = {}));
/// <reference path='_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var impexPortal = angular.module('portal', [, 'ui.bootstrap', 'ngRoute', 'ngResource']);

    impexPortal.service('configService', portal.ConfigService);
    impexPortal.service('registryService', portal.RegistryService);

    impexPortal.controller('configCtrl', portal.ConfigCtrl);
    impexPortal.controller('portalCtrl', portal.PortalCtrl);

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
