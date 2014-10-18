'use strict'

describe('MethodsDir', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var $compile, scope, timeout, simDb, obsDb, apiDocs, data, output, mService, uService, 
		element, template, $httpBackend;
	
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
		apiDocs = getJSONFixture('methods.json');
		data = getJSONFixture('numericaldata.json');
		output = getJSONFixture('numericaloutput.json');
		
		// test element 
		element = angular.element('<methods-dir db="{{db}}"></methods-dir>');
		// compiled template
		template = createDirective();
	}));
		
	function createDirective(){
		var template = $compile(element)(scope);
		// setting the db attribute
		scope.db = simDb.id;
		scope.$digest();
		return template;
	}	
		
	it('should render the template and init variables', function(){
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
	
	// @TODO add special cases here (e.g. VOTableURL method)
	it('should react on set-method-active', function(){
		// 20 = FMI method
		scope.$broadcast('set-method-active', apiDocs.apis[20]);
		console.log(apiDocs.apis[20].path);
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.methdirvm.method).toEqual(apiDocs.apis[20]);
		var parameters = scope.methdirvm.method.operations[0].parameters;
		parameters.forEach(function(p) {
			expect(scope.methdirvm.request[p.name]).toEqual(p.defaultValue);
		});
		expect(scope.methdirvm.userService.sessionStorage.methods[simDb.id]).toEqual({
			path: apiDocs.apis[20].path,
			params: scope.methdirvm.request
		});
		// id is the case here
		expect(scope.$broadcast).toHaveBeenCalledWith(
				'set-applyable-elements', parameters[0].description);
		// votable_url is the case here
		expect(scope.$broadcast).toHaveBeenCalledWith(
				'set-applyable-votable', true);
	});
	
	it('should react on reset-method-request', function(){
		// just faking set-active broadcast
		scope.methdirvm.method = apiDocs.apis[20];
		// just faking a request change
		scope.methdirvm.request['id'] = 'blah';
		//scope.$digest();
		scope.$broadcast('reset-method-request');
		expect(scope.$on).toHaveBeenCalled();
		var parameters = scope.methdirvm.method.operations[0].parameters;
		parameters.forEach(function(p) {
			expect(scope.methdirvm.request[p.name]).toEqual(p.defaultValue);
		});
		expect(scope.methdirvm.votableColumns).toBeNull();
		expect(scope.methdirvm.votableRows).toEqual([]);
		expect(scope.methdirvm.votableMetadata).toEqual([]);
		expect(scope.methdirvm.selected).toEqual([]);
		expect(scope.methdirvm.userService.sessionStorage.methods[simDb.id].params)
			.toEqual(scope.methdirvm.request);
	});
	
	it('should react on apply-selection of observations', function(){
		// just faking set-active broadcast
		// 33 = AMDA method
		scope.methdirvm.method = apiDocs.apis[33];
		// faking repository id
		scope.methdirvm.repositoryId = 'spase://IMPEX/Repository/AMDA';
		console.log(apiDocs.apis[33].path)
		var fakeData = data.resources[0].numericalData;
		var fakeKeys = fakeData.parameter.map(function(p){ return p.parameterKey; });
		scope.$broadcast('apply-selection', fakeData.resourceId, fakeKeys);
		expect(scope.$on).toHaveBeenCalled();
		//scope.$digest();
		expect(scope.methdirvm.request['parameterId']).toEqual(fakeKeys[0]);
		// 2 = parameterId
		expect(scope.methdirvm.method.operations[0].parameters[2]['enum']).toEqual(fakeKeys);
	});

	it('should react on apply-selection of simulations', function(){
		// just faking set-active broadcast
		// 20 = FMI method
		scope.methdirvm.method = apiDocs.apis[20];
		var fakeOutput= output.resources[0].numericalOutput;
		var fakeKeys = fakeOutput.parameter.map(function(p){ return p.parameterKey; });
		scope.$broadcast('apply-selection', fakeOutput.resourceId, fakeKeys);
		expect(scope.$on).toHaveBeenCalled();
		//scope.$digest();
		expect(scope.methdirvm.request['variable']).toEqual(fakeKeys.join(','));
		expect(scope.methdirvm.request['id']).toEqual(fakeOutput.resourceId)
	});
	
	it('should react on apply-votable', function(){
		// just faking set-active broadcast
		scope.methdirvm.method = apiDocs.apis[20];
		var url = 'http://localhost:9000/userdata/getDPS_FMI-53eea35b30048e2c8d9f344a.xml';
		scope.$broadcast('apply-votable', url)
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.methdirvm.request['votable_url']).toEqual(url);
	});
		
	it('should update request in session on user action', function(){
		// just faking set-active broadcast
		scope.methdirvm.method = apiDocs.apis[20];
		var resourceId = output.resources[0].numericalOutput.resourceId
		scope.methdirvm.request['id'] = resourceId
		scope.methdirvm.updateRequest('id');
		expect(scope.methdirvm.userService.sessionStorage.methods[scope.methdirvm.repositoryId].params['id'])
			.toEqual(scope.methdirvm.request['id']);
	});

	it('should update request date in sesson on user action', function () {
		// just faking set-active broadcast
		scope.methdirvm.method = apiDocs.apis[20];
		var time = "Mon Jan 15 1996 01:00:00 GMT+0100 (CET)";
		var iso = new Date(time);
		scope.methdirvm.request['start_time'] = time;
		scope.methdirvm.updateRequestDate('start_time');
		expect(scope.methdirvm.userService.sessionStorage.methods[scope.methdirvm.repositoryId].params['start_time'])
			.toEqual(moment(iso).format());
	});

	
	// @FIXME refresh votableheader not testable!
	it('should refresh/update votable header on user action', function(){
		scope.methdirvm.votableColumns = 1;
		scope.methdirvm.refreshVotableHeader();
		expect(scope.methdirvm.votableMetadata.length).toBe(1);
		expect(scope.methdirvm.votableRows.length).toBe(1);
		expect(scope.methdirvm.request['Fields']).toBeDefined();
		expect(scope.methdirvm.request['Fields'].length).toBe(1);
		//scope.methdirvm.selected = [{name:'name', value:'test'}];
		//console.log(scope.methdirvm.selected[0]);
		//scope.methdirvm.updateVotableHeader(0);
		//console.log(JSON.stringify(scope.methdirvm.votableMetadata[0]));
	});


	it('should add/remove votable row on user action', function(){
		scope.methdirvm.votableColumns = 1;
		scope.methdirvm.refreshVotableHeader();
		scope.methdirvm.addVotableRow();
		expect(scope.methdirvm.votableRows.length).toBe(2);
		// must be as long as columns
		expect(scope.methdirvm.request['Fields'].length).toBe(1);
		// must be as long as rows
		expect(scope.methdirvm.request['Fields'][0].data.length).toBe(2);
		scope.methdirvm.deleteVotableRow();
		expect(scope.methdirvm.votableRows.length).toBe(1);
		expect(scope.methdirvm.request['Fields'][0].data.length).toBe(1);
	})


	it('should add/remove votable column on user action', function(){
		scope.methdirvm.votableColumns = 1;
		scope.methdirvm.refreshVotableHeader();
		scope.methdirvm.addVotableColumn();
		scope.methdirvm.addVotableRow();
		scope.methdirvm.addVotableRow();
		expect(scope.methdirvm.votableColumns).toBe(2);
		scope.methdirvm.deleteVotableColumn();
		expect(scope.methdirvm.votableColumns).toBe(1);
		expect(scope.methdirvm.request['Fields'].length).toBe(1);
		expect(scope.methdirvm.request['Fields'][0].data.length).toBe(3);
	});

	it('should reset votable on user action', function(){
		scope.methdirvm.votableColumns = 1;
		scope.methdirvm.refreshVotableHeader();
		scope.methdirvm.addVotableColumn();
		scope.methdirvm.addVotableColumn();
		scope.methdirvm.addVotableRow();
		scope.methdirvm.addVotableRow();
		expect(scope.methdirvm.request['Fields'].length).toBe(3);
		expect(scope.methdirvm.request['Fields'][0].data.length).toBe(3);
		scope.methdirvm.resetVotable();
        expect(scope.methdirvm.votableColumns).toEqual(null);
        expect(scope.methdirvm.votableRows).toEqual([]);    
        expect(scope.methdirvm.votableMetadata).toEqual([]);
        expect(scope.methdirvm.selected).toEqual([]);
        expect(scope.methdirvm.request['Fields']).toEqual([]);
	});
		
		
});