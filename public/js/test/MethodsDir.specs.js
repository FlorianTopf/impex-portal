'use strict'

describe('MethodsDir', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var $compile, scope, timeout, simDb, obsDb, mService, uService, element, $httpBackend;
	
	beforeEach(module('templates'));
	
	beforeEach(angular.mock.module('portal'));
		
	beforeEach(angular.mock.inject(function(_$compile_, $rootScope, $timeout, 
			methodsService, userService, _$httpBackend_) {
		
		$compile = _$compile_;
		scope = $rootScope.$new();
		mService = methodsService;
		uService = userService;
		$httpBackend = _$httpBackend_;
		
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
        spyOn(scope, '$on').andCallThrough();
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		simDb = getJSONFixture('simDatabase.json');
		obsDb = getJSONFixture('obsDatabase.json');
		
		
		// test element 
		element = angular.element('<methods-dir db="{{db}}"></methods-dir>');
		
	}));
		
	function createDirective(){
		var template = $compile(element)(scope);
		// setting the db attribute
		scope.db = simDb.id;
		scope.$digest();
		return template;
	}	
		
	it('should render the template and init variables', function(){
		var template = createDirective();
		var templateAsHtml = template.html();
		expect(templateAsHtml).toContain('col-sm-12');
		expect(scope.methdirvm.repositoryId).toEqual(simDb.id);
		expect(scope.methdirvm.method).toBeNull();
		expect(scope.methdirvm.votableRows).toEqual([]);
		expect(scope.methdirvm.votableColumns).toBeNull();
		expect(scope.methdirvm.votableMetadata).toEqual([]);
		expect(scope.methdirvm.selected).toEqual([]);
		expect(scope.methdirvm.methodsService).toEqual(mService)
		expect(scope.methdirvm.userService).toEqual(uService)
	});	
	
	// @TODO finalise at least triggers
	/* it('should react on set-method-active', function(){

		
	});
	
	it('should react on reset-method-request', function(){
		
		
	});
	
	it('should react on apply-selection', function(){
		
		
	});
	
	it('should react on apply-votable', function(){
		
		
	});*/
		
	
	// @TODO add specs for public methods (dom manipulation)
		
		
});