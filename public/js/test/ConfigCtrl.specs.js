'use strict'

describe('ConfigCtrl', function() {
	var path= '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
	
	var scope, interval, cService, uService, state, cfg, uData, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $interval, configService, 
			userService, $state, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		interval = $interval
		cService = configService;
		uService = userService;
		state = $state;
		$httpBackend = _$httpBackend_
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		cfg = getJSONFixture('config.json');
		uData = getJSONFixture('userData.json');
		$httpBackend.when('GET', '/methods/FMI/isAlive').respond(true);
		$httpBackend.when('GET', '/methods/LATMOS/isAlive').respond(true);
		$httpBackend.when('GET', '/methods/SINP/isAlive').respond(true);
		$httpBackend.when('GET', '/config?&fmt=json').respond(cfg);
		$httpBackend.when('GET', '/userdata').respond(uData);
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');
	
		$controller('configCtrl', {$scope: scope, $interval: interval, configService: cService, 
			userService: uService, $state: state, config: cfg, userData: uData});
	}));
	
	it('should inject services', function(){
		expect(scope.vm.interval).toBeDefined();
		expect(scope.vm.configService).toBeDefined();
		expect(scope.vm.userService).toBeDefined();
		expect(scope.vm.state).toBeDefined();
	});
	
	it('should resolve config', function(){
		$httpBackend.flush();
		expect(scope.vm.configService.config).toBeDefined();
		expect(scope.vm.configService.config).toEqual(cfg);
	});
	
	it('should resolve session userdata', function(){
		$httpBackend.flush();
		expect(scope.vm.userService.user.voTables).toBeDefined();
		expect(scope.vm.userService.user.voTables).toEqual(uData);
	});
	
	it('should initialise localStorage', function(){
		expect(scope.vm.userService.localStorage).toBeDefined();
		expect(scope.vm.userService.localStorage.results).toBeDefined();
		expect(scope.vm.userService.localStorage.selections).toBeDefined();
	})
	
	it('should create temporary user', function(){
		expect(scope.vm.userService.user).toBeDefined();
		expect(scope.vm.userService.user.id).toBeDefined();
		expect(scope.vm.userService.user.results).toBeDefined();
		expect(scope.vm.userService.user.selections).toBeDefined();
	});
	
	it('should initialise isAlive map', function(){
		$httpBackend.flush();
		scope.vm.interval.flush();
		expect(scope.vm.configService.aliveMap).toBeDefined()
		for(var i = 0; i < cfg.databases.length; i++) {
			// only simulations are used atm
			if(cfg.databases[i].type == 'simulation')
				expect(scope.vm.configService.aliveMap[cfg.databases[i].name]).toBeTruthy()
			else
				expect(scope.vm.configService.aliveMap[cfg.databases[i].name]).toBeUndefined()
		}
	});
	

});






