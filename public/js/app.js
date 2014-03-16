/// <reference path='../_all.ts' />
var portal;
(function (portal) {
    'use strict';

    var Config = (function () {
        function Config(databases, tools) {
            this.databases = databases;
            this.tools = tools;
        }
        return Config;
    })();
    portal.Config = Config;

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

    var ConfigService = (function () {
        function ConfigService() {
            this.config = null;
            this.url = '/';
        }
        ConfigService.prototype.getConfigUrl = function () {
            return this.url + 'config?fmt=json';
        };
        return ConfigService;
    })();
    portal.ConfigService = ConfigService;
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
            this.http.get(this.configService.getConfigUrl(), { timeout: 5000 }).success(function (data, status) {
                return _this.handleData(data, status);
            }).error(function (data, status) {
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
        function PortalCtrl($scope, $http, $location, $timeout, $interval, $window, configService) {
            var _this = this;
            this.configAble = true;
            this.ready = false;
            this.scope = $scope;
            this.scope.vm = this;
            this.http = $http;
            this.location = $location;
            this.configService = configService;
            this.config = this.configService.config;
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
        PortalCtrl.prototype.doSomething = function () {
        };
        PortalCtrl.$inject = [
            '$scope',
            '$http',
            '$location',
            '$timeout',
            '$interval',
            '$window',
            'configService'
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

    var impexPortal = angular.module('portal', ['ngRoute']);

    impexPortal.service('configService', portal.ConfigService);

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
