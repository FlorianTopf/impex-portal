'use strict'

describe('RegistrySvc', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, resource, rService, cfg, uData, regs, repos, models, runs, outputs, granule, $httpBackend, $q;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, registryService, _$httpBackend_) {
		scope = $rootScope;
		rService = registryService;
		$httpBackend = _$httpBackend_;
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		cfg = getJSONFixture('config.json');
		uData = getJSONFixture('userData.json');
		repos = getJSONFixture('simRepository.json');
		
		$httpBackend.when('GET', '/config?fmt=json').respond(cfg);
		$httpBackend.when('GET', '/userdata').respond(uData);
		$httpBackend.when('GET', '/filter/region').respond(regs);
		$httpBackend.when('GET', '/registry/repository?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FFMI%2FHYB').respond(repos);
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');
	}));
	
	
	it('should inject services', function(){
		expect(rService.scope).toBeDefined();
		expect(rService.resource).toBeDefined();
	});
	
	it('should set initial variables', function(){
		expect(rService.url).toEqual('/');
		expect(rService.isFilterSet).toBeFalsy();
		expect(rService.selectedFilter).toBeDefined();
		expect(rService.cachedElements).toBeDefined();
		expect(rService.selectables).toBeDefined();
		expect(rService.selectables['spase://IMPEX/Repository/FMI/HYB']).toEqual(['NumericalOutput' ]);
		expect(rService.selectables['spase://IMPEX/Repository/FMI/GUMICS']).toEqual(['NumericalOutput']);
		expect(rService.selectables['spase://IMPEX/Repository/LATMOS']).toEqual(['SimulationRun','NumericalOutput']);
		expect(rService.selectables['spase://IMPEX/Repository/SINP']).toEqual(['SimulationModel','NumericalOutput']);
	});

	it('should fetch repository', function(){
		var promise = rService.Repository().get({ fmt: 'json' , id: 'spase://IMPEX/Repository/FMI/HYB' }).$promise;
		promise.then(function(r){
			expect(r.resources).toEqual(repos.resources)
		});
		$httpBackend.flush();
	});
	
	// @TODO test the rest of resources (maybe not needed)
	
	it('notify system on selection save', function(){
		var id = 'spase://IMPEX/Repository/FMI/HYB';
		rService.notify('success', id);
		expect(scope.$broadcast).toHaveBeenCalledWith('database-success', id);
	});
	
	
});