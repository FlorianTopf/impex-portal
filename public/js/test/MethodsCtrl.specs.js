'use strict'

describe('MethodsCtrl on Simulations', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, window, timeout, cService, mService, uService, state, modalInstance, 
		repoId, db, methods, apiDocs, result, $httpBackend;
	
	beforeEach(module('templates'));
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $window, $timeout, configService, 
			methodsService, userService, $state, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		window = $window;
		timeout = $timeout;
		cService = configService;
		mService = methodsService;
		uService = userService
		state = $state;
		// create a mock object using spies
	    modalInstance = {                 
	    	close: jasmine.createSpy('modalInstance.close'),
	    	dismiss: jasmine.createSpy('modalInstance.dismiss'),
	    	result: {
	    	   then: jasmine.createSpy('modalInstance.result.then')
	    	}
	    }
		$httpBackend = _$httpBackend_;
		
		// spying on broadcast events
        spyOn(scope, '$broadcast');
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		db = getJSONFixture('simDatabase.json');
		methods = getJSONFixture('simMethods.json');
		apiDocs = getJSONFixture('methods.json');
		result = getJSONFixture('methodResult.json');
		
		// just mocking some methods
		cService.getDatabase = function(id){ return db; }
		mService.getMethods = function(db){ return methods; }
		// just mocking the method stored in sessionstorage
		uService.sessionStorage.methods = getJSONFixture('sessionMethod.json');
		// just mocking a userId
		uService.createId = function(){ return '1234'; }
		// just mocking a user
		uService.user = {
				results: []
		}
		
		// state init needs empty responses here
		$httpBackend.when('GET', '/config?fmt=json').respond('');
		$httpBackend.when('GET', '/userdata').respond('');
		$httpBackend.when('GET', '/filter/region').respond('');
		// serving the api docs
		$httpBackend.when('GET', '/api-docs/methods').respond(apiDocs);
		// serving a result
		$httpBackend.when('GET', '/methods/FMI/getDataPointValue?id=spase:%2F%2FIMPEX%2FNumericalOutput%2F'+
				'FMI%2FHYB%2Fmars%2Fspiral_angle_runset_20130607_mars_20deg%2FMag&interpolation_method=Linear&'+
				'output_filetype=VOTable&votable_url=http:%2F%2Fimpex-fp7.fmi.fi%2Fws_tests%2Finput%2FgetDataPointValue_input.vot').respond(result);
		
		$controller('methodsCtrl', {$scope: scope, $window: window, $timeout: timeout, configService: cService, 
			methodsService: mService, userService: uService, $state: state, $modalInstance: modalInstance, id: db.id});
	}));
	
	it('should inject services', function(){
		expect(scope.methvm.window).toBeDefined();
		expect(scope.methvm.timeout).toBeDefined();
		expect(scope.methvm.configService).toBeDefined();
		expect(scope.methvm.methodsService).toBeDefined();
		expect(scope.methvm.userService).toBeDefined();
		expect(scope.methvm.state).toBeDefined();
		expect(scope.methvm.modalInstance).toBeDefined();
	});
	
	it('should init tooltip', function(){
		expect(scope.methvm.methodsTooltip).toBeDefined();
	});
	
	it('should init status values', function(){
		expect(scope.methvm.database).toBeDefined();
		expect(scope.methvm.methods).toBeDefined();
        expect(scope.methvm.status).toEqual('');
        expect(scope.methvm.initialising).toBeTruthy();
        expect(scope.methvm.showError).toBeFalsy();
        expect(scope.methvm.dropdownStatus.isOpen).toBeFalsy();
        expect(scope.methvm.dropdownStatus.active).toEqual('Choose Method');
       
	});

	it('should init database object, methods and active method', function(){
		spyOn(scope.methvm, 'setActive').andCallThrough();
		$httpBackend.flush();
        expect(scope.methvm.status).toEqual('success');
        expect(scope.methvm.initialising).toBeFalsy();
        expect(scope.methvm.showError).toBeFalsy();
		expect(scope.methvm.database).toEqual(db);
		expect(scope.methvm.methodsService.methods.api).toEqual(apiDocs.api);
		// there is a mock session storage active
		expect(scope.methvm.setActive).toHaveBeenCalledWith(methods[3]);
		expect(scope.methvm.dropdownStatus.active).toEqual('getDataPointValue');
		scope.methvm.timeout.flush();
		expect(scope.$broadcast).toHaveBeenCalledWith('set-method-active', methods[3]);
		expect(scope.methvm.isActive('getDataPointValue')).toBeTruthy();
		expect(scope.methvm.getActive()).toEqual('getDataPointValue');
	});
	
	it('should submit active method with default parameters', function(){
		spyOn(scope.methvm.methodsService, 'notify');
		$httpBackend.flush();
		scope.methvm.setActive(methods[3]);
		scope.methvm.submitMethod();
		$httpBackend.flush();
		expect(scope.$broadcast).toHaveBeenCalledWith('update-results', '1234');
		expect(scope.methvm.methodsService.notify).toHaveBeenCalled();
	});
	
	it('should close or dismiss the modal', function () {
	    scope.methvm.saveMethods(true);
	    expect(modalInstance.close).toHaveBeenCalled();
	    scope.methvm.saveMethods(false);
	    expect(modalInstance.dismiss).toHaveBeenCalled();
	});
	
	
});  

