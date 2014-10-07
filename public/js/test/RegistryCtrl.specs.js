'use strict'

describe('RegistryCtrl on Simulations', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, timeout, cService, rService, state, modalInstance, db, 
		repos, models, runs, outputs, granuleInput, granule, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $timeout, configService, 
			registryService, $state, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		timeout = $timeout;
		cService = configService;
		rService = registryService;
		state = $state;
		// Create a mock object using spies
	    modalInstance = {                 
	    	close: jasmine.createSpy('modalInstance.close'),
	    	dismiss: jasmine.createSpy('modalInstance.dismiss'),
	    	result: {
	    	   then: jasmine.createSpy('modalInstance.result.then')
	    	}
	    };
		$httpBackend = _$httpBackend_;
		// spying on broadcast events
        spyOn(scope, '$broadcast');
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		//just mocking the configService getDatabase method
		db = getJSONFixture('simDatabase.json');
		cService.getDatabase = function(id){ return db; }
		repos = getJSONFixture('simRepository.json');
		models = getJSONFixture('simulationmodel.json');
		runs = getJSONFixture('simulationrun.json');
		outputs = getJSONFixture('numericaloutput.json');
		granuleInput = getJSONFixture('granule-input.json');
		granule = getJSONFixture('granule.json');

		$httpBackend.when('GET', '/config?fmt=json').respond(getJSONFixture('config.json'));
		$httpBackend.when('GET', '/userdata').respond(getJSONFixture('userData.json'));
		$httpBackend.when('GET', '/filter/region').respond(getJSONFixture('regions.json'));
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');
		
		$httpBackend.when('GET', 
				'/registry/repository?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FFMI%2FHYB').respond(repos)
		$httpBackend.when('GET', 
				'/registry/simulationmodel?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FFMI%2FHYB').respond(models)
		$httpBackend.when('GET', 
				'/registry/simulationrun?fmt=json&id=spase:%2F%2FIMPEX%2FSimulationModel%2FFMI%2FHYB').respond(runs)	
		$httpBackend.when('GET', 
				'/registry/numericaloutput?fmt=json&id=spase:%2F%2FIMPEX%2FSimulationRun%2FFMI%2FHYB%2Fvenus%2Fvenus_nominal_lowres_cx_20121120').respond(outputs)
		$httpBackend.when('GET', '/registry/granule?fmt=json&id=spase:%2F%2FIMPEX%2FNumericalOutput%2FLATMOS%2FHybrid%2FMerc_02_10_13%2FHes%2F3D').respond(granule)
		
		$controller('registryCtrl', {$scope: scope, $timeout: timeout, configService: cService, 
			registryService: rService, $state: state, $modalInstance: modalInstance, id: db.id});
	}));
	
	it('should inject services', function(){
		expect(scope.regvm.timeout).toBeDefined();
		expect(scope.regvm.configService).toBeDefined();
		expect(scope.regvm.registryService).toBeDefined();
		expect(scope.regvm.state).toBeDefined();
		expect(scope.regvm.modalInstance).toBeDefined();
	});
	
	it('should init tooltip', function(){
		expect(scope.regvm.registryTooltip).toBeDefined();
	});
	
	it('should init status values', function(){
		expect(scope.regvm.database).toBeDefined();
        expect(scope.regvm.isFirstOpen).toBeTruthy();
        expect(scope.regvm.initialising).toBeFalsy();
        expect(scope.regvm.loading).toBeFalsy();
	});

	it('should init database object', function(){
		$httpBackend.flush();
		expect(scope.regvm.database).toEqual(db);
	});
	
	it('should init repositories', function(){
		$httpBackend.flush();
		var cacheId = "repo-"+db.id;
		expect(scope.regvm.initialising).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = repos.resources.map(function(r){ return r.repository; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-repositories', cacheId);
	});
	
	it('should init simulationmodels', function(){
		$httpBackend.flush();
		var cacheId = "model-"+db.id;
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = models.resources.map(function(r){ return r.simulationModel; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-simulation-models');
		expect(scope.$broadcast).toHaveBeenCalledWith('update-simulation-models', cacheId);
	});
	
	it('should load simulationruns', function(){
		var elems = models.resources.map(function(r){ return r.simulationModel; })
		var cacheId = "run-"+elems[0].resourceId;
		scope.regvm.getSimulationRun(elems[0]);
		$httpBackend.flush();
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = runs.resources.map(function(r){ return r.simulationRun; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-simulation-runs', elems[0]);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-simulation-runs', cacheId);
	});
	
	it('should load numericaloutputs', function(){
		var elems = runs.resources.map(function(r){ return r.simulationRun; })
		var cacheId = "output-"+elems[0].resourceId;
		scope.regvm.getNumericalOutput(elems[0])
		$httpBackend.flush();
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = outputs.resources.map(function(r){ return r.numericalOutput; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-numerical-outputs', elems[0]);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-numerical-outputs', cacheId);
	});	
	
	it('should load granules', function(){
		var elem = granuleInput.resources[0].numericalOutput
		var cacheId = "granule-"+elem.resourceId;
		scope.regvm.getGranule(elem)
		$httpBackend.flush();
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = [granule.resources[0].granule];
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-granules', elem);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-granules', cacheId);
	});
	
	it('should close or dismiss the modal', function () {
	    scope.regvm.saveRegistry(true);
	    expect(modalInstance.close).toHaveBeenCalled();
	    scope.regvm.saveRegistry(false);
	    expect(modalInstance.dismiss).toHaveBeenCalled();
	});
	
	
}); 


describe('RegistryCtrl on Observations', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, timeout, cService, rService, state, modalInstance, db, 
		repos, obs, instr, data, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $timeout, configService, 
			registryService, $state, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		timeout = $timeout;
		cService = configService;
		rService = registryService;
		state = $state;
		// Create a mock object using spies
	    modalInstance = {                 
	    	close: jasmine.createSpy('modalInstance.close'),
	    	dismiss: jasmine.createSpy('modalInstance.dismiss'),
	    	result: {
	    	   then: jasmine.createSpy('modalInstance.result.then')
	    	}
	    };
		$httpBackend = _$httpBackend_;
		// spying on broadcast events
        spyOn(scope, '$broadcast');
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		//just mocking the configService getDatabase method
		db = getJSONFixture('obsDatabase.json');
		cService.getDatabase = function(id){ return db; }
		repos = getJSONFixture('obsRepository.json');
		obs = getJSONFixture('observatory.json');
		instr = getJSONFixture('instrument.json');
		data = getJSONFixture('numericaldata.json');

		$httpBackend.when('GET', '/config?fmt=json').respond(getJSONFixture('config.json'));
		$httpBackend.when('GET', '/userdata').respond(getJSONFixture('userData.json'));
		$httpBackend.when('GET', '/filter/region').respond(getJSONFixture('regions.json'));
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');
		
		$httpBackend.when('GET', 
				'/registry/repository?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FAMDA').respond(repos);
		$httpBackend.when('GET', 
				'/registry/observatory?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FAMDA').respond(obs);
		$httpBackend.when('GET',
				'/registry/instrument?fmt=json&id=spase:%2F%2FIMPEX%2FObservatory%2FAMDA%2FACE').respond(instr);
		$httpBackend.when('GET', 
				'/registry/numericaldata?fmt=json&id=spase:%2F%2FIMPEX%2FInstrument%2FAMDA%2FACE%2FEphemeris').respond(data)
		$controller('registryCtrl', {$scope: scope, $timeout: timeout, configService: cService, 
			registryService: rService, $state: state, $modalInstance: modalInstance, id: db.id});
	}));
	
	it('should inject services', function(){
		expect(scope.regvm.timeout).toBeDefined();
		expect(scope.regvm.configService).toBeDefined();
		expect(scope.regvm.registryService).toBeDefined();
		expect(scope.regvm.state).toBeDefined();
		expect(scope.regvm.modalInstance).toBeDefined();
	});
	
	it('should init tooltip', function(){
		expect(scope.regvm.registryTooltip).toBeDefined();
	});
	
	it('should init status values', function(){
		expect(scope.regvm.database).toBeDefined();
        expect(scope.regvm.isFirstOpen).toBeTruthy();
        expect(scope.regvm.initialising).toBeFalsy();
        expect(scope.regvm.loading).toBeFalsy();
	});


	it('should init database object', function(){
		$httpBackend.flush();
		expect(scope.regvm.database).toEqual(db);
	});
	
	it('should init repositories', function(){
		$httpBackend.flush();
		var cacheId = "repo-"+db.id;
		expect(scope.regvm.initialising).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = repos.resources.map(function(r){ return r.repository; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-repositories', cacheId);
	});
	
	it('should init observatories', function(){
		$httpBackend.flush();
		var cacheId = "obs-"+db.id;
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = obs.resources.map(function(r){ return r.observatory; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-observatories');
		expect(scope.$broadcast).toHaveBeenCalledWith('update-observatories', cacheId);
	});
	
    it('should load instruments', function(){
		var elems = obs.resources.map(function(r){ return r.observatory; })
		var cacheId = "instr-"+elems[0].resourceId;
		scope.regvm.getInstrument(elems[0]);
		$httpBackend.flush();
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = instr.resources.map(function(r){ return r.instrument; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-instruments', elems[0]);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-instruments', cacheId);
	});
	 
    it('should load numericaldata', function(){
		var elems = instr.resources.map(function(r){ return r.instrument; })
		var cacheId = "data-"+elems[0].resourceId;
		scope.regvm.getNumericalData(elems[0]);
		$httpBackend.flush();
		expect(scope.regvm.loading).toBeFalsy();
		expect(scope.regvm.registryService.cachedElements[cacheId]).toBeDefined();
		var cacheElem = data.resources.map(function(r){ return r.numericalData; });
		expect(scope.regvm.registryService.cachedElements[cacheId]).toEqual(cacheElem);
		expect(scope.$broadcast).toHaveBeenCalledWith('clear-numerical-data', elems[0]);
		expect(scope.$broadcast).toHaveBeenCalledWith('update-numerical-data', cacheId);
	});

	it('should close or dismiss the modal', function () {
	    scope.regvm.saveRegistry(true);
	    expect(modalInstance.close).toHaveBeenCalled();
	    scope.regvm.saveRegistry(false);
	    expect(modalInstance.dismiss).toHaveBeenCalled();
	});
	
	
}); 

