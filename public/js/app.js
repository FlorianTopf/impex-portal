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
var portal;
(function (portal) {
    'use strict';

    var Contact = (function () {
        function Contact(personId, role) {
            this.personId = personId;
            this.role = role;
        }
        return Contact;
    })();
    portal.Contact = Contact;

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

    var Repository = (function () {
        function Repository(resourceId, resourceHeader, accessUrl) {
            this.resourceId = resourceId;
            this.resourceHeader = resourceHeader;
            this.accessUrl = accessUrl;
        }
        return Repository;
    })();
    portal.Repository = Repository;
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
            // we will store here a map of loaded resources
            // we need do design an appropriate structure for that
            // action descriptor for registry actions
            this.registryAction = {
                method: 'GET',
                params: {
                    fmt: '@fmt'
                },
                isArray: false
            };
            this.resource = $resource;
        }
        RegistryService.prototype.getRepository = function () {
            return this.resource(this.url + 'registry/repository?', { fmt: '@fmt' }, { getRepository: this.registryAction });
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
            this.num = 1;
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

            this.treedata = this.createSubTree(7);

            // PLAYING AROUND WITH ANGULAR RESOURCES
            // don know if this is the optimal way to do it
            this.registryPromise = this.registryService.getRepository().get({ fmt: 'json' }).$promise;
            this.registryPromise.then(function (res) {
                var result = res.spase;
                console.log(result.resources);
            });

            if (this.config == null) {
                $location.path('/config');
            } else {
                this.timeout(function () {
                    _this.ready = true;
                });
            }
        }
        PortalCtrl.prototype.doSomething = function () {
        };

        PortalCtrl.prototype.getNum = function () {
            return this.num++;
        };

        PortalCtrl.prototype.createSubTree = function (level) {
            if (level > 0)
                return [
                    { "label": "Node " + this.getNum(), "id": "id", "children": this.createSubTree(level - 1) },
                    { "label": "Node " + this.getNum(), "id": "id", "children": this.createSubTree(level - 1) },
                    { "label": "Node " + this.getNum(), "id": "id", "children": this.createSubTree(level - 1) },
                    { "label": "Node " + this.getNum(), "id": "id", "children": this.createSubTree(level - 1) }
                ];
else
                return [];
        };

        PortalCtrl.prototype.showSelected = function (sel) {
            this.selected = sel.label;
        };

        PortalCtrl.prototype.addRoot = function () {
            this.treedata.push({ "label": "New Node " + this.getNum(), "id": "id", "children": [] });
        };

        PortalCtrl.prototype.addChild = function () {
            this.treedata[0].children.push({ "label": "New Node " + this.getNum(), "id": "id", "children": [] });
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
/// <reference path='_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var impexPortal = angular.module('portal', ['treecontrol', 'ngRoute', 'ngResource']);

    impexPortal.service('configService', portal.ConfigService);

    // testing resources
    impexPortal.service('registryService', portal.RegistryService);

    impexPortal.controller('configCtrl', portal.ConfigCtrl);
    impexPortal.controller('portalCtrl', portal.PortalCtrl);

    impexPortal.directive('databasesDir', portal.DatabasesDir.prototype.injection());

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
